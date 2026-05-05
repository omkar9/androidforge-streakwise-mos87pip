package com.androidforge.streakwise.data.repository

import com.androidforge.streakwise.data.local.dao.HabitCompletionDao
import com.androidforge.streakwise.data.mapper.toDomain
import com.androidforge.streakwise.data.mapper.toEntity
import com.androidforge.streakwise.domain.model.HabitCompletion
import com.androidforge.streakwise.domain.repository.HabitCompletionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitCompletionRepositoryImpl @Inject constructor(
    private val habitCompletionDao: HabitCompletionDao
) : HabitCompletionRepository {

    override suspend fun addOrUpdateHabitCompletion(habitCompletion: HabitCompletion) {
        val existingCompletion = habitCompletionDao.getHabitCompletionForDate(
            habitCompletion.habitId,
            habitCompletion.completionDate.toString()
        )
        if (existingCompletion != null) {
            habitCompletionDao.updateHabitCompletion(habitCompletion.copy(id = existingCompletion.id).toEntity())
        } else {
            habitCompletionDao.insertHabitCompletion(habitCompletion.toEntity())
        }
    }

    override fun getHabitCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>> {
        return habitCompletionDao.getHabitCompletionsForHabit(habitId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getHabitCompletionStatusForDate(habitId: Long, date: LocalDate): Boolean {
        return habitCompletionDao.getHabitCompletionForDate(habitId, date.toString())?.isCompleted ?: false
    }

    override suspend fun deleteCompletionsForHabit(habitId: Long) {
        habitCompletionDao.deleteCompletionsForHabit(habitId)
    }
}