package com.androidforge.streakwise.domain.usecase.completions

import com.androidforge.streakwise.domain.model.HabitCompletion
import com.androidforge.streakwise.domain.repository.HabitCompletionRepository
import java.time.LocalDate
import javax.inject.Inject

class MarkHabitMissedUseCase @Inject constructor(
    private val habitCompletionRepository: HabitCompletionRepository
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate) {
        val completion = HabitCompletion(habitId = habitId, completionDate = date, isCompleted = false)
        habitCompletionRepository.addOrUpdateHabitCompletion(completion)
    }
}