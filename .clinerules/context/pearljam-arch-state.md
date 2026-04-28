# Context : Platine Pilotage — Architecture en couches (état & cible)

> Source unique sur l'architecture du projet Platine Pilotage : principes,
> structure, règles d'import, patterns en place, écarts à la cible.
> Dernière MAJ : 2026-04-28.
>
> **À noter** : contrairement à d'autres projets INSEE (ex : Pearl Jam) qui
> visent une architecture hexagonale stricte, Platine Pilotage suit une
> **architecture en couches MVC** classique (Controller → Service → Repository
> → Entity). Les éléments listés en "écart cible" sont des points
> d'amélioration progressive ; **ne pas les reproduire dans du nouveau code**.

## Principes fondamentaux (cible projet)

1. **Séparation par couche** : API (REST) → Service (métier) → DB (persistance).
2. **Séparation Entity / DTO stricte** : les entités JPA ne sortent jamais
   des controllers ; le module `shared` porte les DTOs exposés.
3. **Injection par constructeur** uniquement (`@RequiredArgsConstructor`
   Lombok). Pas de `@Autowired` sur champ.
4. **Transactions explicites** : `@Transactional` au niveau service, jamais
   au niveau controller.
5. **Découpage par domaine fonctionnel** dans chaque module (pas par couche).
   Exemple : `contact/`, `metadata/`, `questioning/`, `query/`, `user/`.

## Structure des modules Maven

```
platine-pilotage/                          (parent — groupId fr.insee.survey)
├── platine-pilotage-shared/               # DTOs, enums, validation, constants
│   └── fr.insee.survey.datacollectionmanagement
│       └── [domaine]/
│           ├── dto/                       # DTOs exposés (API + transverses)
│           ├── enums/                     # Enums métier
│           └── validation/                # Validateurs Jakarta
├── platine-pilotage-db/                   # Couche persistance (JPA + JdbcTemplate)
│   └── fr.insee.survey.datacollectionmanagement
│       └── [domaine]/
│           ├── domain/                    # Entités JPA (@Entity)
│           ├── repository/                # Spring Data JPA repositories
│           └── dao/                       # DAOs JdbcTemplate / orchestrateurs
├── platine-pilotage-service/              # Couche métier
│   └── fr.insee.survey.datacollectionmanagement
│       └── [domaine]/service/
│           ├── (interfaces)               # Contrats des services
│           ├── impl/                      # Implémentations
│           ├── exception/                 # Exceptions métier
│           ├── component/ | builder/      # Composants spécialisés
│           └── mapper/                    # Mappers ModelMapper custom
└── platine-pilotage-api/                  # Couche REST + sécurité
    └── fr.insee.survey.datacollectionmanagement
        ├── [domaine]/controller/          # @RestController par domaine
        ├── configuration/                 # Spring config, sécurité, CORS
        ├── exception/                     # @ControllerAdvice
        └── constants/                     # UrlConstants, etc.
```

**Package racine commun** : `fr.insee.survey.datacollectionmanagement`
(historique — le nom du repo a changé mais le package reste).

## Bounded contexts (sous-domaines fonctionnels)

| Contexte | Sous-package | Responsabilité |
|---|---|---|
| `contact` | `contact` | Contacts, adresses, événements contact |
| `metadata` | `metadata` | Campagnes, enquêtes, sources, partitionnements, propriétaires |
| `questioning` | `questioning` | Interrogations, unités d'enquête, accréditations, événements, communications |
| `query` | `query` | Reporting & requêtes transverses (Moog, monitoring, habilitations) |
| `user` | `user` | Utilisateurs, groupes, portefeuilles (wallets) |
| `ldap` | `ldap` | Intégration LDAP externe (lecture / écriture annuaire) |
| `view` | `view` | Vues SQL natives pour exploitation |

Chacun de ces contextes apparaît dans **plusieurs modules** : un même nom de
package (`contact`, `metadata`, …) traverse `shared`, `db`, `service`, `api`
avec des responsabilités différentes selon la couche.