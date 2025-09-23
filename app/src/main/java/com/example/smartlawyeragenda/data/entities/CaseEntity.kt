package com.example.smartlawyeragenda.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

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
    val caseNumber: String,
    val rollNumber: String? = null,
    val clientName: String,
    val opponentName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val caseType: String? = null,
    val caseDescription: String? = null,
    val isActive: Boolean = true
) {
    fun isValid(): Boolean = caseNumber.isNotBlank() && clientName.isNotBlank()

    fun getDisplayName(): String = "$caseNumber - $clientName"

    fun getOpponentDisplay(): String = opponentName?.takeIf { it.isNotBlank() } ?: "غير محدد"

    fun getRollDisplay(): String = rollNumber?.takeIf { it.isNotBlank() } ?: "غير محدد"
}