package com.androidforge.streakwise.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.androidforge.streakwise.data.local.dao.HabitCompletionDao
import com.androidforge.streakwise.data.local.dao.HabitDao
import com.androidforge.streakwise.data.local.entity.HabitCompletionEntity
import com.androidforge.streakwise.data.local.entity.HabitEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME) }
    }

    @TypeConverter
    fun fromListInt(list: List<Int>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter\    fun toListInt(data: String?): List<Int>? {
        return data?.split(",")?.mapNotNull { it.toIntOrNull() }
    }

    @TypeConverter
    fun fromDayOfWeekList(dayOfWeeks: List<DayOfWeek>?): String? {
        return dayOfWeeks?.map { it.value }?.joinToString(",")
    }

    @TypeConverter
    fun toDayOfWeekList(data: String?): List<DayOfWeek>? {
        return data?.split(",")?.mapNotNull { it.toIntOrNull() }?.map { DayOfWeek.of(it) }
    }

    @TypeConverter
    fun fromFrequencyType(type: com.androidforge.streakwise.domain.model.Habit.FrequencyType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toFrequencyType(name: String?): com.androidforge.streakwise.domain.model.Habit.FrequencyType? {
        return name?.let { com.androidforge.streakwise.domain.model.Habit.FrequencyType.valueOf(it) }
    }
}

@Database(entities = [HabitEntity::class, HabitCompletionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StreakWiseDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
}