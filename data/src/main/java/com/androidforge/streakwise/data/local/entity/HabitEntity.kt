package com.androidforge.streakwise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String,
    val frequencyType: String, // Stored as String (e.g., "DAILY", "WEEKLY")
    val frequencyValue: String, // Stored as comma-separated string of DayOfWeek.value (e.g., "1,3,5")
    val reminderTime: String?, // Stored as String (e.g., "HH:mm"), nullable
    val creationDate: String, // Stored as String (ISO-8601 YYYY-MM-DD)
    val archived: Boolean
)