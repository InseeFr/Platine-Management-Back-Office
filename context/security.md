# Skill : Sécurité — Platine Pilotage Back Office

## Objectif

Ce document couvre l'authentification OIDC, l'autorisation par rôle et par
permission contextuelle, la configuration Spring Security, les utilitaires
de test d'auth et les patterns à suivre côté API.

Il sert de référence pour LeCodeur, LeTesteur et LeRefactoAnalyste quand
ils touchent à un endpoint, une `SecurityFilterChain` ou un test de
contrôleur.

## Stack

| Composant | Version | Notes |
|---|---|---|
| Spring Security | 6.x (transitif Spring Boot 3.5.13) → cible **7** en SB4 | |
| Protocole | OAuth2 Resource Server + JWT | bearer token |
| Fournisseur d'identité (local) | Keycloak 24.0 | via `compose.yml` |
| Modes d'auth | `OIDC` (prod) / `noauth` (dev/test) | `auth.mode` property |
| Module | `platine-pilotage-api` (`configuration/auth/`) | toute la config sécurité |

> Spring Security est déclaré explicitement via les starters
> `spring-boot-starter-oauth2-resource-server`, `spring-boot-starter-security`
> et `spring-security-oauth2-client` dans `platine-pilotage-api/pom.xml`.
> En migration SB4, ces starters seront renommés (cf. `rules.md` §1.2).

## Modèle de rôles

Les rôles métier sont définis dans un enum **partagé** :

```java
// fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum
public enum AuthorityRoleEnum {
    ADMIN,
    INTERNAL_USER,
    WEB_CLIENT,
    RESPONDENT,
    PORTAL,
    READER,
    SUPPORT;

    public static final String ROLE_PREFIX = "ROLE_";

    public String securityRole() { return ROLE_PREFIX + name(); }
    public static AuthorityRoleEnum fromSecurityRole(String role) { … }
}
```

Localisation : `platine-pilotage-shared/src/main/java/fr/insee/survey/datacollectionmanagement/constants/AuthorityRoleEnum.java`.

Mapping JWT → rôle assuré par `ProfiledAuthenticationConverter`
(`platine-pilotage-api`). Les noms de rôles attendus dans le JWT sont
configurables via les propriétés `roles.<role>.role` :

```properties
roles.admin.role=admin
roles.internal.user.role=internalUser
roles.webclient.role=webclient
roles.respondent.role=respondent
roles.portal.role=portail_Platine
roles.reader.role=consultation
roles.support.role=support
```

Le claim source des rôles dans le JWT est lui-même configurable
(`jwt.role-claim`). À défaut, le converter retombe sur `realm_access.roles`
(claim Keycloak standard).

## Configuration Spring Security (cible)

### Trois `SecurityFilterChain` complémentaires

Toutes définies dans
`platine-pilotage-api/src/main/java/fr/insee/survey/datacollectionmanagement/configuration/auth/security/` :

| Classe | Profil / Condition | Order | Rôle |
|---|---|---|---|
| `PublicSecurityFilterChain` | aucune (toujours actif) | `@Order(1)` | Whitelist d'URLs publiques (Swagger, healthcheck, actuator) |
| `OpenIDConnectSecurityContext` | `@ConditionalOnProperty(name="auth.mode", havingValue="OIDC")` | `@Order(2)` | Chaîne OIDC / JWT (mode prod & test) |
| `NoAuthSecurityContext` | `@ConditionalOnProperty(name="auth.mode", havingValue="noauth")` | `@Order(2)` | `permitAll` + anonyme `ROLE_ADMIN` (dev local) |

### Règles clés de la chaîne OIDC

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "auth.mode", havingValue = AuthConstants.OIDC)
public class OpenIDConnectSecurityContext {

    @Bean
    @Order(2)
    SecurityFilterChain oidcChain(HttpSecurity http,
                                  Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter)
            throws Exception {
        http
            .securityMatcher("/**")
            // API stateless : CSRF désactivé intentionnellement
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .headers(h -> h
                .xssProtection(x -> x.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED))
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'"))
                .referrerPolicy(r -> r.policy(ReferrerPolicy.SAME_ORIGIN)))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(cfg -> cfg.anyRequest().authenticated())
            .oauth2ResourceServer(o -> o
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));
        return http.build();
    }
}
```

**Invariants à préserver** :
- `SessionCreationPolicy.STATELESS` (jamais de session HTTP).
- CSRF désactivé **uniquement** parce que l'API est stateless bearer-token.
  Si un cookie de session est réintroduit, CSRF **doit** être réactivé.
- Aucune extension de la liste `public.urls` sans justification documentée.
  Liste actuelle : `/swagger-ui/**, /v3/api-docs/**, /healthcheck, /actuator/**, /, /webjars/**, /environnement, /csrf`.
- Le mode `noauth` ne doit **jamais** être activé en production.

### Headers de sécurité

| Header | Valeur | Raison |
|---|---|---|
| `Content-Security-Policy` | `default-src 'none'` | API JSON, aucun asset à charger |
| `Referrer-Policy` | `same-origin` | minimiser fuite contexte |
| `X-XSS-Protection` | désactivé | obsolète, géré par CSP |
| `Strict-Transport-Security` | hérité du défaut Spring Security | forcé HTTPS prod |

`PublicSecurityFilterChain` ajoute dynamiquement l'URL du token Keycloak
au `connect-src` du CSP pour permettre le flow OAuth2 dans Swagger UI.

## Pattern d'autorisation (cible projet)

Le projet utilise **deux mécanismes complémentaires** :

### 1. `@PreAuthorize` sur les méthodes de Controller

`@EnableMethodSecurity` est activé. Toutes les méthodes de controller
exposées sont annotées avec une **constante** issue de `AuthorityPrivileges`
(jamais de SpEL inline ad-hoc) :

```java
// platine-pilotage-api/.../configuration/auth/user/AuthorityPrivileges.java
public final class AuthorityPrivileges {
    public static final String HAS_ADMIN_PRIVILEGES =
        "hasRole('ADMIN')";
    public static final String HAS_MANAGEMENT_PRIVILEGES =
        "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'ADMIN')";
    public static final String HAS_READER_PRIVILEGES =
        "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'ADMIN', 'READER')";
    public static final String HAS_RESPONDENT_PRIVILEGES =
        "hasRole('RESPONDENT')";
    public static final String HAS_RESPONDENT_LIMITED_PRIVILEGES =
        "hasRole('RESPONDENT') && #id.toLowerCase() == authentication.name.toLowerCase()";
    public static final String HAS_PORTAL_PRIVILEGES =
        "hasAnyRole('PORTAL', 'INTERNAL_USER', 'WEB_CLIENT', 'ADMIN')";
    public static final String HAS_USER_PRIVILEGES =
        "hasAnyRole('INTERNAL_USER', 'WEB_CLIENT', 'RESPONDENT', 'ADMIN')";
    public static final String HAS_WEBCLIENT_PRIVILEGES =
        "hasRole('WEB_CLIENT')";
    public static final String HAS_ORCHESTRATOR_PROTOCOLS_PRIVILEGES =
        "hasAnyRole('WEB_CLIENT', 'ADMIN')";
    // ...
}
```

Usage type :

```java
@GetMapping("/contacts/{identifier}")
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
public ContactDto get(@PathVariable String identifier) { … }

// Restriction d'un répondant à ses propres données :
@GetMapping("/users/{id}/wallet")
@PreAuthorize(AuthorityPrivileges.HAS_RESPONDENT_LIMITED_PRIVILEGES)
public WalletDto wallet(@PathVariable String id) { … }
```

### 2. Permissions contextuelles via `PermissionEvaluator` custom

Pour les autorisations qui dépendent d'**un objet métier précis** (ex :
"l'utilisateur peut lire CETTE interrogation"), le projet utilise
`hasPermission(target, permission)` avec un `PermissionEvaluator` custom.

Localisation :
`platine-pilotage-api/.../configuration/auth/permission/evaluator/`

```java
// Interface
public interface ApplicationPermissionEvaluator<T> {
    boolean hasPermission(Authentication authentication, T target);
    Class<T> getTargetType();
    Permission getPermission();
}

// Enum des permissions
public enum Permission {
    SUPPORT_READ,                   // global (basé rôle)
    INTERROGATION_DATA_READ,        // contextuel (par interrogation)
    INTERROGATION_DATA_EDIT,        // contextuel
    INTERROGATION_DATA_EXPORT,      // contextuel
    INTERROGATION_PAPER_DATA_EDIT,  // contextuel
    INTERROGATION_EXPERT_DATA_EDIT  // contextuel
}
```

Usage type :

```java
@GetMapping("/interrogations/{interroId}/data")
@PreAuthorize("hasPermission(#interroId, 'INTERROGATION', 'READ')")
public InterrogationDataDto getData(@PathVariable String interroId) { … }

@GetMapping("/support/items")
@PreAuthorize("hasPermission(null, 'SUPPORT_READ')")
public List<SupportItemDto> supportList() { … }
```

### Ce qu'il NE faut PAS faire

- `SecurityContextHolder.getContext().getAuthentication()` en dur dans une
  classe de service. Toujours passer par `AuthenticationUserHelper`
  (composant Spring injecté, testable).
- Ajouter une nouvelle expression SpEL inline dans `@PreAuthorize` au lieu
  de l'extraire dans `AuthorityPrivileges`.
- Ajouter un `permitAll()` sur un endpoint applicatif sans justification
  documentée (et sans test associé).
- Utiliser un nouveau rôle JWT non recensé dans `AuthorityRoleEnum`.

## JWT → AuthorizationProfile (le détail)

Le converter `ProfiledAuthenticationConverter` produit un
`ProfiledAuthenticationToken` (extends `JwtAuthenticationToken`) qui
contient un `AuthorizationProfile` (record immuable) :

```java
public record AuthorizationProfile(
    Set<AuthorityRoleEnum> appRoles,
    Set<String> sources,
    Set<Permission> permissions
) {
    public boolean can(String sourceId) { … }
    public boolean hasRole(AuthorityRoleEnum role) { … }
}
```

- `appRoles` : rôles applicatifs déduits du JWT (mapping via properties).
- `sources` : sources d'enquête autorisées (filtrage de données par
  périmètre métier — utilisé par les `PermissionEvaluator` contextuels).
- `permissions` : permissions globales découlant des rôles.

Pour récupérer le profil dans une couche de permission :

```java
ProfiledAuthenticationToken token = (ProfiledAuthenticationToken) authentication;
AuthorizationProfile profile = token.getProfile();
if (profile.can(interrogation.getSourceId())) { … }
```

## Exceptions & mapping HTTP

Le mapping HTTP est centralisé dans
`platine-pilotage-api/.../exception/ExceptionControllerAdvice.java`
(`@ControllerAdvice`).

| Exception | HTTP | Source |
|---|---|---|
| `AccessDeniedException` (Spring Security) | **403** | auth |
| `AuthenticationException` (Spring Security) | **401** | auth (souvent géré par EntryPoint) |
| `NotFoundException` (métier) | 404 | métier |
| `*AlreadyExistException` / `*ConflictException` | 409 | métier |
| `MethodArgumentNotValidException` / `ConstraintViolationException` | 400 | validation |
| `HttpMessageNotReadableException` | 400 | parsing JSON |
| Fallback `Exception` | 500 | défaut |

**Règle** : une exception d'auth ne doit **jamais** remonter telle quelle
au client (fuite de stack trace) — elle doit toujours passer par
`ExceptionControllerAdvice` qui retourne un `ApiError` propre (path,
message, status, timestamp).

## Tests de sécurité

### Helpers et utilitaires partagés

Le projet utilise les utilitaires standards de `spring-security-test` :

- `SecurityMockMvcRequestPostProcessors.jwt()` pour produire un JWT
  authenticated avec rôles arbitraires.
- `@WithMockUser` pour les tests simples (≈ 6 occurrences) ; mais le
  pattern recommandé pour les nouveaux tests est `jwt().authorities(...)`
  car il reproduit fidèlement la chaîne OIDC.

Helper idiomatique présent dans plusieurs `*SecurityTest` :

```java
private static RequestPostProcessor jwtWithRole(AuthorityRoleEnum role) {
    return jwt().authorities((GrantedAuthority) role::securityRole);
}
```

### Test de sécurité MockMvc (pattern type)

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SourceControllerSecurityTest {

    @Autowired MockMvc mockMvc;

    private static RequestPostProcessor jwtWithRole(AuthorityRoleEnum role) {
        return jwt().authorities((GrantedAuthority) role::securityRole);
    }

    @Test
    void getSources_returns200ForAdmin() throws Exception {
        mockMvc.perform(get("/api/sources").with(jwtWithRole(AuthorityRoleEnum.ADMIN)))
            .andExpect(status().isOk());
    }

    @Test
    void getSources_returns403ForRespondent() throws Exception {
        mockMvc.perform(get("/api/sources").with(jwtWithRole(AuthorityRoleEnum.RESPONDENT)))
            .andExpect(status().isForbidden());
    }

    @Test
    void getSources_returns401WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/sources"))
            .andExpect(status().isUnauthorized());
    }
}
```

Ce pattern est verrouillé par convention : **tout nouveau Controller doit
être accompagné d'un `*SecurityTest`** couvrant 401 / 403 / 200.

### Profils de test

| Profil | Fichier | Usage |
|---|---|---|
| `test` (défaut) | `platine-pilotage-api/src/test/resources/application.properties` | H2 in-memory, mode OIDC mocké |
| `demo` | `application-demo.properties` | Keycloak local, données de démo |

### Scénarios de sécurité à couvrir pour tout endpoint

| Scénario | Code attendu |
|---|---|
| Utilisateur non authentifié | 401 |
| Authentifié, rôle insuffisant | 403 |
| Authentifié, rôle valide, ressource inexistante | 404 |
| Authentifié, rôle valide, happy path | 200 / 201 |
| `OPTIONS` (CORS preflight) | 200 / 204 |
| Permission contextuelle refusée (autre source / interrogation d'un autre user) | 403 |

## CORS

Configuration : `platine-pilotage-api/.../configuration/CorsGlobalConfig.java`.

```java
registry.addMapping("/**")
    .allowedOrigins(corsAllowedOrigins) // property cors.allowedOrigins
    .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
    .allowedHeaders("Authorization", "Origin", "X-Requested-With",
                    "Content-Type", "Accept", "Source")
    .maxAge(3600);
```

`cors.allowedOrigins=*` en dev/local, mais doit être **restreint** par
liste explicite en prod.

## Keycloak local

| Élément | Valeur |
|---|---|
| Image | `quay.io/keycloak/keycloak:24.0` |
| Mode | `start-dev --import-realm` |
| Realm importé | `platine.json` (`platine-pilotage-api/container/keycloak/realms/`) |
| Realm name | `platine` |
| Admin console | `http://localhost:${KEYCLOAK_PORT}` (admin/administrator) |

Utilisateurs de test (cf. realm) : `gestio1` (gestionnaire), `respon1`
(répondant), série `E2E_RESPON_*`.

## Legacy vs cible

| Aspect | Legacy / À éviter | Cible |
|---|---|---|
| Vérification de rôle | `SecurityContextHolder` direct dans un service | `AuthenticationUserHelper` injecté |
| Test d'auth | `@WithMockUser` (toléré, simple) | `jwt().authorities(role::securityRole)` (reproduit la chaîne OIDC) |
| Expression `@PreAuthorize` | SpEL inline ad-hoc | constantes `AuthorityPrivileges` |
| 401/403 retournés | `ResponseEntity.status(403)` direct depuis un controller | exception métier ou `AccessDeniedException` → `ExceptionControllerAdvice` |
| Permission contextuelle | `if (...)` dans le service | `PermissionEvaluator` custom + `hasPermission(...)` dans `@PreAuthorize` |

## Check-list sécurité pour une nouvelle feature

- [ ] Endpoint protégé par `@PreAuthorize(AuthorityPrivileges.X)` ou
      `@PreAuthorize("hasPermission(...)")` selon la nature
- [ ] Si nouvelle expression SpEL réutilisable → ajoutée dans `AuthorityPrivileges`
- [ ] Si nouvelle permission contextuelle → nouvel `ApplicationPermissionEvaluator`
      enregistré et `Permission` enum complétée
- [ ] Pas d'accès direct à `SecurityContextHolder` (passer par `AuthenticationUserHelper`)
- [ ] Test d'intégration 401 (non authentifié)
- [ ] Test d'intégration 403 (rôle insuffisant)
- [ ] Test d'intégration 200 / 201 (rôle autorisé, happy path)
- [ ] Pas de nouveau `permitAll()` ajouté à la chaîne sans justification documentée
- [ ] Session reste `STATELESS` (pas de réintroduction de session HTTP)
- [ ] CSRF reste désactivé (réactiver **uniquement** si on réintroduit une session)
- [ ] Aucun secret / token en clair dans les logs (Gitleaks check OK)
- [ ] Si nouveau rôle JWT : ajouté à `AuthorityRoleEnum` + property
      `roles.<role>.role` documentée
