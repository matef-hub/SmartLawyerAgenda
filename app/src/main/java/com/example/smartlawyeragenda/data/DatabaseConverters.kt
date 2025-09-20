package com.example.smartlawyeragenda.data

import androidx.room.TypeConverter
import com.example.smartlawyeragenda.data.entities.SessionStatus

/**
 * Type converters for Room database to handle custom types
 */
class DatabaseConverters {

    @TypeConverter
    fun fromSessionStatus(status: SessionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toSessionStatus(status: String): SessionStatus {
        return try {
            SessionStatus.valueOf(status)
        } catch (_: IllegalArgumentException) {
            SessionStatus.SCHEDULED // Default fallback
        }
    }
}