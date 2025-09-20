package com.example.smartlawyeragenda.data.dao

import androidx.room.*
import com.example.smartlawyeragenda.data.entities.CaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {

    // Get all cases ordered by newest first
    @Query("SELECT * FROM cases ORDER BY createdAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    // Get case by ID
    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    suspend fun getCaseById(caseId: Long): CaseEntity?

    // Get case by case number
    @Query("SELECT * FROM cases WHERE caseNumber = :caseNumber")
    suspend fun getCaseByNumber(caseNumber: String): CaseEntity?

    // Search cases by client name, opponent name, case number, or roll number
    @Query("""
        SELECT * FROM cases 
        WHERE clientName LIKE '%' || :query || '%' 
           OR COALESCE(opponentName, '') LIKE '%' || :query || '%'
           OR caseNumber LIKE '%' || :query || '%'
           OR COALESCE(rollNumber, '') LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchCases(query: String): Flow<List<CaseEntity>>

    // Get cases count
    @Query("SELECT COUNT(*) FROM cases")
    suspend fun getCasesCount(): Int

    // Get cases with upcoming sessions
    @Query("""
        SELECT DISTINCT c.* FROM cases c
        INNER JOIN sessions s ON c.caseId = s.caseId
        WHERE s.sessionDate >= :fromDate
        ORDER BY c.createdAt DESC
    """)
    fun getCasesWithUpcomingSessions(fromDate: Long): Flow<List<CaseEntity>>

    // Insert or replace case
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: CaseEntity): Long

    // Update case
    @Update
    suspend fun updateCase(case: CaseEntity)

    // Delete case by object
    @Delete
    suspend fun deleteCase(case: CaseEntity)

    // Delete case by ID
    @Query("DELETE FROM cases WHERE caseId = :caseId")
    suspend fun deleteCaseById(caseId: Long)

    // Delete all cases
    @Query("DELETE FROM cases")
    suspend fun deleteAllCases()

    // Check if case number already exists (for validation)
    @Query("SELECT COUNT(*) FROM cases WHERE caseNumber = :caseNumber AND caseId != :excludeCaseId")
    suspend fun isCaseNumberExists(caseNumber: String, excludeCaseId: Long = 0): Int
}