package com.androidforge.streakwise.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakwise.domain.usecase.settings.GetNotificationSettingsUseCase
import com.androidforge.streakwise.domain.usecase.settings.UpdateNotificationSettingsUseCase
import com.androidforge.streakwise.presentation.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(UiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            getNotificationSettingsUseCase()
                .onStart { _uiState.value = UiState.Loading }
                .catch { e ->
                    Timber.e(e, "Error loading settings")
                    _uiState.value = UiState.Error(e.localizedMessage ?: "Failed to load settings")
                }
                .collect {
                    _uiState.value = UiState.Success(SettingsScreenState(it.isGlobalRemindersEnabled))
                }
        }
    }

    fun toggleGlobalReminders(enabled: Boolean) {
        viewModelScope.launch {
            try {
                updateNotificationSettingsUseCase(enabled)
                _uiState.value = UiState.Success(SettingsScreenState(enabled))
                showSnackbar("Notification settings updated.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to update global reminder setting")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Failed to update settings")
                showSnackbar("Failed to update notification settings.")
            }
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarEvent.send(message)
        }
    }
}