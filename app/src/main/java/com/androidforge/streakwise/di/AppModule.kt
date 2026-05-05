package com.androidforge.streakwise.di

import com.androidforge.streakwise.core.ads.AdManager
import com.androidforge.streakwise.core.datastore.PreferencesManager
import com.androidforge.streakwise.core.notifications.NotificationHelper
import com.androidforge.streakwise.core.network.NetworkStatusTracker
import com.androidforge.streakwise.data.ads.AdManagerImpl
import com.androidforge.streakwise.data.datastore.PreferencesManagerImpl
import com.androidforge.streakwise.data.network.ConnectivityManagerNetworkStatusTracker
import com.androidforge.streakwise.data.notifications.NotificationHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNotificationHelper(impl: NotificationHelperImpl): NotificationHelper

    @Binds
    @Singleton
    abstract fun bindAdManager(impl: AdManagerImpl): AdManager

    @Binds
    @Singleton
    abstract fun bindPreferencesManager(impl: PreferencesManagerImpl): PreferencesManager

    @Binds
    @Singleton
    abstract fun bindNetworkStatusTracker(impl: ConnectivityManagerNetworkStatusTracker): NetworkStatusTracker
}