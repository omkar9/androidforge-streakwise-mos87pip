package com.androidforge.streakwise.domain.usecase.habits

import com.androidforge.streakwise.domain.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long) {
        habitRepository.deleteHabit(habitId)
    }
}