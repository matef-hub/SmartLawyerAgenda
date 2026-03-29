package com.example.smartlawyeragenda.data.dao

import androidx.room.*
import com.example.smartlawyeragenda.data.entities.CaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CaseDao {

    @Query("SELECT * FROM cases ORDER BY createdAt DESC")
    abstract fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases ORDER BY createdAt DESC")
    abstract suspend fun getAllCasesList(): List<CaseEntity>

    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    abstract suspend fun getCaseById(caseId: Long): CaseEntity?

    @Query("SELECT * FROM cases WHERE caseNumber = :caseNumber")
    abstract suspend fun getCaseByNumber(caseNumber: String): CaseEntity?

    @Query("""
        SELECT * FROM cases 
        WHERE clientName LIKE '%' || :query || '%' 
           OR COALESCE(opponentName, '') LIKE '%' || :query || '%'
           OR caseNumber LIKE '%' || :query || '%'
           OR COALESCE(rollNumber, '') LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    abstract fun searchCases(query: String): Flow<List<CaseEntity>>

    @Query("SELECT COUNT(*) FROM cases")
    abstract suspend fun getCasesCount(): Int

    @Query("SELECT COUNT(*) FROM cases WHERE isActive = 1")
    abstract suspend fun getActiveCasesCount(): Int

    @Query("""
        SELECT DISTINCT c.* FROM cases c
        INNER JOIN sessions s ON c.caseId = s.caseId
        WHERE s.sessionDate >= :fromDate
        ORDER BY c.createdAt DESC
    """)
    abstract fun getCasesWithUpcomingSessions(fromDate: Long): Flow<List<CaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCase(case: CaseEntity): Long

    @Update
    abstract suspend fun updateCase(case: CaseEntity)

    @Delete
    abstract suspend fun deleteCase(case: CaseEntity)

    @Query("DELETE FROM cases WHERE caseId = :caseId")
    abstract suspend fun deleteCaseById(caseId: Long)

    @Query("DELETE FROM cases")
    abstract suspend fun deleteAllCases()

    @Query("SELECT COUNT(*) FROM cases WHERE caseNumber = :caseNumber AND caseId != :excludeCaseId")
    abstract suspend fun isCaseNumberExists(caseNumber: String, excludeCaseId: Long = 0): Int
}
