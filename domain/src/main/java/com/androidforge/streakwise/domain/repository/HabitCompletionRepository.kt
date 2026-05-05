package com.androidforge.streakwise.domain.repository

import com.androidforge.streakwise.domain.model.HabitCompletion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitCompletionRepository {
    suspend fun addOrUpdateHabitCompletion(habitCompletion: HabitCompletion)
    fun getHabitCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>>
    suspend fun getHabitCompletionStatusForDate(habitId: Long, date: LocalDate): Boolean
    suspend fun deleteCompletionsForHabit(habitId: Long)
}