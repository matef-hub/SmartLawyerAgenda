package com.example.smartlawyeragenda.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["caseId"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["caseId"]),
        Index(value = ["sessionDate"]),
        Index(value = ["caseId", "sessionDate"], unique = true)
    ]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,
    val caseId: Long,
    val sessionDate: Long,
    val fromSession: String? = null,
    val reason: String? = null,
    val decision: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val status: SessionStatus = SessionStatus.SCHEDULED,
    val notes: String? = null,
    val sessionTime: String? = null
) {
    fun isValid(): Boolean = caseId > 0 && sessionDate > 0

    fun isPast(): Boolean = sessionDate < System.currentTimeMillis()

    fun isToday(): Boolean {
        val today = LocalDate.now()
        val sessionDateLocal = Instant.ofEpochMilli(sessionDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return today == sessionDateLocal
    }

    fun isUpcoming(): Boolean = sessionDate > System.currentTimeMillis()

    fun getFormattedDate(pattern: String = "yyyy-MM-dd"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return Instant.ofEpochMilli(sessionDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(formatter)
    }

    fun getFormattedDateTime(): String {
        val dateStr = getFormattedDate()
        return sessionTime?.let { "$dateStr $it" } ?: dateStr
    }

    fun getStatusDisplay(): String = when (status) {
        SessionStatus.SCHEDULED -> "مجدولة"
        SessionStatus.COMPLETED -> "مكتملة"
        SessionStatus.POSTPONED -> "مؤجلة"
        SessionStatus.CANCELLED -> "ملغية"
    }

    fun getReasonDisplay(): String = reason?.takeIf { it.isNotBlank() } ?: "لا توجد ملاحظات"

    fun getDecisionDisplay(): String = decision?.takeIf { it.isNotBlank() } ?: "لم يتم اتخاذ قرار بعد"

    fun getFromSessionDisplay(): String = fromSession?.takeIf { it.isNotBlank() } ?: "جلسة جديدة"
}

enum class SessionStatus {
    SCHEDULED, COMPLETED, POSTPONED, CANCELLED
}