package com.androidforge.streakwise.data.repository

import com.androidforge.streakwise.data.local.dao.HabitCompletionDao
import com.androidforge.streakwise.data.local.dao.HabitDao
import com.androidforge.streakwise.data.mapper.toDomain
import com.androidforge.streakwise.data.mapper.toEntity
import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitCompletion
import com.androidforge.streakwise.domain.model.HabitWithCompletionStatus
import com.androidforge.streakwise.domain.repository.HabitCompletionRepository
import com.androidforge.streakwise.domain.repository.HabitRepository
import com.androidforge.streakwise.domain.usecase.streaks.CalculateCurrentStreakUseCase
import com.androidforge.streakwise.domain.usecase.streaks.CalculateLongestStreakUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitCompletionRepository: HabitCompletionRepository, // Injecting completion repository to help calculate status
    private val calculateCurrentStreakUseCase: CalculateCurrentStreakUseCase,
    private val calculateLongestStreakUseCase: CalculateLongestStreakUseCase
) : HabitRepository {

    override suspend fun addHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit.toEntity())
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
        // Completions are automatically deleted via CASCADE foreign key
    }

    override suspend fun archiveHabit(habitId: Long) {
        habitDao.archiveHabit(habitId)
    }

    override fun getHabitById(habitId: Long): Flow<Habit?> {
        return habitDao.getHabitById(habitId).map { it?.toDomain() }
    }

    override fun getAllActiveHabits(): Flow<List<Habit>> {
        return habitDao.getAllActiveHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllActiveHabitsWithCompletionStatus(today: LocalDate): Flow<List<HabitWithCompletionStatus>> {
        return habitDao.getAllActiveHabitsWithCompletions().map { habitWithCompletionsList ->
            habitWithCompletionsList.map { habitWithCompletions ->
                val habit = habitWithCompletions.habit.toDomain()
                val completions = habitWithCompletions.completions.map { it.toDomain() }
                val isCompletedToday = completions.any { it.completionDate == today && it.isCompleted }
                val currentStreak = calculateCurrentStreakUseCase(habit, completions)
                val longestStreak = calculateLongestStreakUseCase(habit, completions)
                HabitWithCompletionStatus(habit, isCompletedToday, currentStreak, longestStreak)
            }
        }
    }

    // This method is for HabitDetailScreen, combines Habit and all its completions for Streak calculation
    override fun getHabitWithAllCompletions(habitId: Long): Flow<Pair<Habit?, List<HabitCompletion>>> {
        return habitDao.getHabitWithCompletions(habitId).map { relation ->
            val habit = relation?.habit?.toDomain()
            val completions = relation?.completions?.map { it.toDomain() } ?: emptyList()
            Pair(habit, completions)
        }
    }
}