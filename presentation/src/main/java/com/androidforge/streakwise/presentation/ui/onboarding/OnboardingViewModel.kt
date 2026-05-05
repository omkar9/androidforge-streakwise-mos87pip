package com.androidforge.streakwise.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidforge.streakwise.domain.usecase.onboarding.SetOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
) : ViewModel() {

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex = _currentPageIndex.asStateFlow()

    fun nextPage() {
        if (_currentPageIndex.value < onboardingPages.lastIndex) {
            _currentPageIndex.value++
        }
    }

    fun completeOnboarding(onOnboardingComplete: () -> Unit) {
        viewModelScope.launch {
            setOnboardingCompletedUseCase()
            onOnboardingComplete()
        }
    }
}