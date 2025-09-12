package com.example.smartlawyeragenda.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cases",
    indices = [Index(value = ["caseNumber"], unique = true)]
)
data class CaseEntity(
    @PrimaryKey(autoGenerate = true)
    val caseId: Long = 0,

    // رقم القضية الأساسي
    val caseNumber: String,

    // رقم الرول (اختياري)
    val rollNumber: String? = null,

    // اسم الموكل
    val clientName: String,

    // اسم الخصم (اختياري)
    val opponentName: String? = null,

    // وقت إنشاء القضية
    val createdAt: Long = System.currentTimeMillis()
)
