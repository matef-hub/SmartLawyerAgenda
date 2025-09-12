package com.example.smartlawyeragenda.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartlawyeragenda.data.AppDatabase
import com.example.smartlawyeragenda.repository.MainRepository
import com.example.smartlawyeragenda.ui.screens.*
import com.example.smartlawyeragenda.utils.BackupManager
import com.example.smartlawyeragenda.viewmodel.AgendaViewModel
import com.example.smartlawyeragenda.viewmodel.AgendaViewModelFactory
import com.example.smartlawyeragenda.viewmodel.SessionWithCase

@Composable
fun AppNavHost(
    database: AppDatabase,
    backupManager: BackupManager,
    navController: NavHostController = rememberNavController()
) {
    val repository = remember { MainRepository(database) }
    val viewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(repository, backupManager)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("agenda") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onSignInClick = {
                    // TODO: Implement Google Sign-In
                    navController.navigate("agenda")
                }
            )
        }
        
        composable("agenda") {
            AgendaScreen(
                uiState = uiState,
                onAddSessionClick = {
                    navController.navigate("add_session")
                },
                onEditSessionClick = { sessionWithCase ->
                    navController.navigate("edit_session/${sessionWithCase.session.sessionId}")
                },
                onDeleteSessionClick = { sessionWithCase ->
                    viewModel.deleteSession(sessionWithCase.session)
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onCasesClick = {
                    navController.navigate("cases")
                },
                onDateSelected = { dateMillis ->
                    viewModel.selectDate(dateMillis)
                },
                onSearchQuery = { query ->
                    viewModel.searchSessions(query)
                }
            )
        }
        
        composable("add_session") {
            AddEditSessionScreen(
                onSave = { case, session, nextSessionDate ->
                    viewModel.saveSession(case, session, nextSessionDate)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("edit_session/{sessionId}") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull()
            
            // Find the session and case data
            val sessionWithCase = uiState.sessions.find { it.session.sessionId == sessionId }
            
            if (sessionWithCase != null) {
                AddEditSessionScreen(
                    session = sessionWithCase.session,
                    case = sessionWithCase.case,
                    onSave = { case, session, nextSessionDate ->
                        viewModel.saveSession(case, session, nextSessionDate)
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            } else {
                // Try to load session and case data from database
                LaunchedEffect(sessionId) {
                    if (sessionId != null) {
                        val session = viewModel.repository.getSessionById(sessionId)
                        if (session != null) {
                            val case = viewModel.repository.getCaseById(session.caseId)
                            if (case != null) {
                                // Navigate to edit screen with loaded data
                                // This would require a different navigation approach
                            }
                        }
                    }
                    navController.popBackStack()
                }
            }
        }
        
        composable("settings") {
            SettingsScreen(
                isLoggedIn = uiState.isLoggedIn,
                isLoading = uiState.isLoading,
                onBackupClick = {
                    viewModel.backupToDrive()
                },
                onRestoreClick = {
                    viewModel.restoreFromDrive()
                },
                onExportJsonClick = {
                    // TODO: Implement JSON export
                },
                onExportCsvClick = {
                    // TODO: Implement CSV export
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("cases") {
            CasesScreen(
                cases = uiState.sessions.map { it.case }.distinctBy { it.caseId },
                isLoading = uiState.isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddCaseClick = {
                    navController.navigate("add_session")
                },
                onCaseClick = { case ->
                    // Navigate to case details or sessions for this case
                },
                onDeleteCaseClick = { case ->
                    viewModel.deleteCaseWithSessions(case.caseId)
                },
                onSearchQuery = { query ->
                    // TODO: Implement case search
                }
            )
        }
    }
}
