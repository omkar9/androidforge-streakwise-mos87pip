package com.androidforge.streakwise.domain.usecase.streaks

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitCompletion
import java.time.LocalDate
import javax.inject.Inject

class CalculateLongestStreakUseCase @Inject constructor() {
    operator fun invoke(habit: Habit, completions: List<HabitCompletion>): Int {
        if (habit.archived) return 0

        val sortedCompletions = completions.filter { it.isCompleted }.sortedBy { it.completionDate }
        if (sortedCompletions.isEmpty()) return 0

        var longestStreak = 0
        var currentStreak = 0
        var lastCompletionDate: LocalDate? = null

        // Iterate from habit creation date up to today
        var dateIterator = habit.creationDate
        val today = LocalDate.now()

        while (dateIterator <= today) {
            val isScheduled = isHabitScheduledOnDate(habit, dateIterator)
            val isCompleted = sortedCompletions.any { it.completionDate == dateIterator && it.isCompleted }

            if (isScheduled) {
                if (isCompleted) {
                    currentStreak++
                } else {
                    // Scheduled but not completed, streak broken
                    longestStreak = maxOf(longestStreak, currentStreak)
                    currentStreak = 0
                }
            }
            // If not scheduled, it doesn't break the streak, nor does it add to it. Just move on.

            dateIterator = dateIterator.plusDays(1)
        }

        // After the loop, update longestStreak one last time in case the streak continues to today
        longestStreak = maxOf(longestStreak, currentStreak)

        return longestStreak
    }

    private fun isHabitScheduledOnDate(habit: Habit, date: LocalDate): Boolean {
        return when (habit.frequencyType) {
            Habit.FrequencyType.DAILY -> true
            Habit.FrequencyType.WEEKLY -> habit.frequencyValue.contains(date.dayOfWeek.value)
        }
    }
}