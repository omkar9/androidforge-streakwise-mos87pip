package com.androidforge.streakwise.presentation.ui.habitdetail

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitCompletion
import com.androidforge.streakwise.presentation.ui.common.UiState

data class HabitDetailScreenState(
    val habit: Habit,
    val currentStreak: Int,
    val longestStreak: Int,
    val completions: List<HabitCompletion>
)

typealias HabitDetailUiState = UiState<HabitDetailScreenState>