package com.androidforge.streakwise.domain.usecase.habits

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        habitRepository.updateHabit(habit)
    }
}