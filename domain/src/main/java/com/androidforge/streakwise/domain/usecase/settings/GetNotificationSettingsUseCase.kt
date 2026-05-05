package com.androidforge.streakwise.domain.usecase.settings

import com.androidforge.streakwise.core.datastore.PreferencesManager
import com.androidforge.streakwise.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    operator fun invoke(): Flow<NotificationSettings> {
        return preferencesManager.notificationSettings
    }
}