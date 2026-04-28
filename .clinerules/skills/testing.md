# Skill : Testing — Spring Boot 4

## Objectif

Standards pour tests **unitaires et de contrôleur** (stratégie de doublures, services
Domain, contrôleurs MockMvc, paramétrés). Couvre ~70% des tests écrits. Pour les
tests touchant l'infrastructure réelle (adaptateurs JPA/JDBC, `@SpringBootTest`,
Cucumber, ArchUnit), voir `testing-integration.md`.

## Stack

- **JUnit 5** + **AssertJ** (exclusivement — jamais JUnit `assertEquals`)
- **Mockito** : usage restreint (voir règles ci-dessous)
- **MockMvc** : tests de contrôleurs (`standaloneSetup`)
- **JSONAssert** : comparaison JSON
- **Spring Boot Test** : intégration uniquement

## Organisation

| Type | Emplacement |
|---|---|
| Test unitaire (service, controller) | `[module]/src/test/java/.../[Feature]Test.java` |
| Test d'intégration | `.../integration/.../[Feature]IT.java` |
| Test de mapper JPA ↔ Domain | `[module]/src/test/java/.../[Mapper]Test.java` |
| Cucumber | `src/test/java/.../features/` + `src/test/resources/features/` |
| ArchUnit | `src/test/java/.../config/*ArchTests.java` |
| Fake (doublure) | `[module]/src/test/java/.../fake/[Port]Fake.java` |

**Nouveau code** : `fake/` uniquement. **Legacy** (`dummy/`, `stub/`) : migrer
quand on modifie le test, pas refondre par principe.

## Legacy vs Cible

| Aspect | Legacy | Cible |
|---|---|---|
| 404 dans contrôleur | `if (result == null) return NOT_FOUND` | Exception métier + `ExceptionControllerAdvice` |
| Tests contrôleur | Mockito + `ResponseEntity` direct | Fake + MockMvc + matcher d'erreur API |
| Assertions | JUnit `assertEquals`, `assertNull` | AssertJ `assertThat()`, `assertThatThrownBy()` |
| Nommage | `testGetEntity01()` | `shouldReturnEntityWhenExists()` |
| Port domain | `Optional<EntityDB>` (fuite JPA) | `Optional<Entity>` (modèle domain) |
| Retour service | `null` pour "pas trouvé" | Exception métier |

---

## Doublures : Fake ou Mock ?

### Critère : taille du port

Un Fake pour un port à 25+ méthodes = implémenter 80 % de méthodes inutiles
(`List.of()` ou `throw UnsupportedOperationException`), et chaque évolution du
port casse tous les Fakes. Pour un port à 1–6 méthodes, le Fake coûte ~10 lignes,
reste stable et documente le contrat.

| Quoi tester | Taille du port | Doublure |
|---|---|---|
| Service via port **≤ 6 méthodes** | Petit | **Fake** |
| Service via port **> 6 méthodes** | Gros | **Mockito** |
| Contrôleur via port entrant | Variable | **Fake** (flags/getters) |
| Adaptateur (persistence) | — | **Test d'intégration** |
| Date/temps | 1 méthode | **FixedDateService** (Fake de `DateService`) |

Seuil de 6 = heuristique. **Exception contrôleurs** : toujours Fake (même gros
ports), car flags/getters sont plus expressifs que Mockito pour codes HTTP et
états après action.

### Arbre de décision

```
Adaptateur (persistence/http) ?  → Test d'intégration
Sinon (Service ou Contrôleur) :
  Port ≤ 6 méthodes ? → Fake (package fake/)
  Sinon              → Mockito (when ; verify si paramètre métier significatif)
```

---

## Conventions des Fakes

### Fake de port sortant (repository) — in-memory

```java
public class EntityRepositoryFake implements EntityRepository {
  private final List<Entity> entities = new ArrayList<>();

  public void save(Entity e) { if (!entities.contains(e)) entities.add(e); }

  @Override
  public Optional<Entity> findById(String id) {
    return entities.stream().filter(e -> e.id().equals(id)).findFirst();
  }
}
```

### Fake de port entrant (service) — flags + getters

```java
@RequiredArgsConstructor
public class EntityServiceFake implements EntityService {
  @Getter private boolean deleted = false;
  @Setter private boolean shouldThrowNotFoundException = false;

  @Override
  public void delete(String id) throws EntityNotFoundException {
    if (shouldThrowNotFoundException) throw new EntityNotFoundException();
    deleted = true;
  }

  @Override
  public List<EntityDto> getAll() {
    throw new UnsupportedOperationException("Not used in this test");
  }
}
```

### Gestion du temps — FixedDateService

Port `DateService` (1 méthode) injecté dans les services domaine. Fake renvoie un
timestamp constant — évite `LocalDate.now()` / `Instant.now()` dans le code testé.

---

## Tests par Couche

### Service Domain — Fake (port petit)

```java
class EntityServiceImplTest {
  private EntityRepositoryFake repository;
  private EntityServiceImpl service;

  @BeforeEach void setUp() {
    repository = new EntityRepositoryFake();
    service = new EntityServiceImpl(repository);
  }

  @Test @DisplayName("Should throw when updating non-existent entity")
  void shouldThrowWhenUpdatingNonExistent() {
    assertThatThrownBy(() -> service.update(unknown))
            .isInstanceOf(EntityNotFoundException.class);
  }
}
```

### Service Domain — Mockito (port gros)

```java
class ReportingServiceTest {
  EntityRepository repository = mock(EntityRepository.class);  // 28 méthodes → mock
  ReportingService service = new ReportingService(repository);

  @Test void shouldCallRepositoryWithCorrectId() {
    service.process("itw-123");
    verify(repository).getStatsFor(eq("itw-123"), any()); // paramètre métier
  }
}
```

### Contrôleur (MockMvc + Fake)

```java
class EntityControllerTest {
  private MockMvc mockMvc;
  private EntityServiceFake service;

  @BeforeEach void setup() {
    service = new EntityServiceFake();
    mockMvc = MockMvcBuilders.standaloneSetup(new EntityController(service))
            .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
            .build();
  }

  @Test void shouldReturnNotFoundWhenMissing() throws Exception {
    service.setShouldThrowNotFoundException(true);
    mockMvc.perform(get("/api/entity/id"))
            .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, ...));
  }

  @Test void shouldDelete() throws Exception {
    mockMvc.perform(delete("/api/entity/id")).andExpect(status().isOk());
    assertThat(service.isDeleted()).isTrue(); // état du Fake, pas verify
  }
}
```

`standaloneSetup` ne déclenche **pas** Spring Security. Pour 401/403 réels,
voir `testing-integration.md`.

### Tests Paramétrés

```java
@ParameterizedTest @MethodSource("provideScenarios")
void shouldComputeState(Input in, Expected expected) {
  assertThat(service.compute(in)).isEqualTo(expected);
}
```

---

## Conventions (nouveau code)

- **Nommage** : `shouldXxxWhenYyy` + `@DisplayName`
- **Assertions** : AssertJ exclusivement
- **Pattern** : Given/When/Then
- **Doublures** : Fake (`fake/`) si port ≤ 6 méthodes, Mockito sinon
- **Contrôleurs** : toujours Fake + MockMvc
- **Adaptateurs** : test d'intégration
- **Temps** : `FixedDateService` (Fake du port `DateService`)
- **Pas de** : `@Disabled`, `@Ignore`, `@Order`, état partagé entre tests

## Couverture — Checklist par Feature

- [ ] Cas nominal (happy path)
- [ ] Chaque exception métier → `assertThatThrownBy`
- [ ] Cas limites : null, empty, liste vide
- [ ] Branches conditionnelles
- [ ] Codes HTTP : 200, 400, 404, 409
- [ ] JSON comparé avec JSONAssert pour réponses complexes
- [ ] État du Fake vérifié après action
- [ ] Ne pas toucher aux tests legacy sauf refactoring explicite
- [ ] Scénarios sécurité (401/403) → `testing-integration.md`
