package com.androidforge.streakwise.domain.usecase.habits

import com.androidforge.streakwise.domain.model.HabitWithCompletionStatus
import com.androidforge.streakwise.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetAllActiveHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(today: LocalDate = LocalDate.now()): Flow<List<HabitWithCompletionStatus>> {
        return habitRepository.getAllActiveHabitsWithCompletionStatus(today)
    }
}