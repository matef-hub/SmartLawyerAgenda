# SmartLawyerAgenda Functionality

## Current Architecture

- Data: Room (`CaseEntity`, `SessionEntity`) with DAOs (`CaseDao`, `SessionDao`).
- Domain: `MainRepository` handles CRUD, search, statistics, import/export, and transactional save/delete flows.
- State: `AgendaViewModel` exposes `AgendaUiState` as `StateFlow` and orchestrates filters, search, backup/restore, and mutations.
- UI: Jetpack Compose screens under `ui/screens` and navigation in `ui/AppNavHost.kt`.

## Key User Flows

- Manage sessions: add, edit, delete, update status.
- Manage cases: add, edit, delete, toggle active status.
- Filter sessions: date, week, month, upcoming, and text search.
- Backup/restore: Google Drive integration through `BackupManager`.
- Export: JSON and CSV file generation and sharing via `ExportHelper`.

## Core Repository APIs

### Cases

- `getAllCases()`
- `getCaseById(caseId)`
- `insertCase(case)`
- `updateCase(case)`
- `deleteCaseById(caseId)`
- `isCaseNumberExists(caseNumber, excludeCaseId)`

### Sessions

- `getSessionsForDate(dateMillis)`
- `getSessionsForWeek(weekStartMillis)`
- `getSessionsForMonth(monthStartMillis)`
- `getUpcomingSessions(fromDate)`
- `insertSession(session)`
- `updateSession(session)`
- `deleteSession(session)`
- `isSessionExists(caseId, sessionDate, excludeSessionId)`

### Combined/Data Management

- `saveCaseWithSession(case, session, createNextSession, nextSessionDate)`
- `deleteCaseWithSessions(caseId)`
- `exportData()`
- `importData(export)`
- `populateSampleData()`
- `clearAllData()`

## ViewModel Entry Points Used by UI

- `selectDate(...)`
- `searchSessions(...)`
- `getUpcomingSessions()`
- `getSessionsForWeek(...)`
- `getSessionsForMonth(...)`
- `saveSession(...)`
- `saveCase(...)`
- `deleteSession(...)`
- `deleteCaseWithSessions(...)`
- `toggleCaseStatus(...)`
- `backupToDrive()`
- `restoreFromDrive()`
- `initializeGoogleDriveAccount(accountName)`
- `signOutFromGoogle()`

## Operational Notes

- Google sign-in depends on `default_web_client_id` generated from `google-services.json`.
- Export sharing uses `FileProvider` (`${applicationId}.fileprovider`).
- Backup and data extraction are configured through `res/xml/backup_rules.xml` and `res/xml/data_extraction_rules.xml`.

## Testing Scope

- Unit tests currently cover entity validation and case-search helper behavior in `RepositoryTest.kt`.
- Full Android/Room integration tests still require an instrumented or Robolectric setup.
