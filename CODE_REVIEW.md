# SmartLawyerAgenda - Code Review and Fix Plan

This document summarizes the current project state, issues found, and the prioritized fixes implemented in branch `fix/full-wireup`.

## Files scanned (high-level)
- app/src/main/java/com/example/smartlawyeragenda
  - data/: `AppDatabase.kt`, `DatabaseConverters.kt`, dao (`CaseDao.kt`, `SessionDao.kt`), entities (`CaseEntity.kt`, `SessionEntity.kt`), `SampleDataGenerator.kt`
  - repository/: `MainRepository.kt`
  - viewmodel/: `AgendaViewModel.kt`, `AgendaViewModelFactory.kt`
  - ui/: `AppNavHost.kt`, components (`SearchBar.kt`, `DateFilterChips.kt`, `EnhancedComponents.kt`, `LoadingStates.kt`, dialogs), screens (`AgendaScreen.kt`, `AddEditSessionScreen.kt`, `CasesScreen.kt`, `SettingsScreen.kt`, `SplashScreen.kt`, `LoginScreen.kt`, `AddCaseScreen.kt`, `EditCaseScreen.kt`), navigation (`NavigationConstants.kt`, `NavigationHelper.kt`)
  - utils/: `HijriUtils.kt`, `BackupManager.kt`, `NotificationHelper.kt`, `ExportHelper.kt`
- app/src/main/res: values, fonts, drawables, xml
- Root build files and READMEs

## Architecture summary
- Room DB is present with `CaseEntity` and `SessionEntity`, DAOs, and `AppDatabase`. Converters exist.
- Repository (`MainRepository`) exposes flows and suspend APIs for cases/sessions and statistics, plus import/export and sample data.
- ViewModel (`AgendaViewModel`) defines `AgendaUiState`, `SessionWithCase`, and complete logic for date selection, search, CRUD, statistics, backup/restore, sample data, and validation.
- Navigation: `AppNavHost.kt` hosts composable routes and wires UI to ViewModel callbacks.
- UI: Compose screens and reusable components exist, including advanced cards and search/date UI.

## Findings: missing/buggy wiring and layout
- AgendaScreen layout uses Column + nested LazyColumn in `SessionsSection`. This risks measurement issues and makes ordering hard. Should be one `LazyColumn` hosting header/stats/list/filter/search.
- `CustomDateFilterDropdown` defines `DateFilter.Upcoming` but AgendaScreen has a TODO when selected, and the dropdown list omits Upcoming.
- FAB uses a magic bottom padding (60.dp) instead of proper insets; should use `navigationBarsPadding()` and `windowInsetsPadding`.
- Navigation placeholders: Add/Edit Case routes still show TODO text in `AppNavHost`. Real screens (`AddCaseScreen`, `EditCaseScreen`) exist and should be wired.
- Case toggle in `CasesScreen` dialog is TODO; ViewModel provides `toggleCaseStatus`.
- Some GlobalScope usages in `AppNavHost` should be avoided; minor and not blocking.
- RTL: Manifest sets `supportsRtl="true"` and `android:layoutDirection="rtl"`; most screens wrap with RTL. Looks OK.

## Prioritized fix list
1) Refactor `AgendaScreen` to a single `LazyColumn` and ensure ordering: DateHeader, Sessions, DateFilter, SearchBar; fix any width/infinite constraints. Add insets for FAB.
2) Wire `CustomDateFilterDropdown` end-to-end: expose callback from `AgendaScreen`, handle Upcoming/Week/Month by delegating to ViewModel methods (`selectDate`, `getUpcomingSessions`, `getSessionsForWeek`, `getSessionsForMonth`). Add Upcoming option to dropdown.
3) Replace Add/Edit Case navigation placeholders with `AddCaseScreen` and `EditCaseScreen`. Hook saving to ViewModel `saveCase`/`updateCase` and navigate back.
4) Hook case status toggle dialog to `AgendaViewModel.toggleCaseStatus`.
5) Provide CODE_REVIEW.md and update README with build/run and basic verification.

## Functions declared but not invoked (action)
- `AgendaViewModel.exportLocalBackup()` returns null and is not used. Left as-is, noted for future refactor if export UX is added.
- Several helper composables are present but not all used on Agenda; acceptable.

## Assumptions
- Date filter “ThisWeek/NextWeek” acceptance: using repository’s week range; “ThisMonth” uses month range; “Upcoming” shows sessions from now onward.
- Authentication is stubbed; not in acceptance criteria.

## Next steps (implemented in this branch)
- AgendaScreen refactor, date filter wiring, FAB insets
- AppNavHost: wire date filter callbacks; Replace Add/Edit Case routes; toggle case status
- Minor RTL and layout polish where needed

PATCH NOTES: See commit history in `fix/full-wireup` for granular edits.
