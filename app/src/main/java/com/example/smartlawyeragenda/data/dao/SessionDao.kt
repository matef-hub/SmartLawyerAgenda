package com.example.smartlawyeragenda.data.dao

import androidx.room.*
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.data.entities.SessionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions WHERE sessionDate >= :dateStartMillis AND sessionDate <= :dateEndMillis ORDER BY sessionDate ASC")
    fun getSessionsByDateRange(dateStartMillis: Long, dateEndMillis: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE sessionDate >= :dayStartMillis AND sessionDate < :dayEndMillis ORDER BY sessionDate ASC")
    fun getSessionsByDay(dayStartMillis: Long, dayEndMillis: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE caseId = :caseId AND sessionDate = :sessionDate")
    suspend fun getSessionByCaseAndDate(caseId: Long, sessionDate: Long): SessionEntity?

    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: Long): SessionEntity?

    @Query("SELECT * FROM sessions WHERE caseId = :caseId ORDER BY sessionDate DESC")
    fun getSessionsByCaseId(caseId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE caseId = :caseId ORDER BY sessionDate DESC LIMIT 1")
    suspend fun getLatestSessionForCase(caseId: Long): SessionEntity?

    @Query("SELECT COUNT(*) FROM sessions WHERE caseId = :caseId")
    suspend fun getSessionsCountForCase(caseId: Long): Int

    @Query("SELECT * FROM sessions WHERE sessionDate >= :fromDateMillis ORDER BY sessionDate ASC")
    fun getUpcomingSessions(fromDateMillis: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE sessionDate < :beforeDateMillis ORDER BY sessionDate DESC")
    fun getPastSessions(beforeDateMillis: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions ORDER BY sessionDate DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("""
        SELECT s.*, c.caseNumber, c.clientName, c.opponentName 
        FROM sessions s 
        INNER JOIN cases c ON s.caseId = c.caseId 
        WHERE s.sessionDate >= :fromDateMillis 
        ORDER BY s.sessionDate ASC
    """)
    fun getSessionsWithCaseDetails(fromDateMillis: Long): Flow<List<SessionWithCaseDetails>>

    @Query("SELECT * FROM sessions WHERE sessionDate >= :todayStartMillis AND sessionDate < :todayEndMillis ORDER BY sessionDate ASC")
    fun getTodaySessions(todayStartMillis: Long, todayEndMillis: Long): Flow<List<SessionEntity>>

    @Query("""
        SELECT * FROM sessions 
        WHERE COALESCE(reason, '') LIKE '%' || :query || '%'
           OR COALESCE(decision, '') LIKE '%' || :query || '%'
           OR COALESCE(fromSession, '') LIKE '%' || :query || '%'
        ORDER BY sessionDate DESC
    """)
    fun searchSessions(query: String): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<SessionEntity>): List<Long>

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Delete
    suspend fun deleteSession(session: SessionEntity)

    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("DELETE FROM sessions WHERE caseId = :caseId")
    suspend fun deleteSessionsByCaseId(caseId: Long)

    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT COUNT(*) FROM sessions WHERE caseId = :caseId AND sessionDate = :sessionDate AND sessionId != :excludeSessionId")
    suspend fun isSessionExists(caseId: Long, sessionDate: Long, excludeSessionId: Long = 0): Int
}

data class SessionWithCaseDetails(
    val sessionId: Long,
    val caseId: Long,
    val sessionDate: Long,
    val fromSession: String?,
    val reason: String?,
    val decision: String?,
    val createdAt: Long,
    val status: SessionStatus,
    val notes: String?,
    val sessionTime: String?,
    val caseNumber: String,
    val clientName: String,
    val opponentName: String?
)