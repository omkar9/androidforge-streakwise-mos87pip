package com.androidforge.streakwise.domain.usecase.streaks

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitCompletion
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class CalculateCurrentStreakUseCase @Inject constructor() {
    operator fun invoke(habit: Habit, completions: List<HabitCompletion>): Int {
        if (habit.archived) return 0

        val sortedCompletions = completions.filter { it.isCompleted }.sortedBy { it.completionDate }
        if (sortedCompletions.isEmpty()) return 0

        var currentStreak = 0
        var currentDate = LocalDate.now()

        // Check if today is completed or allowed to be skipped
        val isCompletedToday = sortedCompletions.any { it.completionDate == currentDate && it.isCompleted }
        val isScheduledToday = isHabitScheduledOnDate(habit, currentDate)

        // If today is scheduled and not completed, the streak is considered broken or not started for today.
        // We calculate the streak up to yesterday or the last completed day.
        if (isScheduledToday && !isCompletedToday) {
            currentDate = currentDate.minusDays(1)
        }

        while (currentDate >= habit.creationDate) {
            val isScheduled = isHabitScheduledOnDate(habit, currentDate)
            val isCompleted = sortedCompletions.any { it.completionDate == currentDate && it.isCompleted }

            if (isScheduled) {
                if (isCompleted) {
                    currentStreak++
                } else {
                    // If a scheduled day is missed, streak is broken
                    break
                }
            } else {
                // If not scheduled, skip this day, streak is not affected
            }
            currentDate = currentDate.minusDays(1)
        }
        return currentStreak
    }

    private fun isHabitScheduledOnDate(habit: Habit, date: LocalDate): Boolean {
        return when (habit.frequencyType) {
            Habit.FrequencyType.DAILY -> true
            Habit.FrequencyType.WEEKLY -> habit.frequencyValue.contains(date.dayOfWeek.value)
        }
    }
}