package com.example.smartlawyeragenda.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

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

    // Foreign key to case
    val caseId: Long,

    // Session date (epoch millis)
    val sessionDate: Long,

    // Source session (optional)
    val fromSession: String? = null,

    // Postponement reason or notes (optional)
    val reason: String? = null,

    // Decision/judgment in the session (optional)
    val decision: String? = null,

    // Session creation timestamp
    val createdAt: Long = System.currentTimeMillis(),

    // Session status
    val status: SessionStatus = SessionStatus.SCHEDULED,

    // Additional notes
    val notes: String? = null,

    // Session time (for more precise scheduling)
    val sessionTime: String? = null // Format: "HH:mm"
) {
    // Validation methods
    fun isValid(): Boolean {
        return caseId > 0 && sessionDate > 0
    }

    // Check if session is in the past
    fun isPast(): Boolean {
        return sessionDate < System.currentTimeMillis()
    }

    // Check if session is today
    fun isToday(): Boolean {
        val today = Calendar.getInstance()
        val sessionCalendar = Calendar.getInstance().apply {
            timeInMillis = sessionDate
        }
        return today.get(Calendar.YEAR) == sessionCalendar.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == sessionCalendar.get(Calendar.DAY_OF_YEAR)
    }

    // Check if session is upcoming (future)
    fun isUpcoming(): Boolean {
        return sessionDate > System.currentTimeMillis()
    }

    // Format session date for display
    fun getFormattedDate(pattern: String = "yyyy-MM-dd"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date(sessionDate))
    }

    // Format session date and time for display
    fun getFormattedDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = dateFormat.format(Date(sessionDate))
        return if (sessionTime != null) {
            "$dateStr $sessionTime"
        } else {
            dateStr
        }
    }

    // Get session status display text
    fun getStatusDisplay(): String {
        return when (status) {
            SessionStatus.SCHEDULED -> "مجدولة"
            SessionStatus.COMPLETED -> "مكتملة"
            SessionStatus.POSTPONED -> "مؤجلة"
            SessionStatus.CANCELLED -> "ملغية"
        }
    }

    // Get reason or default text
    fun getReasonDisplay(): String {
        return reason?.takeIf { it.isNotBlank() } ?: "لا توجد ملاحظات"
    }

    // Get decision or default text
    fun getDecisionDisplay(): String {
        return decision?.takeIf { it.isNotBlank() } ?: "لم يتم اتخاذ قرار بعد"
    }

    // Get from session or default text
    fun getFromSessionDisplay(): String {
        return fromSession?.takeIf { it.isNotBlank() } ?: "جلسة جديدة"
    }
}

enum class SessionStatus {
    SCHEDULED,
    COMPLETED,
    POSTPONED,
    CANCELLED
}