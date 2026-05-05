package com.androidforge.streakwise.domain.model

data class HabitWithCompletionStatus(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val longestStreak: Int
)