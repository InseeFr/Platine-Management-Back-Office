# Repository Guidelines

## Project Structure & Module Organization
- Modules: `platine-management-api` (Spring Boot API), `platine-management-service`, `platine-management-db`, `platine-management-shared`.
- Source: `<module>/src/main/java`; Tests: `<module>/src/test/java` (+ Cucumber features in `platine-management-api/src/test/resources/integration`).
- Docs & CI: `docs/`, `.github/workflows/`.
- Docker/dev assets: `platine-management-api/Dockerfile`, `platine-management-api/compose.yml`, `.env`.

## Build, Test, and Development Commands
- Build all modules: `./mvnw clean verify` (Windows: `mvnw.cmd clean verify`).
- Run API locally: `./mvnw -pl platine-management-api -am spring-boot:run`.
- Unit tests only: `./mvnw test`.
- Integration tests (Failsafe): included in `verify` (patterns `**/*IT.java`, `**/integration/**/*`).
- Coverage (JaCoCo): `./mvnw -P coverage verify` → report aggregate at `platine-management-api/target/site/jacoco-aggregate/jacoco.xml`.

## Coding Style & Naming Conventions
- Language: Java 21, Spring Boot 3.5.
- Indentation: 4 spaces; line endings UTF‑8.
- Packages: `fr.insee.survey.datacollectionmanagement.*`.
- Classes: `PascalCase`; methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Lombok is enabled; prefer constructors/getters via Lombok over boilerplate.

## Testing Guidelines
- Frameworks: JUnit Platform (Jupiter), Spring Test, Cucumber for integration features.
- Naming: unit `*Test.java`; integration `*IT.java` or under `.../integration/...`.
- Keep tests deterministic; mock external systems. H2 and WireMock are available for tests.

## Commit & Pull Request Guidelines
- Commits: follow Conventional Commits (e.g., `feat: add campaign filter`, `fix(api): null check`).
- Pre-commit hooks: Conventional commit check; secret scan (Gitleaks); optional Trivy FS scan on pre-push.
  - Install hooks: `pre-commit install --hook-type commit-msg --hook-type pre-push`.
- PRs: include clear description, linked issue (`Closes #123`), test evidence (logs or screenshots), and note any config/db changes. Ensure CI (build, tests, Sonar) passes.

## Security & Configuration Tips
- Never commit secrets; `.gitleaks.toml` and Trivy help enforce this. Use environment variables or `.env` in `platine-management-api` for local only.
- For local auth, see `platine-management-api/container/keycloak/` realm file; align client IDs/scopes with app config.
