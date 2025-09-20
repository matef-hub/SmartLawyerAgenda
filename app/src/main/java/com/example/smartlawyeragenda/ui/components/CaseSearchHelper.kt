package com.example.smartlawyeragenda.ui.components

import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.repository.CaseStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Helper class for case search functionality
 */
object CaseSearchHelper {
    
    /**
     * Search cases by query
     */
    fun searchCases(
        cases: List<CaseEntity>,
        query: String
    ): List<CaseEntity> {
        if (query.isBlank()) return cases
        
        val lowercaseQuery = query.lowercase()
        
        return cases.filter { case ->
            case.caseNumber.lowercase().contains(lowercaseQuery) ||
            case.clientName.lowercase().contains(lowercaseQuery) ||
            case.opponentName?.lowercase()?.contains(lowercaseQuery) == true ||
            case.caseType?.lowercase()?.contains(lowercaseQuery) == true ||
            case.caseDescription?.lowercase()?.contains(lowercaseQuery) == true ||
            case.rollNumber?.lowercase()?.contains(lowercaseQuery) == true
        }
    }
    
    /**
     * Filter cases by status
     */
    fun filterCasesByStatus(
        cases: List<CaseEntity>,
        showActiveOnly: Boolean
    ): List<CaseEntity> {
        return if (showActiveOnly) {
            cases.filter { it.isActive }
        } else {
            cases
        }
    }
    
    /**
     * Sort cases by different criteria
     */
    fun sortCases(
        cases: List<CaseEntity>,
        sortBy: CaseSortCriteria
    ): List<CaseEntity> {
        return when (sortBy) {
            CaseSortCriteria.CREATED_DATE_DESC -> cases.sortedByDescending { it.createdAt }
            CaseSortCriteria.CREATED_DATE_ASC -> cases.sortedBy { it.createdAt }
            CaseSortCriteria.CASE_NUMBER_ASC -> cases.sortedBy { it.caseNumber }
            CaseSortCriteria.CASE_NUMBER_DESC -> cases.sortedByDescending { it.caseNumber }
            CaseSortCriteria.CLIENT_NAME_ASC -> cases.sortedBy { it.clientName }
            CaseSortCriteria.CLIENT_NAME_DESC -> cases.sortedByDescending { it.clientName }
            CaseSortCriteria.STATUS -> cases.sortedWith(compareBy<CaseEntity> { !it.isActive }.thenBy { it.caseNumber })
        }
    }
    
    /**
     * Get case statistics for filtered cases
     */
    fun getCaseStatistics(
        cases: List<CaseEntity>,
        caseStatistics: Map<Long, CaseStatistics>
    ): CaseSearchStatistics {
        val activeCases = cases.count { it.isActive }
        val inactiveCases = cases.count { !it.isActive }
        val totalCases = cases.size
        
        val casesWithSessions = cases.count { case ->
            caseStatistics[case.caseId]?.totalSessions ?: 0 > 0
        }
        
        val casesWithUpcomingSessions = cases.count { case ->
            caseStatistics[case.caseId]?.upcomingSessionsCount ?: 0 > 0
        }
        
        return CaseSearchStatistics(
            totalCases = totalCases,
            activeCases = activeCases,
            inactiveCases = inactiveCases,
            casesWithSessions = casesWithSessions,
            casesWithUpcomingSessions = casesWithUpcomingSessions
        )
    }
    
    /**
     * Get search suggestions based on query
     */
    fun getSearchSuggestions(
        cases: List<CaseEntity>,
        query: String,
        maxSuggestions: Int = 5
    ): List<String> {
        if (query.length < 2) return emptyList()
        
        val lowercaseQuery = query.lowercase()
        val suggestions = mutableSetOf<String>()
        
        cases.forEach { case ->
            // Add case number suggestions
            if (case.caseNumber.lowercase().contains(lowercaseQuery)) {
                suggestions.add(case.caseNumber)
            }
            
            // Add client name suggestions
            if (case.clientName.lowercase().contains(lowercaseQuery)) {
                suggestions.add(case.clientName)
            }
            
            // Add opponent name suggestions
            case.opponentName?.let { opponentName ->
                if (opponentName.lowercase().contains(lowercaseQuery)) {
                    suggestions.add(opponentName)
                }
            }
            
            // Add case type suggestions
            case.caseType?.let { caseType ->
                if (caseType.lowercase().contains(lowercaseQuery)) {
                    suggestions.add(caseType)
                }
            }
        }
        
        return suggestions.take(maxSuggestions)
    }
}

/**
 * Enum for case sort criteria
 */
enum class CaseSortCriteria {
    CREATED_DATE_DESC,
    CREATED_DATE_ASC,
    CASE_NUMBER_ASC,
    CASE_NUMBER_DESC,
    CLIENT_NAME_ASC,
    CLIENT_NAME_DESC,
    STATUS
}

/**
 * Data class for case search statistics
 */
data class CaseSearchStatistics(
    val totalCases: Int,
    val activeCases: Int,
    val inactiveCases: Int,
    val casesWithSessions: Int,
    val casesWithUpcomingSessions: Int
)
