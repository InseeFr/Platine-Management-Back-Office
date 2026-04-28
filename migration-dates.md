## Migration `Date` → `Instant` (sur SB 3.5)

### 1 Migration DB Liquibase

**Fichier à créer** : `platine-pilotage-db/src/main/resources/db/changelog/060_migrate_dates_to_timestamptz.sql`

Pour chaque colonne concernée, une instruction de type :

```sql
-- liquibase formatted sql
-- changeset ddarras:060-01 context:prod

ALTER TABLE questioning_event
  ALTER COLUMN "date" TYPE timestamptz
  USING "date" AT TIME ZONE 'Europe/Paris';

ALTER TABLE user_event
  ALTER COLUMN event_date TYPE timestamptz
  USING event_date AT TIME ZONE 'Europe/Paris';

-- ... etc pour les 25+ colonnes des 8 tables
-- rollback ALTER TABLE ... ALTER COLUMN ... TYPE timestamp USING ... AT TIME ZONE 'Europe/Paris';
```

**Tables concernées** (à vérifier via les noms générés par le naming strategy `CamelCase → underscore`) :

| Table | Colonnes à migrer |
|---|---|
| `questioning_event` | `date` |
| `user_event` | `event_date` |
| `contact_event` | `event_date` |
| `questioning` | `highest_event_date` |
| `questioning_accreditation` | `creation_date` |
| `questioning_comment` | `date` |
| `survey_unit_comment` | `date` |
| `partitioning` | `opening_date`, `closing_date`, `return_date`, `opening_letter_date`, `opening_mail_date`, `followup_letter_1_date` … `followup_mail_4_date`, `formal_notice_date`, `no_reply_date` |
| `survey_unit_event` (déjà `LocalDateTime`) | `date`, `creation_date` — passer aussi en `timestamptz` **si D2 = oui** |
| `questioning_communication` (déjà `LocalDateTime`) | `date` — idem |

Points d'attention :
- Les données existantes sont **interprétées comme heure de Paris** par le `AT TIME ZONE 'Europe/Paris'`. C'est cohérent car l'appli tournait avec JVM en TZ Paris. À valider sur un snapshot avant prod.
- Rollback : prévoir la commande inverse dans `-- rollback`.
- Ajouter le fichier à `db/master.xml`.

### 2 Migration des entités (`platine-pilotage-db`)

| Fichier | Transformation |
|---|---|
| `domain/questioning/QuestioningEvent.java` | `Date date` → `Instant date`, constructeur et import |
| `domain/user/UserEvent.java` | `Date eventDate` → `Instant eventDate` |
| `domain/contact/ContactEvent.java` | `Date eventDate` → `Instant eventDate` |
| `domain/questioning/Questioning.java` | `Date highestEventDate` → `Instant highestEventDate` |
| `domain/questioning/QuestioningAccreditation.java` | `Date creationDate` → `Instant creationDate` |
| `domain/questioning/QuestioningComment.java` | `Date date` → `Instant date` |
| `domain/questioning/SurveyUnitComment.java` | `Date date` → `Instant date` |
| `domain/metadata/Partitioning.java` | 14 champs `Date` → `Instant` |
| `domain/questioning/SurveyUnitEvent.java` | `LocalDateTime` → `Instant` (si D2 = oui) |
| `domain/questioning/QuestioningCommunication.java` | `LocalDateTime` → `Instant` (si D2 = oui) |

### 3 Migration des DTOs (`platine-pilotage-shared`)

14 DTOs à adapter. Les DTOs identifiés par l'exploration :
- `QuestioningEventDto`, `UserEventDto`, `SurveyUnitCommentOutputDto`, `QuestioningCommentOutputDto`, `QuestioningAccreditationDto`, et 9 autres à lister via `Grep "java.util.Date"` sur `platine-pilotage-shared`.

Règle : si le DTO est **output** vers l'API publique, ajouter éventuellement `@JsonFormat` explicite pour figer le contrat JSON (à confirmer selon D6 et décision sur rétrocompatibilité).

### 4 Migration des services (`platine-pilotage-service`)

16 fichiers touchés. Pattern à appliquer :

```java
// Avant
new Date()
Date now = new Date();

// Après
clock.instant()
Instant now = clock.instant();
```

Injecter le bean `Clock` là où ce n'est pas déjà fait. Les services identifiés :
- `UserEventServiceImpl`
- `QuestioningEventServiceImpl`
- `QuestioningAccreditationServiceImpl`
- `QuestioningCommentServiceImpl`
- `SurveyUnitCommentServiceImpl`
- `ContactEventServiceImpl`
- + mappers (ModelMapper) : vérifier les converters `Date` ↔ `Instant`. ModelMapper 3.2.5 n'a pas de converter natif — il faudra en ajouter un ou retirer le type target.

### B.5 `TimeConfiguration.clock()` (décision D4)

**Fichier** : `platine-pilotage-api/.../configuration/TimeConfiguration.java`

```java
@Bean
public Clock clock() {
    return Clock.systemUTC();
}
```

Impact : `clock.instant()` retournera la même valeur absolue, mais `LocalDate.now(clock)` changera de jour aux extrémités. À auditer si `LocalDate.now(clock)` est utilisé quelque part — si oui, faire une passe explicite pour passer par `ZoneId.of("Europe/Paris")` au bon endroit.

### B.6 `ApiError.timestamp` (décision D6)

**Fichier** : `platine-pilotage-api/.../exception/ApiError.java`

```java
@JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy HH:mm:ss",
            timezone = "Europe/Paris")
private Instant timestamp;
```

Correction implicite : `hh` (12h) → `HH` (24h) — comportement probablement non volontaire actuellement.
