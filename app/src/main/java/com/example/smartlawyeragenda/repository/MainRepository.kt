package com.example.smartlawyeragenda.repository

import com.example.smartlawyeragenda.data.AppDatabase
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class MainRepository(
    private val database: AppDatabase
) {
    private val caseDao = database.caseDao()
    private val sessionDao = database.sessionDao()

    // -------------------------
    // Case operations
    // -------------------------
    fun getAllCases(): Flow<List<CaseEntity>> = caseDao.getAllCases()
    suspend fun getCaseById(caseId: Long): CaseEntity? = caseDao.getCaseById(caseId)
    suspend fun getCaseByNumber(caseNumber: String): CaseEntity? = caseDao.getCaseByNumber(caseNumber)
    suspend fun insertCase(case: CaseEntity): Long = caseDao.insertCase(case)
    suspend fun updateCase(case: CaseEntity) = caseDao.updateCase(case)
    suspend fun deleteCase(case: CaseEntity) = caseDao.deleteCase(case)
    suspend fun deleteCaseById(caseId: Long) = caseDao.deleteCaseById(caseId)

    // -------------------------
    // Session operations
    // -------------------------
    fun getSessionsByDate(dateStartMillis: Long, dateEndMillis: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsByDate(dateStartMillis, dateEndMillis)

    suspend fun existsSession(caseId: Long, sessionDate: Long): SessionEntity? =
        sessionDao.existsSession(caseId, sessionDate)

    suspend fun getSessionById(sessionId: Long): SessionEntity? = sessionDao.getSessionById(sessionId)
    
    fun getSessionsByCaseId(caseId: Long): Flow<List<SessionEntity>> = sessionDao.getSessionsByCaseId(caseId)
    
    suspend fun getLatestSession(caseId: Long): SessionEntity? = sessionDao.getLatestSession(caseId)
    
    suspend fun getSessionsCountForCase(caseId: Long): Int = sessionDao.getSessionsCountForCase(caseId)
    
    fun getUpcomingSessions(todayMillis: Long): Flow<List<SessionEntity>> = sessionDao.getUpcomingSessions(todayMillis)
    
    suspend fun insertSession(session: SessionEntity): Long = sessionDao.insertSession(session)
    
    suspend fun updateSession(session: SessionEntity) = sessionDao.updateSession(session)
    
    suspend fun deleteSession(session: SessionEntity) = sessionDao.deleteSession(session)
    
    suspend fun deleteSessionById(sessionId: Long) = sessionDao.deleteSessionById(sessionId)
    
    fun getAllSessions(): Flow<List<SessionEntity>> = sessionDao.getAllSessions()
    
    suspend fun deleteAllSessions() = sessionDao.deleteAllSessions()
    suspend fun deleteAllCases() = caseDao.deleteAllCases()
    // -------------------------
    // Case deletion with sessions
    // -------------------------
    suspend fun deleteCaseWithSessions(caseId: Long) {
        // First delete all sessions for this case
        val sessions = getSessionsByCaseId(caseId).first()
        sessions.forEach { session ->
            deleteSession(session)
        }
        // Then delete the case
        deleteCaseById(caseId)
    }

    // -------------------------
    // Combined operations
    // -------------------------
    suspend fun saveSessionWithAutoNext(
        case: CaseEntity,
        session: SessionEntity,
        nextSessionDate: Long?
    ) {
        // Insert or update case
        val caseId = if (case.caseId == 0L) {
            insertCase(case)
        } else {
            updateCase(case)
            case.caseId
        }

        // Insert session with the case ID
        val sessionWithCaseId = session.copy(caseId = caseId)
        insertSession(sessionWithCaseId)

        // Auto-create next session if specified and doesn't exist
        if (nextSessionDate != null && existsSession(caseId, nextSessionDate) == null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val nextSession = SessionEntity(
                caseId = caseId,
                sessionDate = nextSessionDate,
                fromSession = "مؤجلة من ${dateFormat.format(Date(session.sessionDate))}",
                decision = null
            )
            insertSession(nextSession)
        }
    }
    
    // -------------------------
    // Statistics and Analytics
    // -------------------------
    suspend fun getTodaySessionsCount(): Int {
        val today = System.currentTimeMillis()
        val startOfDay = getStartOfDay(today)
        val endOfDay = getEndOfDay(today)
        return getSessionsByDate(startOfDay, endOfDay).first().size
    }
    
    suspend fun getUpcomingSessionsCount(): Int {
        val today = System.currentTimeMillis()
        return getUpcomingSessions(today).first().size
    }
    
    suspend fun getTotalCasesCount(): Int {
        return getAllCases().first().size
    }
    
    suspend fun getTotalSessionsCount(): Int {
        return getAllSessions().first().size
    }
    
    suspend fun getCaseStatistics(caseId: Long): CaseStatistics {
        val sessionsCount = getSessionsCountForCase(caseId)
        val latestSession = getLatestSession(caseId)
        val upcomingSessions = getUpcomingSessions(System.currentTimeMillis()).first()
            .filter { it.caseId == caseId }
        
        return CaseStatistics(
            caseId = caseId,
            totalSessions = sessionsCount,
            latestSessionDate = latestSession?.sessionDate,
            upcomingSessionsCount = upcomingSessions.size
        )
    }
    
    // -------------------------
    // Search functionality
    // -------------------------
    suspend fun searchCases(query: String): List<CaseEntity> {
        val allCases = getAllCases().first()
        return allCases.filter { case ->
            case.caseNumber.contains(query, ignoreCase = true) ||
            case.clientName.contains(query, ignoreCase = true) ||
            case.opponentName!!.contains(query, ignoreCase = true) ||
            (case.rollNumber?.contains(query, ignoreCase = true) ?: false)
        }
    }
    
    suspend fun searchSessions(query: String): List<SessionEntity> {
        val allSessions = getAllSessions().first()
        return allSessions.filter { session ->
            (session.reason?.contains(query, ignoreCase = true) ?: false) ||
            (session.decision?.contains(query, ignoreCase = true) ?: false) ||
            (session.fromSession?.contains(query, ignoreCase = true) ?: false)
        }
    }
    
    // -------------------------
    // Utility methods
    // -------------------------
    private fun getStartOfDay(dateMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getEndOfDay(dateMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}

// Data class for case statistics
data class CaseStatistics(
    val caseId: Long,
    val totalSessions: Int,
    val latestSessionDate: Long?,
    val upcomingSessionsCount: Int
)
