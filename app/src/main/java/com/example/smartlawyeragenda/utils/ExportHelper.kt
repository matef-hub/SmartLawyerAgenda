package com.example.smartlawyeragenda.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.example.smartlawyeragenda.repository.DatabaseExport
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class for exporting data to JSON/CSV and sharing the generated files.
 */
class ExportHelper(private val context: Context) {

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    private val fileProviderAuthority = "${context.packageName}.fileprovider"

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
            val exportDir = context.getExternalFilesDir(null) ?: context.cacheDir
            val file = File(exportDir, fileName)

            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }

            Result.success(getContentUri(file))
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to export JSON", e)
            Result.failure(e)
        }
    }

    suspend fun exportToCsv(
        cases: List<CaseEntity>,
        sessions: List<SessionEntity>
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val fileName = "SmartLawyerAgenda_${dateFormatter.format(Date())}.csv"
            val exportDir = context.getExternalFilesDir(null) ?: context.cacheDir
            val file = File(exportDir, fileName)

            FileWriter(file).use { writer ->
                writer.write("Type,ID,Case Number,Client Name,Opponent Name,Session Date,Session Time,Status,Notes,Created At\n")

                cases.forEach { case ->
                    val line = listOf(
                        "CASE",
                        case.caseId.toString(),
                        case.caseNumber,
                        case.clientName,
                        case.opponentName.orEmpty(),
                        "",
                        "",
                        "",
                        case.caseDescription.orEmpty(),
                        formatDate(case.createdAt)
                    ).joinToString(",") { csvValue(it) }
                    writer.write("$line\n")
                }

                sessions.forEach { session ->
                    val relatedCase = cases.find { it.caseId == session.caseId }
                    val line = listOf(
                        "SESSION",
                        session.sessionId.toString(),
                        relatedCase?.caseNumber.orEmpty(),
                        relatedCase?.clientName.orEmpty(),
                        relatedCase?.opponentName.orEmpty(),
                        formatDate(session.sessionDate),
                        session.sessionTime.orEmpty(),
                        session.status.name,
                        session.notes.orEmpty(),
                        formatDate(session.createdAt)
                    ).joinToString(",") { csvValue(it) }
                    writer.write("$line\n")
                }
            }

            Result.success(getContentUri(file))
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to export CSV", e)
            Result.failure(e)
        }
    }

    fun shareFile(uri: Uri, mimeType: String = "application/octet-stream") {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share export file")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to share file", e)
        }
    }

    fun getExportFileInfo(uri: Uri): ExportFileInfo? {
        return try {
            if (uri.scheme == "content") {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        val name = if (nameIdx >= 0) cursor.getString(nameIdx) else "export"
                        val size = if (sizeIdx >= 0) cursor.getLong(sizeIdx) else 0L
                        return ExportFileInfo(
                            name = name,
                            size = size,
                            path = uri.toString(),
                            lastModified = System.currentTimeMillis()
                        )
                    }
                }
                null
            } else {
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
            }
        } catch (e: Exception) {
            Log.e("ExportHelper", "Failed to get file info", e)
            null
        }
    }

    private fun getContentUri(file: File): Uri {
        return FileProvider.getUriForFile(context, fileProviderAuthority, file)
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)
    }

    private fun csvValue(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }
}

/**
 * Data class for export file information.
 */
data class ExportFileInfo(
    val name: String,
    val size: Long,
    val path: String,
    val lastModified: Long
)
