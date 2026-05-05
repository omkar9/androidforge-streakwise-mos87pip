package com.androidforge.streakwise

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class StreakWiseApp : Application() {
    override fun onCreate() {
        super.onCreate() 

        // Initialize Timber for logging in debug builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this) {}
    }
}