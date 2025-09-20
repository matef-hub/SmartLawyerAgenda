package com.example.smartlawyeragenda.data.dao

import androidx.room.*
import com.example.smartlawyeragenda.data.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    // ==================== QUERY METHODS ====================

    // Get sessions by date range
    @Query("SELECT * FROM sessions WHERE sessionDate >= :dateStartMillis AND sessionDate <= :dateEndMillis ORDER BY sessionDate ASC")
    fun getSessionsByDateRange(dateStartMillis: Long, dateEndMillis: Long): Flow<List<SessionEntity>>

    // Get sessions for a specific date (exact day)
    @Query("SELECT * FROM sessions WHERE sessionDate >= :dayStartMillis AND sessionDate < :dayEndMillis ORDER BY sessionDate ASC")
    fun getSessionsByDay(dayStartMillis: Long, dayEndMillis: Long): Flow<List<SessionEntity>>

    // Check if session exists for case on specific date
    @Query("SELECT * FROM sessions WHERE caseId = :caseId AND sessionDate = :sessionDate")
    suspend fun getSessionByCaseAndDate(caseId: Long, sessionDate: Long): SessionEntity?

    // Get session by ID
    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: Long): SessionEntity?

    // Get all sessions for a specific case
    @Query("SELECT * FROM sessions WHERE caseId = :caseId ORDER BY sessionDate DESC")
    fun getSessionsByCaseId(caseId: Long): Flow<List<SessionEntity>>

    // Get latest session for a case
    @Query("SELECT * FROM sessions WHERE caseId = :caseId ORDER BY sessionDate DESC LIMIT 1")
    suspend fun getLatestSessionForCase(caseId: Long): SessionEntity?

    // Get count of sessions for a case
    @Query("SELECT COUNT(*) FROM sessions WHERE caseId = :caseId")
    suspend fun getSessionsCountForCase(caseId: Long): Int

    // Get upcoming sessions (from specified date onwards)
    @Query("SELECT * FROM sessions WHERE sessionDate >= :fromDateMillis ORDER BY sessionDate ASC")
    fun getUpcomingSessions(fromDateMillis: Long): Flow<List<SessionEntity>>

    // Get past sessions (before specified date)
    @Query("SELECT * FROM sessions WHERE sessionDate < :beforeDateMillis ORDER BY sessionDate DESC")
    fun getPastSessions(beforeDateMillis: Long): Flow<List<SessionEntity>>

    // Get all sessions ordered by date (newest first)
    @Query("SELECT * FROM sessions ORDER BY sessionDate DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    // Get sessions with case details (join query)
    @Query("""
        SELECT s.*, c.caseNumber, c.clientName, c.opponentName 
        FROM sessions s 
        INNER JOIN cases c ON s.caseId = c.caseId 
        WHERE s.sessionDate >= :fromDateMillis 
        ORDER BY s.sessionDate ASC
    """)
    fun getSessionsWithCaseDetails(fromDateMillis: Long): Flow<List<SessionWithCaseDetails>>

    // Get sessions for today
    @Query("SELECT * FROM sessions WHERE sessionDate >= :todayStartMillis AND sessionDate < :todayEndMillis ORDER BY sessionDate ASC")
    fun getTodaySessions(todayStartMillis: Long, todayEndMillis: Long): Flow<List<SessionEntity>>

    // Search sessions by reason, decision, or fromSession
    @Query("""
        SELECT * FROM sessions 
        WHERE COALESCE(reason, '') LIKE '%' || :query || '%'
           OR COALESCE(decision, '') LIKE '%' || :query || '%'
           OR COALESCE(fromSession, '') LIKE '%' || :query || '%'
        ORDER BY sessionDate DESC
    """)
    fun searchSessions(query: String): Flow<List<SessionEntity>>

    // ==================== INSERT/UPDATE/DELETE METHODS ====================

    // Insert new session
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    // Insert multiple sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SessionEntity>): List<Long>

    // Update existing session
    @Update
    suspend fun updateSession(session: SessionEntity)

    // Delete session by object
    @Delete
    suspend fun deleteSession(session: SessionEntity)

    // Delete session by ID
    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    // Delete all sessions for a specific case
    @Query("DELETE FROM sessions WHERE caseId = :caseId")
    suspend fun deleteSessionsByCaseId(caseId: Long)

    // Delete all sessions
    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()

    // ==================== VALIDATION METHODS ====================

    // Check if session exists (for duplicate prevention)
    @Query("SELECT COUNT(*) FROM sessions WHERE caseId = :caseId AND sessionDate = :sessionDate AND sessionId != :excludeSessionId")
    suspend fun isSessionExists(caseId: Long, sessionDate: Long, excludeSessionId: Long = 0): Int
}

// Data class for joined query result
data class SessionWithCaseDetails(
    val sessionId: Long,
    val caseId: Long,
    val sessionDate: Long,
    val fromSession: String?,
    val reason: String?,
    val decision: String?,
    val createdAt: Long,
    val status: String,
    val notes: String?,
    val sessionTime: String?,
    val caseNumber: String,
    val clientName: String,
    val opponentName: String?
)