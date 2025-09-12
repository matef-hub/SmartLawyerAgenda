package com.example.smartlawyeragenda.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    indices = [Index(value = ["caseId"])]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,

    // مفتاح أجنبي يشير إلى القضية
    val caseId: Long,

    // تاريخ الجلسة (epochMillis)
    val sessionDate: Long,

    // مصدر/جلسة محالة منها (اختياري)
    val fromSession: String? = null,

    // سبب التأجيل أو الملاحظات (اختياري)
    val reason: String? = null,

    // القرار/الحكم في الجلسة (اختياري)
    val decision: String? = null,

    // وقت إنشاء الجلسة
    val createdAt: Long = System.currentTimeMillis()
)