package com.androidforge.streakwise.domain.usecase.onboarding

import com.androidforge.streakwise.core.datastore.PreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsOnboardingCompletedUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    operator fun invoke(): Flow<Boolean> {
        return preferencesManager.onboardingCompleted
    }
}