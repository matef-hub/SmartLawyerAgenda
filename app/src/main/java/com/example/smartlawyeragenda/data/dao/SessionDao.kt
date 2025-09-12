package com.example.smartlawyeragenda.data.dao

import androidx.room.*
import com.example.smartlawyeragenda.data.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    
    // ==================== QUERY METHODS ====================
    
    // Get sessions by date range
    @Query("SELECT * FROM sessions WHERE sessionDate >= :dateStartMillis AND sessionDate < :dateEndMillis ORDER BY sessionDate ASC")
    fun getSessionsByDate(dateStartMillis: Long, dateEndMillis: Long): Flow<List<SessionEntity>>
    
    // Check if session exists for case on specific date
    @Query("SELECT * FROM sessions WHERE caseId = :caseId AND sessionDate = :sessionDate")
    suspend fun existsSession(caseId: Long, sessionDate: Long): SessionEntity?
    
    // Get session by ID
    @Query("SELECT * FROM sessions WHERE sessionId = :sessionId")
    suspend fun getSessionById(sessionId: Long): SessionEntity?
    
    // Get all sessions for a specific case
    @Query("SELECT * FROM sessions WHERE caseId = :caseId ORDER BY sessionDate DESC")
    fun getSessionsByCaseId(caseId: Long): Flow<List<SessionEntity>>
    
    // Get latest session for a case
    @Query("SELECT * FROM sessions WHERE caseId = :caseId ORDER BY sessionDate DESC LIMIT 1")
    suspend fun getLatestSession(caseId: Long): SessionEntity?
    
    // Get count of sessions for a case
    @Query("SELECT COUNT(*) FROM sessions WHERE caseId = :caseId")
    suspend fun getSessionsCountForCase(caseId: Long): Int
    
    // Get upcoming sessions (from today onwards)
    @Query("SELECT * FROM sessions WHERE sessionDate >= :todayMillis ORDER BY sessionDate ASC")
    fun getUpcomingSessions(todayMillis: Long): Flow<List<SessionEntity>>
    
    // Get all sessions ordered by date
    @Query("SELECT * FROM sessions ORDER BY sessionDate DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>
    
    // ==================== INSERT/UPDATE/DELETE METHODS ====================
    
    // Insert new session
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long
    
    // Update existing session
    @Update
    suspend fun updateSession(session: SessionEntity)
    
    // Delete session by object
    @Delete
    suspend fun deleteSession(session: SessionEntity)
    
    // Delete session by ID
    @Query("DELETE FROM sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)
    
    // Delete all sessions (for cleanup)
    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
}
