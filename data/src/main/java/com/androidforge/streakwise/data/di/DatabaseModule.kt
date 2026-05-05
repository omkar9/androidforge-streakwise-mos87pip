package com.androidforge.streakwise.data.di

import android.content.Context
import androidx.room.Room
import com.androidforge.streakwise.data.local.StreakWiseDatabase
import com.androidforge.streakwise.data.local.dao.HabitCompletionDao
import com.androidforge.streakwise.data.local.dao.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StreakWiseDatabase {
        return Room.databaseBuilder(
            context,
            StreakWiseDatabase::class.java,
            "streakwise-db"
        ).build()
    }

    @Provides
    fun provideHabitDao(database: StreakWiseDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideHabitCompletionDao(database: StreakWiseDatabase): HabitCompletionDao {
        return database.habitCompletionDao()
    }
}