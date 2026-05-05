package com.androidforge.streakwise.presentation.ui.habitdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakwise.core.network.NetworkStatus
import com.androidforge.streakwise.core.network.NetworkStatusTracker
import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitCompletion
import com.androidforge.streakwise.domain.usecase.completions.GetHabitCompletionsForHabitUseCase
import com.androidforge.streakwise.domain.usecase.completions.MarkHabitCompletedUseCase
import com.androidforge.streakwise.domain.usecase.completions.MarkHabitMissedUseCase
import com.androidforge.streakwise.domain.usecase.habits.ArchiveHabitUseCase
import com.androidforge.streakwise.domain.usecase.habits.DeleteHabitUseCase
import com.androidforge.streakwise.domain.usecase.habits.GetHabitByIdUseCase
import com.androidforge.streakwise.domain.usecase.streaks.CalculateCurrentStreakUseCase
import com.androidforge.streakwise.domain.usecase.streaks.CalculateLongestStreakUseCase
import com.androidforge.streakwise.presentation.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val getHabitCompletionsForHabitUseCase: GetHabitCompletionsForHabitUseCase,
    private val calculateCurrentStreakUseCase: CalculateCurrentStreakUseCase,
    private val calculateLongestStreakUseCase: CalculateLongestStreakUseCase,
    private val markHabitCompletedUseCase: MarkHabitCompletedUseCase,
    private val markHabitMissedUseCase: MarkHabitMissedUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val archiveHabitUseCase: ArchiveHabitUseCase,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {

    private val habitId: Long = savedStateHandle["habitId"] ?: -1L

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _snackbarEvent = Channel<String>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _forceRefresh = MutableStateFlow(false)

    val uiState: StateFlow<HabitDetailUiState> = _forceRefresh
        .flatMapLatest { 
            if (habitId == -1L) {
                MutableStateFlow(UiState.Error("Invalid Habit ID"))
            } else {
                combine(
                    getHabitByIdUseCase(habitId),
                    getHabitCompletionsForHabitUseCase(habitId)
                ) { habit, completions ->
                    if (habit == null) {
                        UiState.Empty("Habit not found")
                    } else {
                        val currentStreak = calculateCurrentStreakUseCase(habit, completions)
                        val longestStreak = calculateLongestStreakUseCase(habit, completions)
                        UiState.Success(
                            HabitDetailScreenState(
                                habit = habit,
                                currentStreak = currentStreak,
                                longestStreak = longestStreak,
                                completions = completions
                            )
                        )
                    }
                }
                .onStart { emit(UiState.Loading) }
                .catch { e ->
                    Timber.e(e, "Error loading habit details for ID: $habitId")
                    emit(UiState.Error(e.localizedMessage ?: "Unknown error loading habit details", e))
                }
            }
        }
        .combine(networkStatusTracker.networkStatus) { state, network ->
            if (network == NetworkStatus.Lost && state is UiState.Error) {
                UiState.Offline(state.message)
            } else if (network == NetworkStatus.Lost && state is UiState.Loading) {
                UiState.Loading // Still loading, but network is lost
            } else {
                state
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    fun loadHabitDetails(id: Long) {
        if (id == -1L) {
            _uiState.value = UiState.Error("Invalid Habit ID")
            return
        }
        _forceRefresh.value = !_forceRefresh.value // Trigger reload
    }

    fun previousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }

    fun toggleHabitCompletionForDate(habitId: Long, date: LocalDate, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                if (isCompleted) {
                    markHabitCompletedUseCase(habitId, date)
                    showSnackbar("Habit marked completed for $date")
                } else {
                    markHabitMissedUseCase(habitId, date)
                    showSnackbar("Habit marked missed for $date")
                }
                // The flow will automatically update when DB changes, no manual reload needed
            } catch (e: Exception) {
                Timber.e(e, "Failed to toggle habit completion for $date")
                showSnackbar("Failed to update completion: ${e.localizedMessage}")
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                deleteHabitUseCase(habitId)
                showSnackbar("Habit deleted successfully.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete habit: $habitId")
                showSnackbar("Failed to delete habit: ${e.localizedMessage}")
            }
        }
    }

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                archiveHabitUseCase(habitId)
                showSnackbar("Habit archived successfully.")
            } catch (e: Exception) {
                Timber.e(e, "Failed to archive habit: $habitId")
                showSnackbar("Failed to archive habit: ${e.localizedMessage}")
            }
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarEvent.send(message)
        }
    }
}