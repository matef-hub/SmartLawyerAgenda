package com.example.smartlawyeragenda.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartlawyeragenda.data.AppDatabase
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.ui.navigation.NavigationConstants
import com.example.smartlawyeragenda.ui.navigation.NavigationHelper
import com.example.smartlawyeragenda.ui.screens.*
import com.example.smartlawyeragenda.ui.theme.ThemeState
import com.example.smartlawyeragenda.utils.BackupManager
import com.example.smartlawyeragenda.viewmodel.AgendaViewModel
import com.example.smartlawyeragenda.viewmodel.AgendaViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
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

    NavHost(
        navController = navController,
        startDestination = NavigationConstants.SPLASH_ROUTE
    ) {
        // Splash Screen
        composable(NavigationConstants.SPLASH_ROUTE) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(NavigationConstants.AGENDA_ROUTE) {
                        popUpTo(NavigationConstants.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(NavigationConstants.LOGIN_ROUTE) {
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            
            LoginScreen(
                onSignInClick = {
                    isLoading = true
                    errorMessage = null
                    // TODO: Implement actual Google Sign-In
                    // For now, simulate successful login
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(2000) // Simulate network delay
                        isLoading = false
                        navController.navigate(NavigationConstants.AGENDA_ROUTE)
                    }
                }
            )
        }

        // Main Agenda Screen
        composable(NavigationConstants.AGENDA_ROUTE) {
            var showDeleteSessionDialog by remember { mutableStateOf<com.example.smartlawyeragenda.viewmodel.SessionWithCase?>(null) }
            var showErrorDialog by remember { mutableStateOf<String?>(null) }
            var isProcessing by remember { mutableStateOf(false) }
            
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
                onDateSelected = { dateMillis ->
                    isProcessing = true
                    viewModel.selectDate(dateMillis)
                    // Simulate processing delay
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(500)
                        isProcessing = false
                    }
                },
                onSearchQuery = { query ->
                    isProcessing = true
                    viewModel.searchSessions(query)
                    // Simulate search delay
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(300)
                        isProcessing = false
                    }
                },
                onUpdateSessionStatus = { sessionId, status ->
                    isProcessing = true
                    viewModel.updateSessionStatus(sessionId, status)
                    // Simulate processing delay
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(500)
                        isProcessing = false
                    }
                }
            )
            
            // Delete Session Confirmation Dialog
            showDeleteSessionDialog?.let { sessionWithCase ->
                com.example.smartlawyeragenda.ui.components.DeleteSessionDialog(
                    sessionTitle = sessionWithCase.getDisplayTitle(),
                    onConfirm = {
                        viewModel.deleteSession(sessionWithCase.session)
                        showDeleteSessionDialog = null
                    },
                    onDismiss = { showDeleteSessionDialog = null },
                    isVisible = true
                )
            }
            
            // Error Dialog
            com.example.smartlawyeragenda.ui.components.GenericErrorDialog(
                message = showErrorDialog ?: "",
                onDismiss = { showErrorDialog = null },
                isVisible = showErrorDialog != null
            )
            
            // Processing Loading State
            if (isProcessing) {
                com.example.smartlawyeragenda.ui.components.LoadingState(
                    message = "جاري المعالجة...",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Add Session Screen (without caseId - will show case selection)
        composable(NavigationConstants.ADD_SESSION_ROUTE) {
            AddEditSessionScreen(
                navController = navController,
                caseId = 0L, // Will show case selection
                onSave = { session ->
                    // Get the case for this session
                    val case = uiState.sessions.find { it.case.caseId == session.caseId }?.case
                        ?: com.example.smartlawyeragenda.data.entities.CaseEntity(
                            caseId = session.caseId,
                            caseNumber = "Unknown",
                            clientName = "Unknown"
                        )
                    
                    viewModel.saveSession(
                        case = case,
                        session = session,
                        createNextSession = false,
                        nextSessionDate = null
                    )
                    navController.popBackStack()
                }
            )
        }

        // Add Session Screen (with specific caseId)
        composable(NavigationConstants.ADD_SESSION_WITH_CASE_ROUTE) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString(NavigationConstants.Arguments.CASE_ID)?.toLongOrNull() ?: 0L
            
            AddEditSessionScreen(
                navController = navController,
                caseId = caseId,
                onSave = { session ->
                    // Get the case for this session
                    val case = uiState.sessions.find { it.case.caseId == session.caseId }?.case
                        ?: com.example.smartlawyeragenda.data.entities.CaseEntity(
                            caseId = session.caseId,
                            caseNumber = "Unknown",
                            clientName = "Unknown"
                        )
                    
                    viewModel.saveSession(
                        case = case,
                        session = session,
                        createNextSession = false,
                        nextSessionDate = null
                    )
                    navController.popBackStack()
                }
            )
        }

        // Edit Session Screen
        composable(NavigationConstants.EDIT_SESSION_WITH_ID_ROUTE) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString(NavigationConstants.Arguments.SESSION_ID)?.toLongOrNull()

            if (sessionId == null || !NavigationHelper.Validation.isValidSessionId(sessionId)) {
                // Invalid session ID, navigate back with error handling
                LaunchedEffect(Unit) {
                    NavigationHelper.navigateBack(navController, NavigationConstants.AGENDA_ROUTE)
                }
                return@composable
            }

            val sessionWithCase = uiState.sessions.find { it.session.sessionId == sessionId }

            if (sessionWithCase != null) {
                // Session found in current state
                AddEditSessionScreen(
                    navController = navController,
                    caseId = sessionWithCase.case.caseId,
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
                // Session not in current state, load from database
                var isLoadingSession by remember { mutableStateOf(true) }
                var loadedSession by remember { mutableStateOf<com.example.smartlawyeragenda.data.entities.SessionEntity?>(null) }
                var loadedCase by remember { mutableStateOf<com.example.smartlawyeragenda.data.entities.CaseEntity?>(null) }

                LaunchedEffect(sessionId) {
                    try {
                        val session = viewModel.getSessionById(sessionId)
                        if (session != null) {
                            val case = viewModel.getCaseById(session.caseId)
                            loadedSession = session
                            loadedCase = case
                        }
                    } catch (e: Exception) {
                        // Handle error - log and set error state
                        android.util.Log.e("AppNavHost", "Error loading session $sessionId", e)
                        isLoadingSession = false
                        // Show error dialog
                        // TODO: Show error dialog
                    } finally {
                        isLoadingSession = false
                    }
                }

                when {
                    isLoadingSession -> {
                        // Show loading state
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                    loadedSession != null && loadedCase != null -> {
                        // Show edit screen with loaded data
                        AddEditSessionScreen(
                            navController = navController,
                            caseId = loadedCase!!.caseId,
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
                        // Session not found, navigate back with error handling
                        LaunchedEffect(Unit) {
                            NavigationHelper.navigateBack(navController, NavigationConstants.AGENDA_ROUTE)
                        }
                    }
                }
            }
        }

        // Settings Screen
        composable(NavigationConstants.SETTINGS_ROUTE) {
            var showJsonExportDialog by remember { mutableStateOf(false) }
            var showCsvExportDialog by remember { mutableStateOf(false) }
            var isExporting by remember { mutableStateOf(false) }
            var exportError by remember { mutableStateOf<String?>(null) }
            
            SettingsScreen(
                isLoggedIn = uiState.isLoggedIn,
                isLoading = uiState.isLoading || isExporting,
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
                onBackClick = {
                    NavigationHelper.navigateBack(navController)
                }
            )
            
            // JSON Export Confirmation Dialog
            com.example.smartlawyeragenda.ui.components.ExportConfirmationDialog(
                format = "JSON",
                onConfirm = {
                    showJsonExportDialog = false
                    isExporting = true
                    exportError = null
                    
                    // TODO: Implement actual JSON export
                    kotlinx.coroutines.GlobalScope.launch {
                        try {
                            kotlinx.coroutines.delay(2000) // Simulate export process
                            isExporting = false
                            // Show success message
                        } catch (_: Exception) {
                            isExporting = false
                            exportError = "فشل في تصدير البيانات إلى JSON"
                        }
                    }
                },
                onDismiss = { showJsonExportDialog = false },
                isVisible = showJsonExportDialog
            )
            
            // CSV Export Confirmation Dialog
            com.example.smartlawyeragenda.ui.components.ExportConfirmationDialog(
                format = "CSV",
                onConfirm = {
                    showCsvExportDialog = false
                    isExporting = true
                    exportError = null
                    
                    // TODO: Implement actual CSV export
                    kotlinx.coroutines.GlobalScope.launch {
                        try {
                            kotlinx.coroutines.delay(2000) // Simulate export process
                            isExporting = false
                            // Show success message
                        } catch (_: Exception) {
                            isExporting = false
                            exportError = "فشل في تصدير البيانات إلى CSV"
                        }
                    }
                },
                onDismiss = { showCsvExportDialog = false },
                isVisible = showCsvExportDialog
            )
            
            // Export Error Dialog
            com.example.smartlawyeragenda.ui.components.ExportErrorDialog(
                onRetry = {
                    exportError = null
                    // Retry logic here
                },
                onDismiss = { exportError = null },
                isVisible = exportError != null
            )
        }

        // Cases Screen
        composable(NavigationConstants.CASES_ROUTE) {
            var searchQuery by remember { mutableStateOf("") }
            var showDeleteDialog by remember { mutableStateOf<com.example.smartlawyeragenda.data.entities.CaseEntity?>(null) }
            var showToggleDialog by remember { mutableStateOf<com.example.smartlawyeragenda.data.entities.CaseEntity?>(null) }
            var isSearching by remember { mutableStateOf(false) }
            var searchError by remember { mutableStateOf<String?>(null) }
            
            // Get case statistics
            val caseStatistics = remember(uiState.sessions) {
                uiState.sessions.groupBy { it.case.caseId }.mapValues { (_, sessionsWithCase) ->
                    val case = sessionsWithCase.first().case
                    val sessions = sessionsWithCase.map { it.session }
                    com.example.smartlawyeragenda.repository.CaseStatistics(
                        case = case,
                        totalSessions = sessions.size,
                        latestSessionDate = sessions.maxOfOrNull { it.sessionDate },
                        upcomingSessionsCount = sessions.count { it.sessionDate > System.currentTimeMillis() },
                        completedSessionsCount = sessions.count { it.status == com.example.smartlawyeragenda.data.entities.SessionStatus.COMPLETED },
                        postponedSessionsCount = sessions.count { it.status == com.example.smartlawyeragenda.data.entities.SessionStatus.POSTPONED }
                    )
                }
            }
            
            // Filter cases based on search query
            val filteredCases = remember(uiState.sessions, searchQuery) {
                val allCases = uiState.sessions.map { it.case }.distinctBy { it.caseId }
                if (searchQuery.isBlank()) {
                    allCases
                } else {
                    com.example.smartlawyeragenda.ui.components.CaseSearchHelper.searchCases(allCases, searchQuery)
                }
            }
            
            CasesScreen(
                cases = filteredCases,
                caseStatistics = caseStatistics,
                isLoading = uiState.isLoading || isSearching,
                onBackClick = {
                    NavigationHelper.navigateBack(navController)
                },
                onAddCaseClick = {
                    NavigationHelper.navigateToAddCase(navController)
                },
                onCaseClick = { case ->
                    // Navigate to add session with this case
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
                    isSearching = true
                    // Simulate search delay
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(500)
                        isSearching = false
                    }
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
            
            // Delete Case Confirmation Dialog
            showDeleteDialog?.let { case ->
                com.example.smartlawyeragenda.ui.components.DeleteCaseDialog(
                    caseTitle = case.getDisplayName(),
                    onConfirm = {
                        viewModel.deleteCaseWithSessions(case.caseId)
                        showDeleteDialog = null
                    },
                    onDismiss = { showDeleteDialog = null },
                    isVisible = true
                )
            }
            
            // Toggle Case Status Confirmation Dialog
            showToggleDialog?.let { case ->
                com.example.smartlawyeragenda.ui.components.ToggleCaseStatusDialog(
                    caseTitle = case.getDisplayName(),
                    isCurrentlyActive = case.isActive,
                    onConfirm = {
                        // TODO: Implement case status toggle
                        showToggleDialog = null
                    },
                    onDismiss = { showToggleDialog = null },
                    isVisible = true
                )
            }
            
            // Search Error Dialog
            com.example.smartlawyeragenda.ui.components.GenericErrorDialog(
                message = searchError ?: "",
                onDismiss = { searchError = null },
                isVisible = searchError != null
            )
        }

        // Add Case Screen (placeholder for future implementation)
        composable(NavigationConstants.ADD_CASE_ROUTE) {
            // TODO: Implement AddCaseScreen
            androidx.compose.material3.Text("Add Case Screen - Coming Soon")
        }

        // Edit Case Screen (placeholder for future implementation)
        composable(NavigationConstants.EDIT_CASE_WITH_ID_ROUTE) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString(NavigationConstants.Arguments.CASE_ID)?.toLongOrNull()
            // TODO: Implement EditCaseScreen
            androidx.compose.material3.Text("Edit Case Screen - Coming Soon (Case ID: $caseId)")
        }
    }
}
