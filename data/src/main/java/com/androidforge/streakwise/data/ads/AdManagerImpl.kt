package com.androidforge.streakwise.data.ads

import android.app.Activity
import android.content.Context
import com.androidforge.streakwise.core.ads.AdManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManagerImpl @Inject constructor(
    private val context: Context
) : AdManager {

    private var interstitialAd: InterstitialAd? = null
    private var currentInterstitialAdUnitId: String? = null

    override fun preloadInterstitialAd(adUnitId: String) {
        if (interstitialAd != null && currentInterstitialAdUnitId == adUnitId) {
            Timber.d("Interstitial ad already loaded for unit ID: $adUnitId")
            return
        }

        currentInterstitialAdUnitId = adUnitId
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.e("Interstitial ad failed to load: ${adError.message}")
                interstitialAd = null
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                Timber.d("Interstitial ad loaded for unit ID: $adUnitId")
                interstitialAd = ad
            }
        })
    }

    override fun showInterstitialAd(activity: Activity, adUnitId: String, onAdDismissed: () -> Unit) {
        if (interstitialAd != null && currentInterstitialAdUnitId == adUnitId) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Timber.d("Interstitial ad dismissed.")
                    interstitialAd = null
                    onAdDismissed()
                    // Preload next ad after one is dismissed
                    preloadInterstitialAd(adUnitId)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Timber.e("Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.d("Interstitial ad showed.")
                }
            }
            interstitialAd?.show(activity)
        } else {
            Timber.w("Interstitial ad not ready or wrong ad unit ID. Preloading one now.")
            onAdDismissed() // Ensure callback is invoked even if ad isn't shown
            preloadInterstitialAd(adUnitId) // Attempt to preload for next time
        }
    }

    override fun isInterstitialAdLoaded(adUnitId: String): Boolean {
        return interstitialAd != null && currentInterstitialAdUnitId == adUnitId
    }
}