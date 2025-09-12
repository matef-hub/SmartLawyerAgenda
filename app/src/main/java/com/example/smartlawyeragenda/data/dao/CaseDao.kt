package com.example.smartlawyeragenda.data.dao

import androidx.room.*
import com.example.smartlawyeragenda.data.entities.CaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {

    // كل القضايا بالترتيب الأحدث أولًا
    @Query("SELECT * FROM cases ORDER BY createdAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    // جلب قضية بالـ ID
    @Query("SELECT * FROM cases WHERE caseId = :caseId")
    suspend fun getCaseById(caseId: Long): CaseEntity?

    // جلب قضية برقم القضية
    @Query("SELECT * FROM cases WHERE caseNumber = :caseNumber")
    suspend fun getCaseByNumber(caseNumber: String): CaseEntity?

    // بحث بالاسم أو الخصم
    @Query("SELECT * FROM cases WHERE clientName LIKE '%' || :query || '%' OR opponentName LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchCases(query: String): Flow<List<CaseEntity>>

    // إحصائيات
    @Query("SELECT COUNT(*) FROM cases")
    suspend fun getCasesCount(): Int

    // إضافة أو تحديث قضية
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: CaseEntity): Long

    // تحديث
    @Update
    suspend fun updateCase(case: CaseEntity)

    // حذف (object)
    @Delete
    suspend fun deleteCase(case: CaseEntity)

    // حذف بالـ ID
    @Query("DELETE FROM cases WHERE caseId = :caseId")
    suspend fun deleteCaseById(caseId: Long)

    @Query("DELETE FROM cases")
    suspend fun deleteAllCases()

}
