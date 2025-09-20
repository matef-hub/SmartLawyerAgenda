package com.example.smartlawyeragenda.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smartlawyeragenda.data.dao.CaseDao
import com.example.smartlawyeragenda.data.dao.SessionDao
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity

@Database(
    entities = [CaseEntity::class, SessionEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun caseDao(): CaseDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "smart_lawyer_agenda_database"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): AppDatabase = getDatabase(context)

        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new columns to cases table
                db.execSQL("ALTER TABLE cases ADD COLUMN caseType TEXT")
                db.execSQL("ALTER TABLE cases ADD COLUMN caseDescription TEXT")
                db.execSQL("ALTER TABLE cases ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")

                // Add new columns to sessions table
                db.execSQL("ALTER TABLE sessions ADD COLUMN status TEXT NOT NULL DEFAULT 'SCHEDULED'")
                db.execSQL("ALTER TABLE sessions ADD COLUMN notes TEXT")
                db.execSQL("ALTER TABLE sessions ADD COLUMN sessionTime TEXT")

                // Create new indices for better performance
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cases_clientName ON cases(clientName)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cases_createdAt ON cases(createdAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_sessions_sessionDate ON sessions(sessionDate)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_sessions_caseId_sessionDate ON sessions(caseId, sessionDate)")
            }
        }

        // Method to clear all data (for development/testing)
        fun clearAllTables(context: Context) {
            val db = getDatabase(context)
            db.clearAllTables()
        }

        // Method to close database
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}