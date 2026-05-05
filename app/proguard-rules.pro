# Add project specific ProGuard rules here.
# By default, the flags in proguard-android-optimize.txt are applied.
# You can remove the line for default flags if you want to start from a clean slate.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all classes and members annotated with @Keep for serialization, Room, etc.
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Room persistence library
-keep class androidx.room.RoomDatabase_Impl { *; }
-keep class * extends androidx.room.RxRoom.* { *; }
-keep class * implements androidx.room.InvalidationTracker$Observer { *; }
-keep class androidx.room.AutoMigrationSpec { *; }
-keep class androidx.room.migration.Migration { *; }

# Dagger Hilt
-keep class dagger.hilt.android.HiltAndroidApp { *; }
-keep class dagger.hilt.android.internal.managers.ActivityComponentManager { *; }
-keep class dagger.hilt.android.internal.managers.FragmentComponentManager { *; }
-keep class dagger.hilt.android.internal.managers.ViewComponentManager { *; }
-keep class dagger.hilt.android.internal.managers.ServiceComponentManager { *; }
-keep class dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$ActivityContextWrapper { *; }
-keep class * extends dagger.hilt.android.internal.builders.* { *; }
-keep class * extends dagger.hilt.android.components.* { *; }
-keep class * extends dagger.hilt.components.* { *; }
-keep class *.Hilt_*.{ *; }
-keep class * implements dagger.hilt.codegen.OriginatingElement { *; }
-dontwarn dagger.hilt.internal.**

# Jetpack Compose
-keep class androidx.compose.ui.tooling.preview.PreviewParameterProvider { *; }
-keep class androidx.compose.runtime.ProvidableCompositionLocal { *; }

# AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep public class com.google.android.gms.ads.AdActivity { *; }
-keep public class com.google.ads.mediation.admob.AdMobAdapter { *; }
-keep class com.google.android.gms.ads.initialization.OnInitializationCompleteListener { *; }
-keep class com.google.android.gms.ads.initialization.InitializationStatus { *; }
-keep class com.google.android.gms.ads.MobileAds { *; }
-keep class com.google.android.gms.ads.AdRequest { *; }
-keep class com.google.android.gms.ads.AdSize { *; }
-keep class com.google.android.gms.ads.AdView { *; }
-keep class com.google.android.gms.ads.interstitial.InterstitialAd { *; }
-keep class com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback { *; }
-keep class com.google.android.gms.ads.FullScreenContentCallback { *; }
-keep class com.google.android.gms.ads.AdError { *; }
-keep class com.google.android.gms.ads.LoadAdError { *; }
-keep class com.google.android.gms.internal.ads.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.google.ads.**
-dontwarn org.checkerframework.**

# WorkManager
-keep class androidx.work.** { *; }
-keep class androidx.work.impl.** { *; }
-keep class androidx.work.impl.utils.** { *; }
-keep class androidx.work.multiprocess.** { *; }
-keep class androidx.hilt.work.** { *; }
-dontwarn androidx.work.**
-dontwarn androidx.hilt.work.**

# Kotlin Coroutines
-keep class kotlinx.coroutines.flow.** { *; }
-keep class kotlinx.coroutines.channels.** { *; }
-dontwarn kotlinx.coroutines.internal.**
-dontwarn org.jetbrains.kotlin.compiler.**

# Timber
-dontwarn timber.log.**