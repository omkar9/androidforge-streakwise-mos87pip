package com.androidforge.streakwise.domain.usecase.habits

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitByIdUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(habitId: Long): Flow<Habit?> {
        return habitRepository.getHabitById(habitId)
    }
}