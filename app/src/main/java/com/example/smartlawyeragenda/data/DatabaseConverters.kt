package com.example.smartlawyeragenda.data

import androidx.room.TypeConverter
import com.example.smartlawyeragenda.data.entities.SessionStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Type converters for Room database to handle custom types
 */
class DatabaseConverters {

    // SessionStatus converters
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

    // Instant/Long converters for better date handling
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? = value?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? = value?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? = value?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? = value?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    // Boolean/Int converter (if needed for compatibility)
    @TypeConverter
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    fun toBoolean(value: Int): Boolean = value == 1
}