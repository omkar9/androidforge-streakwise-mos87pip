package com.androidforge.streakwise.domain.repository

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitWithCompletionStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {
    suspend fun addHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habitId: Long)
    suspend fun archiveHabit(habitId: Long)
    fun getHabitById(habitId: Long): Flow<Habit?>
    fun getAllActiveHabits(): Flow<List<Habit>>
    fun getAllActiveHabitsWithCompletionStatus(today: LocalDate): Flow<List<HabitWithCompletionStatus>>
    fun getHabitWithAllCompletions(habitId: Long): Flow<Pair<Habit?, List<HabitCompletionStatus>>>
}