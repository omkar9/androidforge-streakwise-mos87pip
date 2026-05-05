package com.androidforge.streakwise.domain.usecase.habits

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.repository.HabitRepository
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Long {
        return habitRepository.addHabit(habit)
    }
}