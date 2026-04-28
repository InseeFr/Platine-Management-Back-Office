# Skill : Contexte Projet — Platine Pilotage Back Office

> Source unique sur l'environnement opérationnel : versions, modules,
> commandes build, configuration, profils, Liquibase, Docker, conventions.
> Dernière MAJ : 2026-04-28.

## Identité du projet

- **Nom Maven** : `platine-pilotage` (groupId `fr.insee.survey`)
- **Repo** : `Platine-Management-Back-Office3`
- **Description** : Back-office REST API pour la gestion de la collecte
  d'enquêtes (communication entre la BD et les UI Platine-pilotage /
  Platine-My-Surveys).
- **Branche par défaut** : `develop`
- **Branche en cours** (migration SB4) : `refactor-spring-boot4-mistral`

## Versions

| Composant | Version actuelle | Cible migration |
|---|---|---|
| Java | **25** | 25 (déjà conforme) |
| Spring Boot | **3.5.13** | **4.0.x** (cf. `rules.md`) |
| Spring Framework | 6 (transitif) | 7 |
| Spring Security | 6 (transitif) | 7 |
| Hibernate ORM | 6.x | 7 (Jakarta Persistence 4) |
| Jackson | 2.21.1 (`com.fasterxml.jackson.*`) | 3.0.x (`tools.jackson.*`) |
| ModelMapper | 3.2.5 | 3.2.5 (à valider Jackson 3 / Hibernate 7) |
| Lombok | 1.18.42 | inchangé |
| PostgreSQL (driver) | 42.7.x | inchangé |
| Tomcat (override) | 10.1.54 | retirer l'override (baseline 11.x via SB4) |
| Maven | via wrapper `./mvnw` | idem |

> La migration Spring Boot 3.5 → 4.0 est documentée dans `rules.md` (à la
> racine du repo) et son plan d'application dans
> `PLAN_MIGRATION_SPRING_BOOT_4_PLATINE.md`. Toute évolution de version
> doit s'aligner sur ces documents.

## Package racine

`fr.insee.survey.datacollectionmanagement` (historique — conservé par
compatibilité, ne pas renommer).

## Modules Maven

```
platine-pilotage/                       (parent — packaging pom)
├── platine-pilotage-shared              # DTOs, enums, validation, constants
├── platine-pilotage-db                  # Entités JPA, repositories, DAOs JdbcTemplate
├── platine-pilotage-service             # Logique métier (services + impl)
└── platine-pilotage-api                 # REST controllers, config Spring, sécurité, Swagger
```

(Pas de module dédié coverage ; JaCoCo est configuré au niveau du parent
via le profil `coverage`.)

Sens de dépendance : `api → service → db → shared`. Aucune dépendance
inverse — voir `platine-arch-state.md`.

## Commandes Build & Run

```bash
# Build complet (tous modules + tous tests Surefire + Failsafe)
./mvnw clean verify
# Windows :
mvnw.cmd clean verify

# Build sans tests
./mvnw clean install -DskipTests

# Build d'un module spécifique (avec ses dépendances amont)
./mvnw clean install -pl platine-pilotage-service -am

# Tests unitaires uniquement (Surefire — pattern *Test.java)
./mvnw test

# Tests d'intégration uniquement (Failsafe — *IT.java + **/integration/**/*)
./mvnw verify -DskipUnitTests=false  # inclus dans verify par défaut

# Test d'une classe spécifique
./mvnw -Dtest=ContactServiceImplTest test
./mvnw -Dit.test=ContactIT verify

# Lancer l'API localement
./mvnw -pl platine-pilotage-api -am spring-boot:run

# Couverture JaCoCo (profil coverage)
./mvnw -P coverage verify
# Rapport agrégé : platine-pilotage-api/target/site/jacoco-aggregate/jacoco.xml

# Build JAR sans tests
./mvnw clean package -DskipTests

# Sonar (CI uniquement, voir sonar-project.properties)
./mvnw verify sonar:sonar
```

## Configuration & Profils

### Fichiers de configuration

| Fichier | Emplacement | Usage |
|---|---|---|
| `application.properties` | `platine-pilotage-api/src/main/resources/` | Config principale (`auth.mode=noauth` par défaut) |
| `application-compose.properties` | `platine-pilotage-api/src/main/resources/` | Profil Docker local (Keycloak + Postgres conteneurisés) |
| `application.properties` | `platine-pilotage-api/src/test/resources/` | Tests (H2 in-memory, mode OIDC mockée) |
| `application-demo.properties` | `platine-pilotage-api/src/test/resources/` | Démo / E2E avec Keycloak local |

### Propriétés sensibles à connaître

| Propriété | Rôle |
|---|---|
| `auth.mode` | `OIDC` ou `noauth`. Active une `SecurityFilterChain` distincte (cf. `security.md`) |
| `auth.server-url` / `auth.realm` | Endpoint Keycloak |
| `jwt.role-claim` / `jwt.id-claim` | Claims JWT mappés vers rôles / nom utilisateur |
| `roles.<role>.role` | Mapping nom de rôle JWT → `AuthorityRoleEnum` |
| `public.urls` | Liste des URL publiques (Swagger, healthcheck, etc.) |
| `cors.allowedOrigins` | Origines CORS autorisées |
| `spring.liquibase.enabled` | **`false` par défaut** — activer en local pour initialiser la DB |
| `spring.liquibase.contexts` | `prod` / `initdb,prod` selon que la DB existe ou non |
| `questioning.api.url`, `lunatic.*.url`, `xform*.url` | URL des back-ends d'enquête |
| `ldap.api.*` | Connexion LDAP externe |
| `allow-force-delete` | Garde-fou suppression forcée campagnes |

> **Ne jamais commiter de secrets.** Mettre les credentials dans un
> `.env` local (cf. `platine-pilotage-api/.env` / `compose.yml`) ou via
> variables d'environnement. Pre-commit Gitleaks bloque les fuites.

## Base de Données & Liquibase

### Emplacement des changelogs

```
platine-pilotage-db/src/main/resources/db/
├── master.xml                  # Point d'entrée principal (contexte prod)
├── e2e.xml                     # Données pour tests E2E
├── integration-demo.xml        # Données pour démo / intégration
├── changelog/                  # Changesets versionnés (NNN_description.xml)
└── scripts/                    # Scripts SQL ad-hoc
```

### Convention de nommage et règles

- Préfixe numérique croissant : `NNN_description.xml`.
- **Ne jamais modifier un changelog déjà appliqué en prod** — créer un
  nouveau fichier et l'ajouter au `master.xml`.
- Liquibase **est désactivé par défaut** (`spring.liquibase.enabled=false`).
  Activer manuellement en local au premier démarrage avec une DB neuve :
  `spring.liquibase.enabled=true` + `spring.liquibase.contexts=initdb,prod`.
- En migration Spring Boot 4, prévoir d'aligner les colonnes timestamp en
  `timestamptz` (cf. `rules.md` §4 — passage `Date` → `Instant` UTC).

## Suites de Tests

| Pattern / Classe | Type | Module(s) |
|---|---|---|
| `*Test.java` | Unitaire (Surefire) | Tous |
| `*IT.java` | Intégration (Failsafe) | Principalement `platine-pilotage-api` |
| `**/integration/**/*` | Intégration (Failsafe) | `platine-pilotage-api` (Cucumber) |
| `*SecurityTest.java` | Tests de sécurité MockMvc + JWT post-processor | `platine-pilotage-api` |

### Cucumber (BDD)

Features dans `platine-pilotage-api/src/test/resources/integration/query/` :

- `campaign.feature`
- `context.feature`
- `survey.feature`
- `survey_unit.feature`
- `search_contact.feature`
- `search_questioning.feature`
- `search_survey_unit.feature`
- `get_questioning_informations.feature`

Données associées sous `integration/query/data/`.

### Outils de test disponibles

- JUnit 5 (Jupiter)
- AssertJ 3.27.7 (forcé via dependencyManagement)
- Spring Test + `MockMvc`
- `spring-security-test` pour `@WithMockUser` et `jwt()` post-processor
- Cucumber (BDD)
- H2 in-memory pour les tests d'intégration sans Docker
- WireMock disponible pour mocker les API HTTP externes

## Infrastructure Locale (Docker)

```
platine-pilotage-api/
├── Dockerfile                  # Image de l'API
├── compose.yml                 # Stack locale (Keycloak + PostgreSQL)
├── .env                        # Variables d'environnement locales (NON commité)
└── container/
    └── keycloak/realms/
        └── platine.json        # Realm Keycloak avec users de test
```

### Lancer la stack locale

```bash
# Démarrer Keycloak + Postgres en arrière-plan
cd platine-pilotage-api
docker compose up -d

# Lancer l'API connectée à la stack (profil compose)
./mvnw spring-boot:run -Dspring-boot.run.profiles=compose
```

Utilisateurs Keycloak prédéfinis (cf. `platine.json`) :

| Login | Mot de passe | Rôle métier |
|---|---|---|
| `gestio1` | (cf. realm) | `gestionnaire` → `INTERNAL_USER` |
| `respon1` | (cf. realm) | `respondent` → `RESPONDENT` |
| `E2E_RESPON_*` | (cf. realm) | utilisateurs de test E2E |

## Conventions de Code

### Langue

- **Code, identifiants, Javadoc, exceptions, commits techniques en anglais.**
- **Prompts et documentation `.clinerules` en français.**

### Nommage

- Package racine : `fr.insee.survey.datacollectionmanagement`
- Classes : `UpperCamelCase`
- Méthodes / champs : `lowerCamelCase`
- Constantes : `UPPER_SNAKE_CASE`
- Indentation : 4 espaces, encodage **UTF-8**, fin de lignes LF.
- Respecter le nommage existant **même si historiquement incorrect**
  (ex. typos figées dans certains packages — ne pas renommer dans un PR
  fonctionnel).

### Lombok

Activé : préférer `@RequiredArgsConstructor`, `@Getter`, `@Setter`,
`@Data` (parcimonieusement) à du boilerplate manuel. **Pas de
`@Autowired` sur champ** — l'injection passe par le constructeur que
Lombok génère.

### Tests

- Unitaires déterministes (pas d'I/O réel sauf marquage explicite).
- Mocker les systèmes externes (HTTP, LDAP, Keycloak hors profile `auth`).
- Préférer H2 + `@DataJpaTest` pour les tests de repository.
- Tests de controllers : `MockMvc` + `@WithMockUser` ou
  `jwt().authorities(role::securityRole)` post-processor.

### Règles de contribution

- **Conventional Commits obligatoires** :
  `feat:`, `fix:`, `chore:`, `docs:`, `test:`, `refactor:`, `perf:`,
  `style:`, `build:`, `ci:`.
  Un pre-commit hook valide le format.
- **Un commit = un changement focalisé** — pas de mélange `refactor` +
  `feat` dans un même commit.
- **Référencer le ticket** dans la PR (`Closes #123`).
- **Documenter** les changements d'API (`@Operation` Swagger + exemples
  request/response si non triviaux).
- **README et properties** mis à jour si comportement applicatif change.
- Avant de pousser : `./mvnw clean verify` doit passer en local.

### Hooks pre-commit & CI

- `pre-commit-config.yaml` :
  - validation Conventional Commits
  - Gitleaks (scan secrets)
  - Trivy FS scan (pre-push, optionnel)
- Installation : `pre-commit install --hook-type commit-msg --hook-type pre-push`
- `.gitleaks.toml` + `.gitleaksignore` pour les exceptions documentées.
- `sonar-project.properties` : la CI exécute Sonar sur tous les PR.

## Documents de référence dans le repo

| Document | Rôle |
|---|---|
| `README.md` | Démarrage rapide |
| `AGENTS.md` | Conventions structure & contribution (vue concise) |
| `rules.md` | Règles détaillées de migration Spring Boot 3 → 4 |
| `PLAN_MIGRATION_SPRING_BOOT_4_GENERIQUE.md` | Plan générique de migration SB4 |
| `PLAN_MIGRATION_SPRING_BOOT_4_PLATINE.md` | Plan d'exécution spécifique au repo |
| `.clinerules/context/platine-arch-state.md` | Architecture en couches (état & cible) |
| `.clinerules/context/security.md` | Stack et patterns de sécurité |
