# AppNavHost Status

This file tracks the current state of `app/src/main/java/com/example/smartlawyeragenda/ui/AppNavHost.kt`.

## Implemented

- Splash route -> agenda route transition.
- Login route wired to `GoogleSignInHelper` and `AgendaViewModel.initializeGoogleDriveAccount`.
- Agenda route wired for:
  - add/edit/delete session
  - search
  - date filters (today/tomorrow/week/month/upcoming)
  - session status updates
- Settings route wired for:
  - sign in / sign out
  - backup and restore
  - JSON and CSV export with share intent
  - sample data population and full data clear
- Cases route wired for:
  - full list and search
  - add/edit/delete case
  - active/inactive toggle
  - statistics derived from live sessions flow
- Generic error dialog uses ViewModel error state and clear callback.

## Guardrails in Place

- Invalid `sessionId` and `caseId` route arguments fall back safely.
- `rememberCoroutineScope` used for async UI actions; no `GlobalScope` usage.
- Export flow confirms before action and reports success/failure.

## Remaining Operational Prerequisites

- Google sign-in requires a valid `google-services.json` so `default_web_client_id` is generated.
- Local environment must have JDK configured (`JAVA_HOME`) to run Gradle compile/test tasks.
