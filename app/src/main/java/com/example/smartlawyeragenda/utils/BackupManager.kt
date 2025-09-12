package com.example.smartlawyeragenda.utils

import android.content.Context
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File as JavaFile
import java.io.FileWriter
import java.io.IOException
import java.util.*

class BackupManager(private val context: Context) {
    
    private val gson = Gson()

    private val driveService: Drive by lazy {
        val credential = GoogleCredential.getApplicationDefault()
            .createScoped(listOf(DriveScopes.DRIVE_FILE))
        
        Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("SmartLawyerAgenda")
            .build()
    }

    
    data class BackupData(
        val cases: List<CaseEntity>,
        val sessions: List<SessionEntity>,
        val backupDate: Long = System.currentTimeMillis()
    )
    
    suspend fun exportToJson(cases: List<CaseEntity>, sessions: List<SessionEntity>): String = withContext(Dispatchers.IO) {
        val backupData = BackupData(cases, sessions)
        gson.toJson(backupData)
    }
    
    suspend fun importFromJson(json: String): BackupData = withContext(Dispatchers.IO) {
        val type = object : TypeToken<BackupData>() {}.type
        gson.fromJson(json, type)
    }
    
    suspend fun backupToDrive(cases: List<CaseEntity>, sessions: List<SessionEntity>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonData = exportToJson(cases, sessions)
            val tempFile = createTempFile(jsonData)
            
            // Create or get backup folder
            val folderId = getOrCreateBackupFolder()
            
            // Upload file
            val fileMetadata = File().apply {
                name = "SmartLawyerAgenda_Backup_${System.currentTimeMillis()}.json"
                parents = listOf(folderId)
            }
            
            val mediaContent = FileContent("application/json", tempFile)
            val uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()
            
            tempFile.delete()
            
            Result.success("تم إنشاء النسخة الاحتياطية بنجاح. معرف الملف: ${uploadedFile.id}")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun restoreFromDrive(): Result<BackupData> = withContext(Dispatchers.IO) {
        try {
            val folderId = getOrCreateBackupFolder()
            
            // Get latest backup file
            val files = driveService.files().list()
                .setQ("'$folderId' in parents and name contains 'SmartLawyerAgenda_Backup_'")
                .setOrderBy("createdTime desc")
                .execute()
            
            if (files.files.isEmpty()) {
                return@withContext Result.failure(Exception("لا توجد نسخ احتياطية"))
            }
            
            val latestFile = files.files[0]
            val content = driveService.files().get(latestFile.id).executeMediaAsInputStream()
            
            val json = content.bufferedReader().use { it.readText() }
            val backupData = importFromJson(json)
            
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun getOrCreateBackupFolder(): String = withContext(Dispatchers.IO) {
        try {
            // Try to find existing folder
            val files = driveService.files().list()
                .setQ("name='SmartLawyerAgenda Backups' and mimeType='application/vnd.google-apps.folder'")
                .execute()
            
            if (files.files.isNotEmpty()) {
                return@withContext files.files[0].id
            }
            
            // Create new folder
            val folderMetadata = File().apply {
                name = "SmartLawyerAgenda Backups"
                mimeType = "application/vnd.google-apps.folder"
            }
            
            val folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute()
            
            folder.id
        } catch (e: Exception) {
            throw IOException("فشل في إنشاء أو العثور على مجلد النسخ الاحتياطية", e)
        }
    }
    
    private fun createTempFile(jsonData: String): JavaFile {
        val tempFile = JavaFile.createTempFile("backup_", ".json", context.cacheDir)
        FileWriter(tempFile).use { it.write(jsonData) }
        return tempFile
    }
}
