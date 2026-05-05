package com.androidforge.streakwise.domain.usecase.onboarding

import com.androidforge.streakwise.core.datastore.PreferencesManager
import javax.inject.Inject

class SetOnboardingCompletedUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    suspend operator fun invoke() {
        preferencesManager.setOnboardingCompleted(true)
    }
}