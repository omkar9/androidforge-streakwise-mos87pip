package com.androidforge.streakwise.domain.usecase.completions

import com.androidforge.streakwise.domain.repository.HabitCompletionRepository
import java.time.LocalDate
import javax.inject.Inject

class GetHabitCompletionStatusForDateUseCase @Inject constructor(
    private val habitCompletionRepository: HabitCompletionRepository
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate): Boolean {
        return habitCompletionRepository.getHabitCompletionStatusForDate(habitId, date)
    }
}