package com.androidforge.streakwise.domain.usecase.notifications

import com.androidforge.streakwise.data.worker.WorkerScheduler
import javax.inject.Inject

class CancelHabitReminderUseCase @Inject constructor(
    private val workerScheduler: WorkerScheduler
) {
    operator fun invoke(habitId: Long) {
        workerScheduler.cancelHabitReminder(habitId)
    }
}