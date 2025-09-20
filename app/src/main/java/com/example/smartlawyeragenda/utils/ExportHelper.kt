package com.example.smartlawyeragenda.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for exporting data to various formats
 */
class ExportHelper(private val context: Context) {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    
    /**
     * Export data to JSON format
     */
    suspend fun exportToJson(
        cases: List<CaseEntity>,
        sessions: List<SessionEntity>
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val exportData = DatabaseExport(
                cases = cases,
                sessions = sessions,
                exportDate = System.currentTimeMillis()
            )
            
            val jsonString = gson.toJson(exportData)
            val fileName = "SmartLawyerAgenda_${dateFormatter.format(Date())}.json"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }
            
            Result.success(Uri.fromFile(file))
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to export JSON", e)
            Result.failure(e)
        }
    }
    
    /**
     * Export data to CSV format
     */
    suspend fun exportToCsv(
        cases: List<CaseEntity>,
        sessions: List<SessionEntity>
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val fileName = "SmartLawyerAgenda_${dateFormatter.format(Date())}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // Write CSV header
                writer.write("Type,ID,Case Number,Client Name,Opponent Name,Session Date,Session Time,Status,Notes,Created At\n")
                
                // Write cases
                cases.forEach { case ->
                    writer.write("CASE,${case.caseId},${case.caseNumber},${case.clientName},${case.opponentName ?: ""},,,,${case.caseDescription ?: ""},${formatDate(case.createdAt)}\n")
                }
                
                // Write sessions
                sessions.forEach { session ->
                    val case = cases.find { it.caseId == session.caseId }
                    writer.write("SESSION,${session.sessionId},${case?.caseNumber ?: ""},${case?.clientName ?: ""},${case?.opponentName ?: ""},${formatDate(session.sessionDate)},${session.sessionTime ?: ""},${session.status},${session.notes ?: ""},${formatDate(session.createdAt)}\n")
                }
            }
            
            Result.success(Uri.fromFile(file))
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to export CSV", e)
            Result.failure(e)
        }
    }
    
    /**
     * Share file using system share intent
     */
    fun shareFile(uri: Uri, mimeType: String = "application/octet-stream") {
        try {
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                type = mimeType
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooserIntent = android.content.Intent.createChooser(
                shareIntent,
                "مشاركة ملف البيانات"
            )
            
            chooserIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to share file", e)
        }
    }
    
    /**
     * Get export file info
     */
    fun getExportFileInfo(uri: Uri): ExportFileInfo? {
        return try {
            val file = File(uri.path ?: return null)
            if (file.exists()) {
                ExportFileInfo(
                    name = file.name,
                    size = file.length(),
                    path = file.absolutePath,
                    lastModified = file.lastModified()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to get file info", e)
            null
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
    }
}

/**
 * Data class for export file information
 */
data class ExportFileInfo(
    val name: String,
    val size: Long,
    val path: String,
    val lastModified: Long
)

/**
 * Data class for database export
 */
data class DatabaseExport(
    val cases: List<CaseEntity>,
    val sessions: List<SessionEntity>,
    val exportDate: Long
)