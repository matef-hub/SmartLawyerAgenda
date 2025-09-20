package com.example.smartlawyeragenda.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cases",
    indices = [
        Index(value = ["caseNumber"], unique = true),
        Index(value = ["clientName"]),
        Index(value = ["createdAt"])
    ]
)
data class CaseEntity(
    @PrimaryKey(autoGenerate = true)
    val caseId: Long = 0,

    // Case number (required and unique)
    val caseNumber: String,

    // Roll number (optional)
    val rollNumber: String? = null,

    // Client name (required)
    val clientName: String,

    // Opponent name (optional)
    val opponentName: String? = null,

    // Case creation timestamp
    val createdAt: Long = System.currentTimeMillis(),

    // Additional case details (optional)
    val caseType: String? = null,
    val caseDescription: String? = null,
    val isActive: Boolean = true
) {
    // Validation methods
    fun isValid(): Boolean {
        return caseNumber.isNotBlank() && clientName.isNotBlank()
    }

    // Display name for UI
    fun getDisplayName(): String {
        return "$caseNumber - $clientName"
    }

    // Get opponent display name
    fun getOpponentDisplay(): String {
        return opponentName?.takeIf { it.isNotBlank() } ?: "غير محدد"
    }

    // Get roll number display
    fun getRollDisplay(): String {
        return rollNumber?.takeIf { it.isNotBlank() } ?: "غير محدد"
    }
}