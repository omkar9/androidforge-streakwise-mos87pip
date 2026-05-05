package com.androidforge.streakwise.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.androidforge.streakwise.data.local.entity.HabitCompletionEntity
import com.androidforge.streakwise.data.local.entity.HabitEntity

data class HabitWithCompletions(
    @Embedded val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val completions: List<HabitCompletionEntity>
)