package com.example.smartlawyeragenda.repository

import com.example.smartlawyeragenda.data.AppDatabase
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
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

    fun searchCases(query: String): Flow<List<CaseEntity>> = caseDao.searchCases(query)

    suspend fun insertCase(case: CaseEntity): Long {
        // Validate case before inserting
        if (!case.isValid()) {
            throw IllegalArgumentException("Invalid case data")
        }
        return caseDao.insertCase(case)
    }

    suspend fun updateCase(case: CaseEntity) {
        if (!case.isValid()) {
            throw IllegalArgumentException("Invalid case data")
        }
        caseDao.updateCase(case)
    }

    suspend fun deleteCase(case: CaseEntity) = caseDao.deleteCase(case)

    suspend fun deleteCaseById(caseId: Long) = caseDao.deleteCaseById(caseId)

    suspend fun isCaseNumberExists(caseNumber: String, excludeCaseId: Long = 0): Boolean {
        return caseDao.isCaseNumberExists(caseNumber, excludeCaseId) > 0
    }

    fun getCasesWithUpcomingSessions(): Flow<List<CaseEntity>> {
        val today = System.currentTimeMillis()
        return caseDao.getCasesWithUpcomingSessions(today)
    }

    // -------------------------
    // Session operations
    // -------------------------
    fun getSessionsByDateRange(startDate: Long, endDate: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsByDateRange(startDate, endDate)

    fun getSessionsByDay(dayStartMillis: Long, dayEndMillis: Long): Flow<List<SessionEntity>> =
        sessionDao.getSessionsByDay(dayStartMillis, dayEndMillis)

    suspend fun getSessionByCaseAndDate(caseId: Long, sessionDate: Long): SessionEntity? =
        sessionDao.getSessionByCaseAndDate(caseId, sessionDate)

    suspend fun getSessionById(sessionId: Long): SessionEntity? = sessionDao.getSessionById(sessionId)

    fun getSessionsByCaseId(caseId: Long): Flow<List<SessionEntity>> = sessionDao.getSessionsByCaseId(caseId)

    suspend fun getLatestSessionForCase(caseId: Long): SessionEntity? = sessionDao.getLatestSessionForCase(caseId)

    suspend fun getSessionsCountForCase(caseId: Long): Int = sessionDao.getSessionsCountForCase(caseId)

    fun getUpcomingSessions(fromDate: Long = System.currentTimeMillis()): Flow<List<SessionEntity>> =
        sessionDao.getUpcomingSessions(fromDate)

    fun getPastSessions(beforeDate: Long = System.currentTimeMillis()): Flow<List<SessionEntity>> =
        sessionDao.getPastSessions(beforeDate)

    fun getTodaySessions(): Flow<List<SessionEntity>> {
        val (startOfDay, endOfDay) = getTodayBounds()
        return sessionDao.getTodaySessions(startOfDay, endOfDay)
    }

    fun searchSessions(query: String): Flow<List<SessionEntity>> = sessionDao.searchSessions(query)

    suspend fun insertSession(session: SessionEntity): Long {
        // Validate session before inserting
        if (!session.isValid()) {
            throw IllegalArgumentException("Invalid session data")
        }

        // Check for duplicates
        if (isSessionExists(session.caseId, session.sessionDate, session.sessionId)) {
            throw IllegalStateException("Session already exists for this case and date")
        }

        return sessionDao.insertSession(session)
    }

    suspend fun insertSessions(sessions: List<SessionEntity>): List<Long> {
        sessions.forEach { session ->
            if (!session.isValid()) {
                throw IllegalArgumentException("Invalid session data in list")
            }
        }
        return sessionDao.insertSessions(sessions)
    }

    suspend fun updateSession(session: SessionEntity) {
        if (!session.isValid()) {
            throw IllegalArgumentException("Invalid session data")
        }
        sessionDao.updateSession(session)
    }

    suspend fun deleteSession(session: SessionEntity) = sessionDao.deleteSession(session)

    suspend fun deleteSessionById(sessionId: Long) = sessionDao.deleteSessionById(sessionId)

    suspend fun deleteSessionsByCaseId(caseId: Long) = sessionDao.deleteSessionsByCaseId(caseId)

    fun getAllSessions(): Flow<List<SessionEntity>> = sessionDao.getAllSessions()

    suspend fun isSessionExists(caseId: Long, sessionDate: Long, excludeSessionId: Long = 0): Boolean {
        return sessionDao.isSessionExists(caseId, sessionDate, excludeSessionId) > 0
    }

    // -------------------------
    // Combined operations
    // -------------------------
    suspend fun saveCaseWithSession(
        case: CaseEntity,
        session: SessionEntity,
        createNextSession: Boolean = false,
        nextSessionDate: Long? = null
    ): Pair<Long, Long> {
        // Insert or update case
        val caseId = if (case.caseId == 0L) {
            insertCase(case)
        } else {
            updateCase(case)
            case.caseId
        }

        // Insert session with the case ID
        val sessionWithCaseId = session.copy(caseId = caseId)
        val sessionId = insertSession(sessionWithCaseId)

        // Auto-create next session if requested
        if (createNextSession && nextSessionDate != null && !isSessionExists(caseId, nextSessionDate)) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val nextSession = SessionEntity(
                caseId = caseId,
                sessionDate = nextSessionDate,
                fromSession = "مؤجلة من ${dateFormat.format(Date(session.sessionDate))}",
                status = SessionStatus.SCHEDULED
            )
            insertSession(nextSession)
        }

        return Pair(caseId, sessionId)
    }

    // Delete case with all its sessions
    suspend fun deleteCaseWithSessions(caseId: Long) {
        deleteSessionsByCaseId(caseId)
        deleteCaseById(caseId)
    }

    // -------------------------
    // Statistics and Analytics
    // -------------------------
    suspend fun getTodaySessionsCount(): Int {
        return getTodaySessions().first().size
    }

    suspend fun getUpcomingSessionsCount(): Int {
        return getUpcomingSessions().first().size
    }

    suspend fun getTotalCasesCount(): Int {
        return caseDao.getCasesCount()
    }

    suspend fun getTotalSessionsCount(): Int {
        return getAllSessions().first().size
    }

    suspend fun getActivesCasesCount(): Int {
        return getAllCases().first().count { it.isActive }
    }

    suspend fun getCaseStatistics(caseId: Long): CaseStatistics? {
        val case = getCaseById(caseId) ?: return null
        val sessionsCount = getSessionsCountForCase(caseId)
        val latestSession = getLatestSessionForCase(caseId)
        val upcomingSessions = getUpcomingSessions().first().filter { it.caseId == caseId }
        val pastSessions = getPastSessions().first().filter { it.caseId == caseId }

        return CaseStatistics(
            case = case,
            totalSessions = sessionsCount,
            latestSessionDate = latestSession?.sessionDate,
            upcomingSessionsCount = upcomingSessions.size,
            completedSessionsCount = pastSessions.count { it.status == SessionStatus.COMPLETED },
            postponedSessionsCount = pastSessions.count { it.status == SessionStatus.POSTPONED }
        )
    }

    suspend fun getOverallStatistics(): OverallStatistics {
        val totalCases = getTotalCasesCount()
        val activeCases = getActivesCasesCount()
        val totalSessions = getTotalSessionsCount()
        val todaySessions = getTodaySessionsCount()
        val upcomingSessions = getUpcomingSessionsCount()

        return OverallStatistics(
            totalCases = totalCases,
            activeCases = activeCases,
            totalSessions = totalSessions,
            todaySessions = todaySessions,
            upcomingSessions = upcomingSessions
        )
    }

    // -------------------------
    // Data management
    // -------------------------
    suspend fun deleteAllData() {
        sessionDao.deleteAllSessions()
        caseDao.deleteAllCases()
    }

    suspend fun exportData(): DatabaseExport {
        val cases = getAllCases().first()
        val sessions = getAllSessions().first()
        return DatabaseExport(
            cases = cases,
            sessions = sessions,
            exportDate = System.currentTimeMillis()
        )
    }

    suspend fun importData(export: DatabaseExport): ImportResult {
        return try {
            var importedCases = 0
            var importedSessions = 0
            var skippedCases = 0
            var skippedSessions = 0

            // Import cases
            export.cases.forEach { case ->
                try {
                    if (!isCaseNumberExists(case.caseNumber)) {
                        insertCase(case.copy(caseId = 0)) // Reset ID for new insertion
                        importedCases++
                    } else {
                        skippedCases++
                    }
                } catch (_: Exception) {
                    skippedCases++
                }
            }

            // Import sessions
            export.sessions.forEach { session ->
                try {
                    val case = getCaseByNumber(export.cases.find { it.caseId == session.caseId }?.caseNumber ?: "")
                    if (case != null && !isSessionExists(case.caseId, session.sessionDate)) {
                        insertSession(session.copy(sessionId = 0, caseId = case.caseId))
                        importedSessions++
                    } else {
                        skippedSessions++
                    }
                } catch (_: Exception) {
                    skippedSessions++
                }
            }

            ImportResult(
                success = true,
                importedCases = importedCases,
                importedSessions = importedSessions,
                skippedCases = skippedCases,
                skippedSessions = skippedSessions,
                error = null
            )
        } catch (e: Exception) {
            ImportResult(
                success = false,
                importedCases = 0,
                importedSessions = 0,
                skippedCases = 0,
                skippedSessions = 0,
                error = e.message
            )
        }
    }

    // -------------------------
    // Utility methods
    // -------------------------
    private fun getTodayBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // Start of today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // End of today
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }

    private fun getDateBounds(dateMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis

        // Start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // End of day
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }

    fun getSessionsForDate(dateMillis: Long): Flow<List<SessionEntity>> {
        val (startOfDay, endOfDay) = getDateBounds(dateMillis)
        return getSessionsByDay(startOfDay, endOfDay)
    }

    // Get sessions for a specific week
    fun getSessionsForWeek(weekStartMillis: Long): Flow<List<SessionEntity>> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weekStartMillis

        // Start of week (assuming Monday is first day)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis

        // End of week (Sunday)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val weekEnd = calendar.timeInMillis

        return getSessionsByDateRange(weekStart, weekEnd)
    }

    // Get sessions for a specific month
    fun getSessionsForMonth(monthStartMillis: Long): Flow<List<SessionEntity>> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = monthStartMillis

        // Start of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis

        // End of month
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val monthEnd = calendar.timeInMillis

        return getSessionsByDateRange(monthStart, monthEnd)
    }

    // -------------------------
    // Sample Data Operations
    // -------------------------
    suspend fun populateSampleData() {
        try {
            // Check if data already exists
            val existingCasesCount = getTotalCasesCount()
            if (existingCasesCount > 0) {
                return // Don't populate if data already exists
            }

            val (sampleCases, sampleSessions) = com.example.smartlawyeragenda.data.SampleDataGenerator.generateSampleData()

            // Insert sample cases
            sampleCases.forEach { case ->
                insertCase(case)
            }

            // Insert sample sessions
            sampleSessions.forEach { session ->
                insertSession(session)
            }
        } catch (e: Exception) {
            throw Exception("Failed to populate sample data: ${e.message}")
        }
    }

    suspend fun clearAllData() {
        try {
            // Delete all sessions first (due to foreign key constraints)
            sessionDao.deleteAllSessions()
            // Then delete all cases
            caseDao.deleteAllCases()
        } catch (e: Exception) {
            throw Exception("Failed to clear data: ${e.message}")
        }
    }
}

// Data classes for statistics and operations
data class CaseStatistics(
    val case: CaseEntity,
    val totalSessions: Int,
    val latestSessionDate: Long?,
    val upcomingSessionsCount: Int,
    val completedSessionsCount: Int,
    val postponedSessionsCount: Int
)

data class OverallStatistics(
    val totalCases: Int,
    val activeCases: Int,
    val totalSessions: Int,
    val todaySessions: Int,
    val upcomingSessions: Int
)

data class DatabaseExport(
    val cases: List<CaseEntity>,
    val sessions: List<SessionEntity>,
    val exportDate: Long
)

data class ImportResult(
    val success: Boolean,
    val importedCases: Int,
    val importedSessions: Int,
    val skippedCases: Int,
    val skippedSessions: Int,
    val error: String?
)