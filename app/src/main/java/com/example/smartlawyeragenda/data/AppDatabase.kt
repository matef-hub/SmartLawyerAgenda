package com.example.smartlawyeragenda.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartlawyeragenda.data.dao.CaseDao
import com.example.smartlawyeragenda.data.dao.SessionDao
import com.example.smartlawyeragenda.data.entities.CaseEntity
import com.example.smartlawyeragenda.data.entities.SessionEntity

@Database(
    entities = [CaseEntity::class, SessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun caseDao(): CaseDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_lawyer_agenda_database"
                )
                    // مفيد في مرحلة التطوير، بيعمل reset لو الـ schema اتغير
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // اختصار علشان تستدعيه من أي مكان
        fun getInstance(context: Context): AppDatabase = getDatabase(context)
    }
}
