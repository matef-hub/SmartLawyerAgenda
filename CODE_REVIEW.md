# SmartLawyerAgenda Code Review Status

This document tracks issues that were previously identified and their current state.

## Resolved

- Settings/UI wiring mismatch between `AppNavHost` and `SettingsScreen`.
- `AddEditSessionScreen` syntax issue from malformed state block.
- Deprecated manifest permissions (`GET_ACCOUNTS`, `USE_CREDENTIALS`) removed.
- Backup XML files updated from template/TODO content to explicit include rules.
- Theme system mode fixed to respect real system dark mode.
- Double theme wrapping removed from `MainActivity`.
- Export model duplication removed (single `DatabaseExport` in repository layer).
- Export sharing hardened using `FileProvider` URIs and manifest provider config.
- CSV export now escapes values safely.
- Google sign-in helper no longer uses a hardcoded placeholder client ID; it resolves `default_web_client_id`.
- Stale navigation and AppNavHost documentation updated.
- Placeholder repository test scaffolding replaced with deterministic unit tests.

## Environment Blocker

- Local Gradle compile/test verification is blocked until Java is available (`JAVA_HOME` not set, `java` not found).

## Recommended Follow-Up

- Run `./gradlew :app:compileDebugKotlin` and `./gradlew :app:testDebugUnitTest` once Java is configured.
- Add instrumentation tests for Room + ViewModel integration paths.
