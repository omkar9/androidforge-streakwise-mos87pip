package com.androidforge.streakwise.presentation.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import timber.log.Timber

@Composable
fun AdBanner(modifier: Modifier = Modifier, adUnitId: String) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp) // Standard banner height
            .background(Color.Transparent)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = {
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId // Use passed adUnitId
                    loadAd(AdRequest.Builder().build())
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Timber.d("AdBanner: Ad loaded successfully")
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Timber.e("AdBanner: Ad failed to load: ${adError.message}")
                        }

                        override fun onAdOpened() {
                            Timber.d("AdBanner: Ad opened")
                        }

                        override fun onAdClicked() {
                            Timber.d("AdBanner: Ad clicked")
                        }

                        override fun onAdClosed() {
                            Timber.d("AdBanner: Ad closed")
                        }
                    }
                }
            },
            update = {\ adView ->
                adView.loadAd(AdRequest.Builder().build())
            }
        )
    }
}