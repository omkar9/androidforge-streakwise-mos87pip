package com.androidforge.streakwise.data.di

import com.androidforge.streakwise.data.repository.HabitCompletionRepositoryImpl
import com.androidforge.streakwise.data.repository.HabitRepositoryImpl
import com.androidforge.streakwise.domain.repository.HabitCompletionRepository
import com.androidforge.streakwise.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindHabitCompletionRepository(impl: HabitCompletionRepositoryImpl): HabitCompletionRepository
}