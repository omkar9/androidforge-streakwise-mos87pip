package com.androidforge.streakwise.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Habit(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val frequencyType: FrequencyType,
    val frequencyValue: List<Int>, // For WEEKLY: DayOfWeek.value (1-7). For DAILY: empty list.
    val reminderTime: LocalTime? = null,
    val creationDate: LocalDate,
    val archived: Boolean = false
) {
    enum class FrequencyType {
        DAILY,
        WEEKLY
    }
}