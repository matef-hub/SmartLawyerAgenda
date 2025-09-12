package com.example.smartlawyeragenda.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ExportHelper(private val context: Context) {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()
    
    data class ExportData(
        val exportDate: String,
        val totalCases: Int,
        val totalSessions: Int,
        val cases: List<CaseEntity>,
        val sessions: List<SessionEntity>
    )
    
    fun exportToJson(
        cases: List<CaseEntity>,
        sessions: List<SessionEntity>,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            val fileName = "SmartLawyerAgenda_Export_${dateFormatter.format(Date())}.json"
            
            val exportData = ExportData(
                exportDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                totalCases = cases.size,
                totalSessions = sessions.size,
                cases = cases,
                sessions = sessions
            )
            
            val jsonString = gson.toJson(exportData)
            
            val file = File(context.getExternalFilesDir(null), fileName)
            FileWriter(file).use { it.write(jsonString) }
            
            onSuccess(file)
        } catch (e: Exception) {
            onError("فشل في تصدير البيانات: ${e.message}")
        }
    }
    
    fun exportToCsv(
        cases: List<CaseEntity>,
        sessions: List<SessionEntity>,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            val fileName = "SmartLawyerAgenda_Export_${dateFormatter.format(Date())}.csv"
            
            val file = File(context.getExternalFilesDir(null), fileName)
            FileWriter(file).use { writer ->
                // Write CSV header
                writer.write("نوع السجل,رقم القضية,رقم اللفة,اسم الموكل,اسم الخصم,تاريخ الجلسة,من جلسة,السبب,القرار,تاريخ الإنشاء\n")
                
                // Write cases
                cases.forEach { case ->
                    writer.write("قضية,${case.caseNumber},${case.rollNumber ?: ""},${case.clientName},${case.opponentName},,,,,${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(case.createdAt))}\n")
                }
                
                // Write sessions
                sessions.forEach { session ->
                    val case = cases.find { it.caseId == session.caseId }
                    writer.write("جلسة,${case?.caseNumber ?: ""},${case?.rollNumber ?: ""},${case?.clientName ?: ""},${case?.opponentName ?: ""},${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(session.sessionDate))},${session.fromSession ?: ""},${session.reason ?: ""},${session.decision ?: ""},${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(session.createdAt))}\n")
                }
            }
            
            onSuccess(file)
        } catch (e: Exception) {
            onError("فشل في تصدير البيانات: ${e.message}")
        }
    }
    
    fun shareFile(file: File, onError: (String) -> Unit) {
        try {
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val shareIntent = Intent.createChooser(intent, "مشاركة الملف")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            onError("فشل في مشاركة الملف: ${e.message}")
        }
    }
    
    fun importFromJson(
        jsonString: String,
        onSuccess: (List<CaseEntity>, List<SessionEntity>) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val exportData = gson.fromJson(jsonString, ExportData::class.java)
            onSuccess(exportData.cases, exportData.sessions)
        } catch (e: Exception) {
            onError("فشل في استيراد البيانات: ${e.message}")
        }
    }
}
