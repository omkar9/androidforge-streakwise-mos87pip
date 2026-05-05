package com.androidforge.streakwise.presentation.ui.addedit

import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.presentation.ui.common.UiState
import java.time.LocalTime

data class AddEditHabitScreenState(
    val habitName: String = "",
    val habitNameError: Boolean = false,
    val habitDescription: String = "",
    val frequencyType: Habit.FrequencyType = Habit.FrequencyType.DAILY,
    val frequencyValue: List<Int> = emptyList(), // DayOfWeek.value (1-7)
    val reminderTime: LocalTime? = null,
    val isLoading: Boolean = false,
    val showSnackbarMessage: String? = null
)

typealias AddEditHabitUiState = UiState<AddEditHabitScreenState>