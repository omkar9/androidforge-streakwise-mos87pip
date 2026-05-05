package com.androidforge.streakwise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidforge.streakwise.data.local.entity.HabitEntity
import com.androidforge.streakwise.data.local.entity.relations.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Long): Flow<HabitEntity?>

    @Query("UPDATE habits SET archived = 1 WHERE id = :habitId")
    suspend fun archiveHabit(habitId: Long)

    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY name ASC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitWithCompletions(habitId: Long): Flow<HabitWithCompletions?>

    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY name ASC")
    fun getAllActiveHabitsWithCompletions(): Flow<List<HabitWithCompletions>>
}