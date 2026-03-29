package com.example.smartlawyeragenda.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartlawyeragenda.data.AppDatabase
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import com.example.smartlawyeragenda.repository.CaseStatistics
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.ui.components.CaseSearchHelper
import com.example.smartlawyeragenda.ui.components.DeleteCaseDialog
import com.example.smartlawyeragenda.ui.components.DeleteSessionDialog
import com.example.smartlawyeragenda.ui.components.ExportConfirmationDialog
import com.example.smartlawyeragenda.ui.components.GenericErrorDialog
import com.example.smartlawyeragenda.ui.components.GoogleSignInHelper
import com.example.smartlawyeragenda.ui.components.ToggleCaseStatusDialog
import com.example.smartlawyeragenda.ui.navigation.NavigationConstants
import com.example.smartlawyeragenda.ui.navigation.NavigationHelper
import com.example.smartlawyeragenda.ui.screens.AddCaseScreen
import com.example.smartlawyeragenda.ui.screens.AddEditSessionScreen
import com.example.smartlawyeragenda.ui.screens.AgendaScreen
import com.example.smartlawyeragenda.ui.screens.CasesScreen
import com.example.smartlawyeragenda.ui.screens.EditCaseScreen
import com.example.smartlawyeragenda.ui.screens.LoginScreen
import com.example.smartlawyeragenda.ui.screens.SettingsScreen
import com.example.smartlawyeragenda.ui.screens.SplashScreen
import com.example.smartlawyeragenda.ui.theme.ThemeState
import com.example.smartlawyeragenda.utils.BackupManager
import com.example.smartlawyeragenda.utils.ExportHelper
import com.example.smartlawyeragenda.viewmodel.AgendaViewModel
import com.example.smartlawyeragenda.viewmodel.AgendaViewModelFactory
import kotlinx.coroutines.launch

@Composable
@Suppress("UNUSED_PARAMETER")
fun AppNavHost(
    database: AppDatabase,
    backupManager: BackupManager,
    themeState: ThemeState,
    navController: NavHostController = rememberNavController()
) {
    val repository = remember { MainRepository(database) }
    val viewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(repository, backupManager)
    )

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val googleSignInHelper = remember { GoogleSignInHelper(context) }
    val exportHelper = remember { ExportHelper(context) }

    NavHost(
        navController = navController,
        startDestination = NavigationConstants.SPLASH_ROUTE
    ) {
        composable(NavigationConstants.SPLASH_ROUTE) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(NavigationConstants.AGENDA_ROUTE) {
                        popUpTo(NavigationConstants.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationConstants.LOGIN_ROUTE) {
            var isSigningIn by remember { mutableStateOf(false) }
            var loginError by remember { mutableStateOf<String?>(null) }

            LoginScreen(
                isLoading = isSigningIn,
                errorMessage = loginError,
                onSignInClick = {
                    if (isSigningIn) return@LoginScreen

                    coroutineScope.launch {
                        isSigningIn = true
                        loginError = null

                        val signInResult = googleSignInHelper.signIn()
                        if (signInResult == null || !signInResult.isSuccess) {
                            loginError = "Google sign-in failed"
                            isSigningIn = false
                            return@launch
                        }

                        val initialized = viewModel.initializeGoogleDriveAccount(signInResult.email)
                        if (!initialized) {
                            loginError = "Signed in, but account initialization failed"
                            isSigningIn = false
                            return@launch
                        }

                        isSigningIn = false

                        if (!navController.popBackStack()) {
                            NavigationHelper.navigateToAgenda(navController)
                        }
                    }
                }
            )
        }

        composable(NavigationConstants.AGENDA_ROUTE) {
            var showDeleteSessionDialog by remember { mutableStateOf<com.example.smartlawyeragenda.viewmodel.SessionWithCase?>(null) }

            AgendaScreen(
                uiState = uiState,
                onAddSessionClick = {
                    navController.navigate(NavigationConstants.ADD_SESSION_ROUTE)
                },
                onEditSessionClick = { sessionWithCase ->
                    NavigationHelper.navigateToEditSession(
                        navController = navController,
                        sessionId = sessionWithCase.session.sessionId
                    )
                },
                onDeleteSessionClick = { sessionWithCase ->
                    showDeleteSessionDialog = sessionWithCase
                },
                onSettingsClick = {
                    NavigationHelper.navigateToSettings(navController)
                },
                onCasesClick = {
                    NavigationHelper.navigateToCases(navController)
                },
                onSearchQuery = { query ->
                    viewModel.searchSessions(query)
                },
                onDateFilterSelected = { filter ->
                    when (filter) {
                        is com.example.smartlawyeragenda.ui.components.DateFilter.Today -> viewModel.selectDate(filter.startDate)
                        is com.example.smartlawyeragenda.ui.components.DateFilter.Tomorrow -> viewModel.selectDate(filter.startDate)
                        is com.example.smartlawyeragenda.ui.components.DateFilter.ThisWeek -> viewModel.getSessionsForWeek(filter.startDate)
                        is com.example.smartlawyeragenda.ui.components.DateFilter.NextWeek -> viewModel.getSessionsForWeek(filter.startDate)
                        is com.example.smartlawyeragenda.ui.components.DateFilter.ThisMonth -> viewModel.getSessionsForMonth(filter.startDate)
                        com.example.smartlawyeragenda.ui.components.DateFilter.Upcoming -> viewModel.getUpcomingSessions()
                    }
                },
                onUpdateSessionStatus = { sessionId, status ->
                    viewModel.updateSessionStatus(sessionId, status)
                }
            )

            showDeleteSessionDialog?.let { sessionWithCase ->
                DeleteSessionDialog(
                    sessionTitle = sessionWithCase.getDisplayTitle(),
                    onConfirm = {
                        viewModel.deleteSession(sessionWithCase.session)
                        showDeleteSessionDialog = null
                    },
                    onDismiss = { showDeleteSessionDialog = null },
                    isVisible = true
                )
            }

            GenericErrorDialog(
                message = uiState.error ?: "",
                onDismiss = { viewModel.clearError() },
                isVisible = uiState.error != null
            )
        }

        composable(NavigationConstants.ADD_SESSION_ROUTE) {
            AddEditSessionScreen(
                navController = navController,
                cases = uiState.cases,
                onSave = { session ->
                    val sessionCase = uiState.cases.find { it.caseId == session.caseId }
                    if (sessionCase != null) {
                        viewModel.saveSession(
                            case = sessionCase,
                            session = session,
                            createNextSession = false,
                            nextSessionDate = null
                        )
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(NavigationConstants.ADD_SESSION_WITH_CASE_ROUTE) { backStackEntry ->
            val caseId = backStackEntry.arguments
                ?.getString(NavigationConstants.Arguments.CASE_ID)
                ?.toLongOrNull()
                ?: 0L

            AddEditSessionScreen(
                navController = navController,
                cases = uiState.cases,
                preselectedCaseId = caseId,
                onSave = { session ->
                    val sessionCase = uiState.cases.find { it.caseId == session.caseId }
                    if (sessionCase != null) {
                        viewModel.saveSession(
                            case = sessionCase,
                            session = session,
                            createNextSession = false,
                            nextSessionDate = null
                        )
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(NavigationConstants.EDIT_SESSION_WITH_ID_ROUTE) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getString(NavigationConstants.Arguments.SESSION_ID)
                ?.toLongOrNull()

            if (sessionId == null || !NavigationHelper.Validation.isValidSessionId(sessionId)) {
                LaunchedEffect(Unit) {
                    NavigationHelper.navigateBack(navController, NavigationConstants.AGENDA_ROUTE)
                }
                return@composable
            }

            val sessionWithCase = uiState.sessions.find { it.session.sessionId == sessionId }

            if (sessionWithCase != null) {
                AddEditSessionScreen(
                    navController = navController,
                    cases = listOf(sessionWithCase.case),
                    existingSession = sessionWithCase.session,
                    onSave = { session ->
                        viewModel.saveSession(
                            case = sessionWithCase.case,
                            session = session,
                            createNextSession = false,
                            nextSessionDate = null
                        )
                        navController.popBackStack()
                    }
                )
            } else {
                var isLoadingSession by remember { mutableStateOf(true) }
                var loadedSession by remember { mutableStateOf<com.example.smartlawyeragenda.data.entities.SessionEntity?>(null) }
                var loadedCase by remember { mutableStateOf<CaseEntity?>(null) }

                LaunchedEffect(sessionId) {
                    try {
                        val session = viewModel.getSessionById(sessionId)
                        if (session != null) {
                            loadedSession = session
                            loadedCase = viewModel.getCaseById(session.caseId)
                        }
                    } finally {
                        isLoadingSession = false
                    }
                }

                when {
                    isLoadingSession -> androidx.compose.material3.CircularProgressIndicator()
                    loadedSession != null && loadedCase != null -> {
                        AddEditSessionScreen(
                            navController = navController,
                            cases = listOf(loadedCase!!),
                            existingSession = loadedSession,
                            onSave = { session ->
                                viewModel.saveSession(
                                    case = loadedCase!!,
                                    session = session,
                                    createNextSession = false,
                                    nextSessionDate = null
                                )
                                navController.popBackStack()
                            }
                        )
                    }
                    else -> {
                        LaunchedEffect(Unit) {
                            NavigationHelper.navigateBack(navController, NavigationConstants.AGENDA_ROUTE)
                        }
                    }
                }
            }
        }

        composable(NavigationConstants.SETTINGS_ROUTE) {
            var showJsonExportDialog by remember { mutableStateOf(false) }
            var showCsvExportDialog by remember { mutableStateOf(false) }
            var isExporting by remember { mutableStateOf(false) }
            var exportError by remember { mutableStateOf<String?>(null) }
            var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

            SettingsScreen(
                isLoggedIn = uiState.isLoggedIn,
                isLoading = uiState.isLoading || isExporting,
                onSignInClick = {
                    NavigationHelper.navigateToLogin(navController)
                },
                onSignOutClick = {
                    googleSignInHelper.signOut()
                    viewModel.signOutFromGoogle()
                },
                onBackupClick = {
                    viewModel.backupToDrive()
                },
                onRestoreClick = {
                    viewModel.restoreFromDrive()
                },
                onExportJsonClick = {
                    showJsonExportDialog = true
                },
                onExportCsvClick = {
                    showCsvExportDialog = true
                },
                onPopulateSampleDataClick = { viewModel.populateSampleData() },
                onClearAllDataClick = { viewModel.clearAllData() },
                onBackClick = {
                    NavigationHelper.navigateBack(navController)
                }
            )

            ExportConfirmationDialog(
                format = "JSON",
                onConfirm = {
                    showJsonExportDialog = false
                    coroutineScope.launch {
                        isExporting = true
                        exportError = null

                        try {
                            val exportData = viewModel.exportLocalBackup()
                            exportHelper.exportToJson(exportData.cases, exportData.sessions)
                                .fold(
                                    onSuccess = { uri ->
                                        exportHelper.shareFile(uri, "application/json")
                                        exportSuccessMessage = "JSON export completed"
                                    },
                                    onFailure = { error ->
                                        exportError = error.message ?: "JSON export failed"
                                    }
                                )
                        } catch (exception: Exception) {
                            exportError = exception.message ?: "JSON export failed"
                        } finally {
                            isExporting = false
                        }
                    }
                },
                onDismiss = { showJsonExportDialog = false },
                isVisible = showJsonExportDialog
            )

            ExportConfirmationDialog(
                format = "CSV",
                onConfirm = {
                    showCsvExportDialog = false
                    coroutineScope.launch {
                        isExporting = true
                        exportError = null

                        try {
                            val exportData = viewModel.exportLocalBackup()
                            exportHelper.exportToCsv(exportData.cases, exportData.sessions)
                                .fold(
                                    onSuccess = { uri ->
                                        exportHelper.shareFile(uri, "text/csv")
                                        exportSuccessMessage = "CSV export completed"
                                    },
                                    onFailure = { error ->
                                        exportError = error.message ?: "CSV export failed"
                                    }
                                )
                        } catch (exception: Exception) {
                            exportError = exception.message ?: "CSV export failed"
                        } finally {
                            isExporting = false
                        }
                    }
                },
                onDismiss = { showCsvExportDialog = false },
                isVisible = showCsvExportDialog
            )

            GenericErrorDialog(
                message = exportError ?: "",
                onRetry = { exportError = null },
                onDismiss = { exportError = null },
                isVisible = exportError != null
            )

            GenericErrorDialog(
                message = uiState.error ?: "",
                onDismiss = { viewModel.clearError() },
                isVisible = uiState.error != null
            )

            if (exportSuccessMessage != null) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { exportSuccessMessage = null },
                    confirmButton = {
                        androidx.compose.material3.TextButton(onClick = { exportSuccessMessage = null }) {
                            androidx.compose.material3.Text("OK")
                        }
                    },
                    title = { androidx.compose.material3.Text("Export") },
                    text = { androidx.compose.material3.Text(exportSuccessMessage ?: "") }
                )
            }
        }

        composable(NavigationConstants.CASES_ROUTE) {
            val allSessions by repository.getAllSessions().collectAsState(initial = emptyList())

            var searchQuery by remember { mutableStateOf("") }
            var showDeleteDialog by remember { mutableStateOf<CaseEntity?>(null) }
            var showToggleDialog by remember { mutableStateOf<CaseEntity?>(null) }

            val filteredCases = remember(uiState.cases, searchQuery) {
                if (searchQuery.isBlank()) {
                    uiState.cases
                } else {
                    CaseSearchHelper.searchCases(uiState.cases, searchQuery)
                }
            }

            val caseStatistics = remember(uiState.cases, allSessions) {
                val sessionsByCaseId = allSessions.groupBy { it.caseId }

                uiState.cases.associate { case ->
                    val sessions = sessionsByCaseId[case.caseId].orEmpty()
                    case.caseId to CaseStatistics(
                        case = case,
                        totalSessions = sessions.size,
                        latestSessionDate = sessions.maxOfOrNull { it.sessionDate },
                        upcomingSessionsCount = sessions.count { it.sessionDate > System.currentTimeMillis() },
                        completedSessionsCount = sessions.count { it.status == SessionStatus.COMPLETED },
                        postponedSessionsCount = sessions.count { it.status == SessionStatus.POSTPONED }
                    )
                }
            }

            CasesScreen(
                cases = filteredCases,
                caseStatistics = caseStatistics,
                isLoading = uiState.isLoading,
                onBackClick = {
                    NavigationHelper.navigateBack(navController)
                },
                onAddCaseClick = {
                    NavigationHelper.navigateToAddCase(navController)
                },
                onCaseClick = { case ->
                    NavigationHelper.navigateToAddSession(
                        navController = navController,
                        caseId = case.caseId
                    )
                },
                onDeleteCaseClick = { case ->
                    showDeleteDialog = case
                },
                onSearchQuery = { query ->
                    searchQuery = query
                },
                onEditCaseClick = { case ->
                    NavigationHelper.navigateToEditCase(
                        navController = navController,
                        caseId = case.caseId
                    )
                },
                onToggleCaseStatus = { case ->
                    showToggleDialog = case
                }
            )

            showDeleteDialog?.let { case ->
                DeleteCaseDialog(
                    caseTitle = case.getDisplayName(),
                    onConfirm = {
                        viewModel.deleteCaseWithSessions(case.caseId)
                        showDeleteDialog = null
                    },
                    onDismiss = { showDeleteDialog = null },
                    isVisible = true
                )
            }

            showToggleDialog?.let { case ->
                ToggleCaseStatusDialog(
                    caseTitle = case.getDisplayName(),
                    isCurrentlyActive = case.isActive,
                    onConfirm = {
                        viewModel.toggleCaseStatus(case.caseId)
                        showToggleDialog = null
                    },
                    onDismiss = { showToggleDialog = null },
                    isVisible = true
                )
            }

            GenericErrorDialog(
                message = uiState.error ?: "",
                onDismiss = { viewModel.clearError() },
                isVisible = uiState.error != null
            )
        }

        composable(NavigationConstants.ADD_CASE_ROUTE) {
            AddCaseScreen(
                navController = navController,
                onSave = { newCase ->
                    viewModel.saveCase(newCase)
                    NavigationHelper.navigateBack(navController)
                }
            )
        }

        composable(NavigationConstants.EDIT_CASE_WITH_ID_ROUTE) { backStackEntry ->
            val caseId = backStackEntry.arguments
                ?.getString(NavigationConstants.Arguments.CASE_ID)
                ?.toLongOrNull()

            var loadedCase by remember { mutableStateOf<CaseEntity?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(caseId) {
                if (caseId != null) {
                    loadedCase = viewModel.getCaseById(caseId)
                }
                isLoading = false
            }

            when {
                isLoading -> androidx.compose.material3.CircularProgressIndicator()
                loadedCase != null -> {
                    EditCaseScreen(
                        navController = navController,
                        existingCase = loadedCase!!,
                        onSave = { updatedCase ->
                            viewModel.saveCase(updatedCase)
                            NavigationHelper.navigateBack(navController)
                        }
                    )
                }
                else -> {
                    LaunchedEffect(Unit) {
                        NavigationHelper.navigateBack(navController, NavigationConstants.CASES_ROUTE)
                    }
                }
            }
        }
    }
}
