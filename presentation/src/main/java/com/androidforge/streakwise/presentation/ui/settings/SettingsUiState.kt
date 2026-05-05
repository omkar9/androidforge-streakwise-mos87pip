package com.androidforge.streakwise.presentation.ui.settings

import com.androidforge.streakwise.presentation.ui.common.UiState

data class SettingsScreenState(
    val isGlobalRemindersEnabled: Boolean = true
)

typealias SettingsUiState = UiState<SettingsScreenState>