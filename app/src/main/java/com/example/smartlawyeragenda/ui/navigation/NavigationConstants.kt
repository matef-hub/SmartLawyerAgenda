package com.example.smartlawyeragenda.ui.navigation

/**
 * Navigation constants and route definitions for the Smart Lawyer Agenda app
 */
object NavigationConstants {
    
    // Route names
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val AGENDA_ROUTE = "agenda"
    const val ADD_SESSION_ROUTE = "add_session"
    const val EDIT_SESSION_ROUTE = "edit_session"
    const val SETTINGS_ROUTE = "settings"
    const val CASES_ROUTE = "cases"
    const val ADD_CASE_ROUTE = "add_case"
    const val EDIT_CASE_ROUTE = "edit_case"
    
    // Parameter names
    const val SESSION_ID_PARAM = "sessionId"
    const val CASE_ID_PARAM = "caseId"
    
    // Route patterns with parameters
    const val ADD_SESSION_WITH_CASE_ROUTE = "add_session/{$CASE_ID_PARAM}"
    const val EDIT_SESSION_WITH_ID_ROUTE = "edit_session/{$SESSION_ID_PARAM}"
    const val ADD_CASE_ROUTE_PATTERN = "add_case"
    const val EDIT_CASE_WITH_ID_ROUTE = "edit_case/{$CASE_ID_PARAM}"
    
    // Navigation arguments
    object Arguments {
        const val SESSION_ID = "sessionId"
        const val CASE_ID = "caseId"
    }
    
    // Navigation actions
    object Actions {
        fun navigateToAddSession(caseId: Long = 0L): String {
            return if (caseId > 0L) {
                "add_session/$caseId"
            } else {
                "add_session"
            }
        }
        
        fun navigateToEditSession(sessionId: Long): String {
            return "edit_session/$sessionId"
        }
        
        fun navigateToAddCase(): String {
            return "add_case"
        }
        
        fun navigateToEditCase(caseId: Long): String {
            return "edit_case/$caseId"
        }
    }
}
