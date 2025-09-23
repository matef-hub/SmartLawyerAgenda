package com.example.smartlawyeragenda.repository

import androidx.room.withTransaction
import com.example.smartlawyeragenda.data.AppDatabase
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainRepository(
    private val database: AppDatabase
) {
    private val caseDao = database.caseDao()
    private val sessionDao = database.sessionDao()

    // -------------------- Case operations --------------------
    fun getAllCases(): Flow<List<CaseEntity>> = caseDao.getAllCases()

    suspend fun getCaseById(caseId: Long): CaseEntity? = caseDao.getCaseById(caseId)

    suspend fun getCaseByNumber(caseNumber: String): CaseEntity? = caseDao.getCaseByNumber(caseNumber)

    fun searchCases(query: String): Flow<List<CaseEntity>> = caseDao.searchCases(query)

    suspend fun insertCase(case: CaseEntity): Long {
        require(case.isValid()) { "Invalid case data" }
        return caseDao.insertCase(case)
    }

    suspend fun updateCase(case: CaseEntity) {
        require(case.isValid()) { "Invalid case data" }
        caseDao.updateCase(case)
    }

    suspend fun deleteCase(case: CaseEntity) = caseDao.deleteCase(case)

    suspend fun deleteCaseById(caseId: Long) = caseDao.deleteCaseById(caseId)

    suspend fun isCaseNumberExists(caseNumber: String, excludeCaseId: Long = 0): Boolean =
        caseDao.isCaseNumberExists(caseNumber, excludeCaseId) > 0

    fun getCasesWithUpcomingSessions(): Flow<List<CaseEntity>> =
        caseDao.getCasesWithUpcomingSessions(System.currentTimeMillis())

    // -------------------- Session operations --------------------
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
        require(session.isValid()) { "Invalid session data" }

        if (isSessionExists(session.caseId, session.sessionDate, 0)) {
            throw IllegalStateException("Session already exists for this case and date")
        }

        return sessionDao.insertSession(session)
    }

    suspend fun insertSessions(sessions: List<SessionEntity>): List<Long> {
        sessions.forEach { session ->
            require(session.isValid()) { "Invalid session data in list" }
        }
        return sessionDao.insertSessions(sessions)
    }

    suspend fun updateSession(session: SessionEntity) {
        require(session.isValid()) { "Invalid session data" }
        sessionDao.updateSession(session)
    }

    suspend fun deleteSession(session: SessionEntity) = sessionDao.deleteSession(session)

    suspend fun deleteSessionById(sessionId: Long) = sessionDao.deleteSessionById(sessionId)

    suspend fun deleteSessionsByCaseId(caseId: Long) = sessionDao.deleteSessionsByCaseId(caseId)

    fun getAllSessions(): Flow<List<SessionEntity>> = sessionDao.getAllSessions()

    suspend fun isSessionExists(caseId: Long, sessionDate: Long, excludeSessionId: Long = 0): Boolean =
        sessionDao.isSessionExists(caseId, sessionDate, excludeSessionId) > 0

    // -------------------- Combined operations --------------------
    suspend fun saveCaseWithSession(
        case: CaseEntity,
        session: SessionEntity,
        createNextSession: Boolean = false,
        nextSessionDate: Long? = null
    ): Pair<Long, Long> {
        val caseId = if (case.caseId == 0L) {
            insertCase(case)
        } else {
            updateCase(case)
            case.caseId
        }

        val sessionWithCaseId = session.copy(caseId = caseId)
        val sessionId = insertSession(sessionWithCaseId)

        if (createNextSession && nextSessionDate != null && !isSessionExists(caseId, nextSessionDate)) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateStr = Instant.ofEpochMilli(session.sessionDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter)

            val nextSession = SessionEntity(
                caseId = caseId,
                sessionDate = nextSessionDate,
                fromSession = "مؤجلة من $dateStr",
                status = SessionStatus.SCHEDULED
            )
            insertSession(nextSession)
        }

        return Pair(caseId, sessionId)
    }

    suspend fun deleteCaseWithSessions(caseId: Long) {
        deleteSessionsByCaseId(caseId)
        deleteCaseById(caseId)
    }

    // -------------------- Statistics --------------------
    suspend fun getTodaySessionsCount(): Int = getTodaySessions().first().size

    suspend fun getUpcomingSessionsCount(): Int = getUpcomingSessions().first().size

    suspend fun getTotalCasesCount(): Int = caseDao.getCasesCount()

    suspend fun getTotalSessionsCount(): Int = getAllSessions().first().size

    suspend fun getActivesCasesCount(): Int = caseDao.getActiveCasesCount()

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

    suspend fun getOverallStatistics(): OverallStatistics = OverallStatistics(
        totalCases = getTotalCasesCount(),
        activeCases = getActivesCasesCount(),
        totalSessions = getTotalSessionsCount(),
        todaySessions = getTodaySessionsCount(),
        upcomingSessions = getUpcomingSessionsCount()
    )

    // -------------------- Data management --------------------
    suspend fun deleteAllData() {
        sessionDao.deleteAllSessions()
        caseDao.deleteAllCases()
    }

    suspend fun exportData(): DatabaseExport = DatabaseExport(
        cases = getAllCases().first(),
        sessions = getAllSessions().first(),
        exportDate = System.currentTimeMillis()
    )

    suspend fun importData(export: DatabaseExport): ImportResult {
        return database.withTransaction {
            var importedCases = 0
            var importedSessions = 0
            var skippedCases = 0
            var skippedSessions = 0

            try {
                // Use DAO directly inside transaction
                val existingCaseNumbers = caseDao.getAllCases().first().map { it.caseNumber }.toSet()
                val caseNumberToIdMap = mutableMapOf<String, Long>()

                // Import cases using DAO directly
                for (case in export.cases) {
                    try {
                        if (!existingCaseNumbers.contains(case.caseNumber)) {
                            val newId = caseDao.insertCase(case.copy(caseId = 0))
                            caseNumberToIdMap[case.caseNumber] = newId
                            importedCases++
                        } else {
                            val existing = caseDao.getCaseByNumber(case.caseNumber)
                            if (existing != null) {
                                caseDao.updateCase(case.copy(caseId = existing.caseId))
                                caseNumberToIdMap[case.caseNumber] = existing.caseId
                            }
                            skippedCases++
                        }
                    } catch (_: Exception) {
                        skippedCases++
                    }
                }

                // Import sessions using DAO directly
                for (session in export.sessions) {
                    try {
                        val originalCase = export.cases.find { it.caseId == session.caseId }
                        if (originalCase != null) {
                            val newCaseId = caseNumberToIdMap[originalCase.caseNumber]
                            if (newCaseId != null) {
                                // Use DAO directly for duplicate check
                                val existingSession = sessionDao.getSessionByCaseAndDate(newCaseId, session.sessionDate)
                                if (existingSession == null) {
                                    sessionDao.insertSession(session.copy(sessionId = 0, caseId = newCaseId))
                                    importedSessions++
                                } else {
                                    skippedSessions++
                                }
                            } else {
                                skippedSessions++
                            }
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
                    importedCases = importedCases,
                    importedSessions = importedSessions,
                    skippedCases = skippedCases,
                    skippedSessions = skippedSessions,
                    error = e.message
                )
            }
        }
    }

    // -------------------- Utility (java.time) --------------------
    private fun getTodayBounds(): Pair<Long, Long> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return Pair(startOfDay, endOfDay)
    }

    private fun getDateBounds(dateMillis: Long): Pair<Long, Long> {
        val date = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return Pair(startOfDay, endOfDay)
    }

    fun getSessionsForDate(dateMillis: Long): Flow<List<SessionEntity>> {
        val (startOfDay, endOfDay) = getDateBounds(dateMillis)
        return getSessionsByDay(startOfDay, endOfDay)
    }

    fun getSessionsForWeek(weekStartMillis: Long): Flow<List<SessionEntity>> {
        val weekStart = Instant.ofEpochMilli(weekStartMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val monday = weekStart.with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)

        val startMillis = monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = sunday.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return getSessionsByDateRange(startMillis, endMillis)
    }

    fun getSessionsForMonth(monthStartMillis: Long): Flow<List<SessionEntity>> {
        val monthStart = Instant.ofEpochMilli(monthStartMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val firstDay = monthStart.withDayOfMonth(1)
        val lastDay = firstDay.plusMonths(1).minusDays(1)

        val startMillis = firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = lastDay.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return getSessionsByDateRange(startMillis, endMillis)
    }

    // -------------------- Sample data --------------------
    suspend fun populateSampleData() {
        if (getTotalCasesCount() > 0) return

        val (sampleCases, sampleSessions) = com.example.smartlawyeragenda.data.SampleDataGenerator.generateSampleData()

        sampleCases.forEach { case -> insertCase(case) }
        sampleSessions.forEach { session -> insertSession(session) }
    }

    suspend fun clearAllData() {
        sessionDao.deleteAllSessions()
        caseDao.deleteAllCases()
    }
}

// -------------------- Data classes --------------------
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
