package com.androidforge.streakwise.core.ads

import android.app.Activity

/**
 * Abstract interface for managing AdMob ads (banner, interstitial).
 * Decouples ad functionality from Google Mobile Ads SDK specifics.
 */
interface AdManager {
    /**
     * Loads and displays an interstitial ad.
     * @param activity The current activity context, required for showing interstitial ads.
     * @param adUnitId The AdMob ad unit ID for the interstitial ad.
     * @param onAdDismissed Callback invoked when the interstitial ad is dismissed by the user.
     */
    fun showInterstitialAd(activity: Activity, adUnitId: String, onAdDismissed: () -> Unit = {})

    /**
     * Preloads an interstitial ad for faster display later.
     * @param adUnitId The AdMob ad unit ID for the interstitial ad.
     */
    fun preloadInterstitialAd(adUnitId: String)

    /**
     * Checks if an interstitial ad is currently loaded and ready to be shown.
     * @param adUnitId The AdMob ad unit ID for the interstitial ad.
     * @return True if an ad is loaded, false otherwise.
     */
    fun isInterstitialAdLoaded(adUnitId: String): Boolean
}