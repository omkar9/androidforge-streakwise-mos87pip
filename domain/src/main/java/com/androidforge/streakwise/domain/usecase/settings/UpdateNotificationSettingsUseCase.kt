package com.androidforge.streakwise.domain.usecase.settings

import com.androidforge.streakwise.core.datastore.PreferencesManager
import javax.inject.Inject

class UpdateNotificationSettingsUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    suspend operator fun invoke(enabled: Boolean) {
        preferencesManager.updateNotificationSettings(enabled)
    }
}