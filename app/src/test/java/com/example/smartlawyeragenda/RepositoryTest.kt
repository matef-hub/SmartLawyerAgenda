package com.example.smartlawyeragenda

import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.repository.CaseStatistics
import com.example.smartlawyeragenda.ui.components.CaseSearchHelper
import com.example.smartlawyeragenda.ui.components.CaseSortCriteria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryTest {

    @Test
    fun caseEntity_validation_requires_case_number_and_client_name() {
        val valid = CaseEntity(caseNumber = "CASE-1", clientName = "Client")
        val missingCaseNumber = CaseEntity(caseNumber = "", clientName = "Client")
        val missingClient = CaseEntity(caseNumber = "CASE-2", clientName = "")

        assertTrue(valid.isValid())
        assertFalse(missingCaseNumber.isValid())
        assertFalse(missingClient.isValid())
    }

    @Test
    fun sessionEntity_validation_requires_positive_case_id_and_date() {
        val valid = SessionEntity(caseId = 1L, sessionDate = 1_700_000_000_000L)
        val invalidCase = SessionEntity(caseId = 0L, sessionDate = 1_700_000_000_000L)
        val invalidDate = SessionEntity(caseId = 1L, sessionDate = 0L)

        assertTrue(valid.isValid())
        assertFalse(invalidCase.isValid())
        assertFalse(invalidDate.isValid())
    }

    @Test
    fun caseSearchHelper_matches_case_fields() {
        val cases = listOf(
            CaseEntity(caseId = 1, caseNumber = "A-100", clientName = "Ali", opponentName = "Nour", rollNumber = "R1"),
            CaseEntity(caseId = 2, caseNumber = "B-200", clientName = "Sara", opponentName = "Hany", rollNumber = "R2")
        )

        val byCaseNumber = CaseSearchHelper.searchCases(cases, "A-100")
        val byClient = CaseSearchHelper.searchCases(cases, "sara")
        val byRoll = CaseSearchHelper.searchCases(cases, "r1")

        assertEquals(1, byCaseNumber.size)
        assertEquals(2L, byClient.single().caseId)
        assertEquals(1L, byRoll.single().caseId)
    }

    @Test
    fun caseSearchHelper_sorts_by_case_number_ascending() {
        val cases = listOf(
            CaseEntity(caseId = 2, caseNumber = "B-200", clientName = "Client B"),
            CaseEntity(caseId = 1, caseNumber = "A-100", clientName = "Client A")
        )

        val sorted = CaseSearchHelper.sortCases(cases, CaseSortCriteria.CASE_NUMBER_ASC)

        assertEquals(listOf("A-100", "B-200"), sorted.map { it.caseNumber })
    }

    @Test
    fun caseSearchHelper_statistics_are_aggregated_correctly() {
        val cases = listOf(
            CaseEntity(caseId = 1, caseNumber = "A-100", clientName = "Client A", isActive = true),
            CaseEntity(caseId = 2, caseNumber = "B-200", clientName = "Client B", isActive = false)
        )

        val stats = mapOf(
            1L to CaseStatistics(
                case = cases[0],
                totalSessions = 3,
                latestSessionDate = 1_700_000_000_000L,
                upcomingSessionsCount = 1,
                completedSessionsCount = 1,
                postponedSessionsCount = 1
            ),
            2L to CaseStatistics(
                case = cases[1],
                totalSessions = 0,
                latestSessionDate = null,
                upcomingSessionsCount = 0,
                completedSessionsCount = 0,
                postponedSessionsCount = 0
            )
        )

        val aggregate = CaseSearchHelper.getCaseStatistics(cases, stats)

        assertEquals(2, aggregate.totalCases)
        assertEquals(1, aggregate.activeCases)
        assertEquals(1, aggregate.inactiveCases)
        assertEquals(1, aggregate.casesWithSessions)
        assertEquals(1, aggregate.casesWithUpcomingSessions)
    }
}
