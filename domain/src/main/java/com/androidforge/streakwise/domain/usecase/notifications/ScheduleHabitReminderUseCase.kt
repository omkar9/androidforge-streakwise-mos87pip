package com.androidforge.streakwise.domain.usecase.notifications

import com.androidforge.streakwise.data.worker.WorkerScheduler
import javax.inject.Inject

class ScheduleHabitReminderUseCase @Inject constructor(
    private val workerScheduler: WorkerScheduler
) {
    operator fun invoke(habitId: Long, hour: Int, minute: Int) {
        workerScheduler.scheduleHabitReminder(habitId, hour, minute)
    }
}