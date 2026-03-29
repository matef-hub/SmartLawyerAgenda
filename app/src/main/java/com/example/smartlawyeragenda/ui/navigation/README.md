# Navigation Guide

## Files

- `NavigationConstants.kt`: route names, route patterns, argument keys, and route builders.
- `NavigationHelper.kt`: safe wrapper methods for navigation operations.

## Primary Routes

- `splash`
- `login`
- `agenda`
- `settings`
- `cases`
- `add_session`
- `add_session/{caseId}`
- `edit_session/{sessionId}`
- `add_case`
- `edit_case/{caseId}`

## Usage

```kotlin
NavigationHelper.navigateToAgenda(navController)
NavigationHelper.navigateToSettings(navController)
NavigationHelper.navigateToAddSession(navController, caseId = 12L)
NavigationHelper.navigateToEditSession(navController, sessionId = 42L)
NavigationHelper.navigateToEditCase(navController, caseId = 9L)
NavigationHelper.navigateBack(navController, fallbackRoute = NavigationConstants.AGENDA_ROUTE)
```

## Validation Helpers

- `NavigationHelper.Validation.isValidSessionId(sessionId)`
- `NavigationHelper.Validation.isValidCaseId(caseId)`
- `NavigationHelper.Validation.isValidRoute(route)`

`AppNavHost` should always validate incoming IDs before loading/editing entities and fall back to a safe route when invalid.
