package com.androidforge.streakwise.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId", "completionDate"], unique = true)] // Ensure unique completion per habit per day
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val habitId: Long,
    val completionDate: String, // Stored as String (ISO-8601 YYYY-MM-DD)
    val isCompleted: Boolean
)