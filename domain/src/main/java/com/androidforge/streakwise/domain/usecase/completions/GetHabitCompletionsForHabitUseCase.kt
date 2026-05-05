package com.androidforge.streakwise.domain.usecase.completions

import com.androidforge.streakwise.domain.model.HabitCompletion
import com.androidforge.streakwise.domain.repository.HabitCompletionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitCompletionsForHabitUseCase @Inject constructor(
    private val habitCompletionRepository: HabitCompletionRepository
) {
    operator fun invoke(habitId: Long): Flow<List<HabitCompletion>> {
        return habitCompletionRepository.getHabitCompletionsForHabit(habitId)
    }
}