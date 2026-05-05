package com.androidforge.streakwise.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // All use cases in this project have @Inject constructors and depend on Singleton-scoped repositories.
    // Hilt can automatically provide them when requested in ViewModels or other components (e.g., MainViewModel).
    // Explicit @Provides methods are generally not needed for such simple constructor-injected classes.
    // This module exists to explicitly declare that use cases are part of the dependency graph,
    // even if Hilt's implicit binding is used.

    // If any use case had complex initialization logic or needed a non-default constructor,
    // an explicit @Provides method would be added here.
    // For this project, no explicit @Provides methods are required here.
}