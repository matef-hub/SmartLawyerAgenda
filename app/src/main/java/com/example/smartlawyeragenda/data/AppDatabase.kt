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
    exportSchema = true
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
                    .fallbackToDestructiveMigrationOnDowngrade(false) // safer for downgrades
                    .build()

                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): AppDatabase = getDatabase(context)

        // Migration from version 1 to 2 with safe column additions
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE cases ADD COLUMN caseType TEXT")
                } catch (_: Exception) { }
                try {
                    db.execSQL("ALTER TABLE cases ADD COLUMN caseDescription TEXT")
                } catch (_: Exception) { }
                try {
                    db.execSQL("ALTER TABLE cases ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
                } catch (_: Exception) { }

                try {
                    db.execSQL("ALTER TABLE sessions ADD COLUMN status TEXT NOT NULL DEFAULT 'SCHEDULED'")
                } catch (_: Exception) { }
                try {
                    db.execSQL("ALTER TABLE sessions ADD COLUMN notes TEXT")
                } catch (_: Exception) { }
                try {
                    db.execSQL("ALTER TABLE sessions ADD COLUMN sessionTime TEXT")
                } catch (_: Exception) { }
            }
        }

        // Method to close database safely
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
