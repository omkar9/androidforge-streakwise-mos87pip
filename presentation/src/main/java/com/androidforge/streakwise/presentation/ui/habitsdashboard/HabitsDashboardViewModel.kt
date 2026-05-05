package com.androidforge.streakwise.presentation.ui.habitsdashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakwise.core.ads.AdManager
import com.androidforge.streakwise.core.network.NetworkStatus
import com.androidforge.streakwise.core.network.NetworkStatusTracker
import com.androidforge.streakwise.domain.usecase.completions.MarkHabitCompletedUseCase
import com.androidforge.streakwise.domain.usecase.completions.MarkHabitMissedUseCase
import com.androidforge.streakwise.domain.usecase.habits.GetAllActiveHabitsUseCase
import com.androidforge.streakwise.presentation.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HabitsDashboardViewModel @Inject constructor(
    private val getAllActiveHabitsUseCase: GetAllActiveHabitsUseCase,
    private val markHabitCompletedUseCase: MarkHabitCompletedUseCase,
    private val markHabitMissedUseCase: MarkHabitMissedUseCase,
    private val adManager: AdManager,
    networkStatusTracker: NetworkStatusTracker
) : ViewModel() {

    private val _forceRefresh = MutableStateFlow(false)

    val networkStatus: StateFlow<NetworkStatus> = networkStatusTracker.networkStatus.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkStatus.Unavailable // Assume unavailable until checked
    )

    val uiState: StateFlow<HabitsDashboardUiState> = _forceRefresh
        .flatMapLatest { 
            getAllActiveHabitsUseCase()
                .map { habits ->
                    if (habits.isEmpty()) {
                        UiState.Empty()
                    } else {
                        UiState.Success(habits)
                    }
                }
                .onStart { emit(UiState.Loading) }
                .catch { e ->
                    Timber.e(e, "Error loading habits for dashboard")
                    emit(UiState.Error(e.localizedMessage ?: "Unknown error loading habits", e))
                }
        }
        .combine(networkStatus) { state, network ->
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

    init {
        loadHabits()
        adManager.preloadInterstitialAd("ca-app-pub-3940256099942544/1033173712") // Test Ad Unit ID
    }

    fun loadHabits() {
        _forceRefresh.value = !_forceRefresh.value
    }

    fun toggleHabitCompletion(habitId: Long, date: LocalDate, isCompleted: Boolean) {
        viewModelScope.launch {
            if (isCompleted) {
                markHabitCompletedUseCase(habitId, date)
            } else {
                markHabitMissedUseCase(habitId, date)
            }
            // No explicit refresh needed as the Flow from GetAllActiveHabitsUseCase will react to DB changes
        }
    }

    // Method to trigger interstitial ad, can be called on certain events
    fun triggerInterstitialAd(activity: Activity) {
        adManager.showInterstitialAd(activity, "ca-app-pub-3940256099942544/1033173712") // Test Ad Unit ID
    }
}