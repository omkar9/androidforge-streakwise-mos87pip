package com.androidforge.streakwise.presentation.ui.addedit

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakwise.core.ads.AdManager
import com.androidforge.streakwise.core.network.NetworkStatus
import com.androidforge.streakwise.core.network.NetworkStatusTracker
import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.usecase.habits.AddHabitUseCase
import com.androidforge.streakwise.domain.usecase.habits.GetHabitByIdUseCase
import com.androidforge.streakwise.domain.usecase.habits.UpdateHabitUseCase
import com.androidforge.streakwise.domain.usecase.notifications.CancelHabitReminderUseCase
import com.androidforge.streakwise.domain.usecase.notifications.ScheduleHabitReminderUseCase
import com.androidforge.streakwise.presentation.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val scheduleHabitReminderUseCase: ScheduleHabitReminderUseCase,
    private val cancelHabitReminderUseCase: CancelHabitReminderUseCase,
    private val adManager: AdManager,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {

    private val habitId: Long? = savedStateHandle["habitId"]

    private val _habitName = MutableStateFlow("")
    private val _habitNameError = MutableStateFlow(false)
    private val _habitDescription = MutableStateFlow("")
    private val _frequencyType = MutableStateFlow(Habit.FrequencyType.DAILY)
    private val _frequencyValue = MutableStateFlow<List<Int>>(emptyList())
    private val _reminderTime = MutableStateFlow<LocalTime?>(null)
    private val _isLoading = MutableStateFlow(false)

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow<AddEditHabitUiState>(UiState.Success(AddEditHabitScreenState()))
    val uiState: StateFlow<AddEditHabitUiState> = _uiState.asStateFlow()

    init {
        // Combine all form fields into a single state flow for the UI
        viewModelScope.launch {
            combine(
                _habitName,
                _habitNameError,
                _habitDescription,
                _frequencyType,
                _frequencyValue,
                _reminderTime,
                _isLoading,
                networkStatusTracker.networkStatus
            ) { name, nameError, description, freqType, freqValue, reminder, loading, network ->
                // Only update the Success data part if we are not in an Error/Loading state
                if (_uiState.value !is UiState.Error && _uiState.value !is UiState.Loading) {
                    val successState = AddEditHabitScreenState(
                        habitName = name,
                        habitNameError = nameError,
                        habitDescription = description,
                        frequencyType = freqType,
                        frequencyValue = freqValue,
                        reminderTime = reminder,
                        isLoading = loading,
                    )
                    if (network == NetworkStatus.Lost) UiState.Offline(message = "You are offline, some features might be limited")
                    else UiState.Success(successState)
                } else {
                    _uiState.value // Keep current error/loading state
                }
            }.collectLatest { _uiState.value = it as AddEditHabitUiState }
        }
    }

    fun loadHabit(id: Long?) {
        if (id == null || id == -1L) return

        _isLoading.value = true
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val habit = getHabitByIdUseCase(id)
                if (habit != null) {
                    _habitName.value = habit.name
                    _habitDescription.value = habit.description
                    _frequencyType.value = habit.frequencyType
                    _frequencyValue.value = habit.frequencyValue
                    _reminderTime.value = habit.reminderTime
                    _uiState.value = UiState.Success(AddEditHabitScreenState(
                        habitName = habit.name,
                        habitDescription = habit.description,
                        frequencyType = habit.frequencyType,
                        frequencyValue = habit.frequencyValue,
                        reminderTime = habit.reminderTime,
                        isLoading = false
                    ))
                } else {
                    _uiState.value = UiState.Error("Habit not found")
                    showSnackbar("Habit not found")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading habit with id: $id")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Failed to load habit")
                showSnackbar("Failed to load habit: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onHabitNameChange(name: String) {
        _habitName.value = name
        _habitNameError.value = false // Clear error when user types
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(currentState.data.copy(habitName = name, habitNameError = false))
            } else currentState
        }
    }

    fun onHabitDescriptionChange(description: String) {
        _habitDescription.value = description
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(currentState.data.copy(habitDescription = description))
            } else currentState
        }
    }

    fun onFrequencyTypeChange(type: Habit.FrequencyType) {
        _frequencyType.value = type
        // Clear frequency value if switching to daily
        if (type == Habit.FrequencyType.DAILY) {
            _frequencyValue.value = emptyList()
        }
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(currentState.data.copy(frequencyType = type, frequencyValue = if (type == Habit.FrequencyType.DAILY) emptyList() else currentState.data.frequencyValue))
            } else currentState
        }
    }

    fun onToggleDayOfWeek(dayOfWeek: DayOfWeek) {
        val current = _frequencyValue.value.toMutableList()
        if (current.contains(dayOfWeek.value)) {
            current.remove(dayOfWeek.value)
        } else {
            current.add(dayOfWeek.value)
        }
        _frequencyValue.value = current.sorted()
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(currentState.data.copy(frequencyValue = current.sorted()))
            } else currentState
        }
    }

    fun onReminderTimeChange(hour: Int, minute: Int) {
        _reminderTime.value = LocalTime.of(hour, minute)
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(currentState.data.copy(reminderTime = LocalTime.of(hour, minute)))
            } else currentState
        }
    }

    fun onClearReminderTime() {
        _reminderTime.value = null
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(currentState.data.copy(reminderTime = null))
            } else currentState
        }
    }

    fun saveHabit() {
        if (_habitName.value.isBlank()) {
            _habitNameError.value = true
            _uiState.update { currentState ->
                if (currentState is UiState.Success) {
                    UiState.Success(currentState.data.copy(habitNameError = true))
                } else currentState
            }
            showSnackbar("Habit name cannot be empty.")
            return
        }

        _isLoading.value = true
        _uiState.update { currentState ->
            if (currentState is UiState.Success) {
                UiState.Success(currentState.data.copy(isLoading = true))
            } else currentState
        }

        viewModelScope.launch {
            try {
                val currentHabit = Habit(
                    id = habitId ?: 0L, // 0L for new habit, actual ID for existing
                    name = _habitName.value,
                    description = _habitDescription.value,
                    frequencyType = _frequencyType.value,
                    frequencyValue = _frequencyValue.value,
                    reminderTime = _reminderTime.value,
                    creationDate = (uiState.value as? UiState.Success)?.data?.creationDate ?: java.time.LocalDate.now(),
                    archived = false // New habits are not archived
                )

                if (habitId == null || habitId == -1L) {
                    // Add new habit
                    val newId = addHabitUseCase(currentHabit)
                    scheduleReminder(newId, _reminderTime.value)
                    showSnackbar("Habit saved successfully!")
                } else {
                    // Update existing habit
                    updateHabitUseCase(currentHabit)
                    scheduleReminder(habitId, _reminderTime.value)
                    showSnackbar("Habit updated successfully!")
                }
                _uiState.value = UiState.Success((_uiState.value as UiState.Success).data.copy(isLoading = false))
            } catch (e: Exception) {
                Timber.e(e, "Failed to save habit")
                _uiState.value = UiState.Error(e.localizedMessage ?: "Failed to save habit")
                showSnackbar("Failed to save habit: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
                _uiState.update { currentState ->
                    if (currentState is UiState.Success) {
                        UiState.Success(currentState.data.copy(isLoading = false))
                    } else currentState
                }
            }
        }
    }

    private suspend fun scheduleReminder(habitId: Long, reminderTime: LocalTime?) {
        cancelHabitReminderUseCase(habitId) // Always cancel existing first
        reminderTime?.let {
            scheduleHabitReminderUseCase(habitId, it.hour, it.minute)
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarEvent.send(message)
        }
    }

    fun triggerInterstitialAd(activity: Activity) {
        adManager.showInterstitialAd(activity, "ca-app-pub-3940256099942544/1033173712") // Test Ad Unit ID
    }
}