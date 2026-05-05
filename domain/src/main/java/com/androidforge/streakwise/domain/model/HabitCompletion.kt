package com.androidforge.streakwise.domain.model

import java.time.LocalDate

data class HabitCompletion(
    val id: Long = 0L,
    val habitId: Long,
    val completionDate: LocalDate,
    val isCompleted: Boolean
)