package com.example.smartlawyeragenda.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity

/**
 * Helper class for navigation operations with proper error handling
 */
object NavigationHelper {
    
    /**
     * Navigate to add session screen with optional caseId
     */
    fun navigateToAddSession(
        navController: NavController,
        caseId: Long? = null,
        navOptions: NavOptions? = null
    ) {
        try {
            val route = if (caseId != null && caseId > 0L) {
                NavigationConstants.Actions.navigateToAddSession(caseId)
            } else {
                NavigationConstants.ADD_SESSION_ROUTE
            }
            
            navController.navigate(route, navOptions)
        } catch (e: Exception) {
            // Log error and fallback to basic route
            android.util.Log.e("NavigationHelper", "Error navigating to add session", e)
            navController.navigate(NavigationConstants.ADD_SESSION_ROUTE, navOptions)
        }
    }
    
    /**
     * Navigate to edit session screen with sessionId
     */
    fun navigateToEditSession(
        navController: NavController,
        sessionId: Long,
        navOptions: NavOptions? = null
    ) {
        try {
            if (sessionId <= 0L) {
                throw IllegalArgumentException("Invalid session ID: $sessionId")
            }
            
            val route = NavigationConstants.Actions.navigateToEditSession(sessionId)
            navController.navigate(route, navOptions)
        } catch (e: Exception) {
            android.util.Log.e("NavigationHelper", "Error navigating to edit session", e)
            // Navigate back to agenda as fallback
            navController.navigate(NavigationConstants.AGENDA_ROUTE, navOptions)
        }
    }
    
    /**
     * Navigate to add case screen
     */
    fun navigateToAddCase(
        navController: NavController,
        navOptions: NavOptions? = null
    ) {
        try {
            navController.navigate(NavigationConstants.ADD_CASE_ROUTE, navOptions)
        } catch (e: Exception) {
            android.util.Log.e("NavigationHelper", "Error navigating to add case", e)
            navController.popBackStack()
        }
    }
    
    /**
     * Navigate to edit case screen with caseId
     */
    fun navigateToEditCase(
        navController: NavController,
        caseId: Long,
        navOptions: NavOptions? = null
    ) {
        try {
            if (caseId <= 0L) {
                throw IllegalArgumentException("Invalid case ID: $caseId")
            }
            
            val route = NavigationConstants.Actions.navigateToEditCase(caseId)
            navController.navigate(route, navOptions)
        } catch (e: Exception) {
            android.util.Log.e("NavigationHelper", "Error navigating to edit case", e)
            // Navigate back to cases as fallback
            navController.navigate(NavigationConstants.CASES_ROUTE, navOptions)
        }
    }
    
    /**
     * Navigate to cases screen
     */
    fun navigateToCases(
        navController: NavController,
        navOptions: NavOptions? = null
    ) {
        try {
            navController.navigate(NavigationConstants.CASES_ROUTE, navOptions)
        } catch (e: Exception) {
            android.util.Log.e("NavigationHelper", "Error navigating to cases", e)
            navController.popBackStack()
        }
    }
    
    /**
     * Navigate to settings screen
     */
    fun navigateToSettings(
        navController: NavController,
        navOptions: NavOptions? = null
    ) {
        try {
            navController.navigate(NavigationConstants.SETTINGS_ROUTE, navOptions)
        } catch (e: Exception) {
            android.util.Log.e("NavigationHelper", "Error navigating to settings", e)
            navController.popBackStack()
        }
    }
    
    /**
     * Navigate to agenda screen (main screen)
     */
    fun navigateToAgenda(
        navController: NavController,
        navOptions: NavOptions? = null
    ) {
        try {
            navController.navigate(NavigationConstants.AGENDA_ROUTE, navOptions)
        } catch (e: Exception) {
            android.util.Log.e("NavigationHelper", "Error navigating to agenda", e)
            // This is a critical error, try to clear back stack
            navController.navigate(NavigationConstants.AGENDA_ROUTE) {
                popUpTo(0) { inclusive = false }
            }
        }
    }
    
    /**
     * Safe back navigation with error handling
     */
    fun navigateBack(
        navController: NavController,
        fallbackRoute: String? = null
    ) {
        try {
            if (!navController.popBackStack()) {
                // No back stack, navigate to fallback or agenda
                val route = fallbackRoute ?: NavigationConstants.AGENDA_ROUTE
                navController.navigate(route) {
                    popUpTo(0) { inclusive = false }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("NavigationHelper", "Error navigating back", e)
            // Force navigate to agenda as last resort
            try {
                navController.navigate(NavigationConstants.AGENDA_ROUTE) {
                    popUpTo(0) { inclusive = false }
                }
            } catch (e2: Exception) {
                android.util.Log.e("NavigationHelper", "Critical navigation error", e2)
            }
        }
    }
    
    /**
     * Create safe navigation options
     */
    fun createSafeNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .build()
    }
    
    /**
     * Validate navigation parameters
     */
    object Validation {
        fun isValidSessionId(sessionId: Long): Boolean {
            return sessionId > 0L
        }
        
        fun isValidCaseId(caseId: Long): Boolean {
            return caseId > 0L
        }
        
        fun isValidRoute(route: String?): Boolean {
            return !route.isNullOrBlank() && route.startsWith("/")
        }
    }
}
