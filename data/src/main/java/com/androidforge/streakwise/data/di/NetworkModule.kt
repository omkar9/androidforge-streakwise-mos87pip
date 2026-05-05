package com.androidforge.streakwise.data.di

import com.androidforge.streakwise.core.network.NetworkStatusTracker
import com.androidforge.streakwise.data.network.ConnectivityManagerNetworkStatusTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkStatusTracker(impl: ConnectivityManagerNetworkStatusTracker): NetworkStatusTracker
}