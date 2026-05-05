/**
 * This is the top-level build file where you can add configuration options common to all sub-projects/modules.
 * In a production environment, dependency versions would typically be managed in `gradle/libs.versions.toml`.
 * The following `libs.versions.toml` is provided for context and assumed to be present.
 */

// Dummy libs.versions.toml for reference (should be in gradle/libs.versions.toml)
/*
[versions]
# SDK versions
minSdk = "24"
targetSdk = "34"
compileSdk = "34"
jvmTarget = "1.8"

# Android Build Tools
androidGradlePlugin = "8.1.0"
kotlin = "1.9.0" # Match with composeCompiler
ksp = "1.9.0-1.0.13" # Should match Kotlin version

# Jetpack Compose
composeBom = "2023.08.00" # Latest stable as of writing, check for newer if available
composeCompiler = "1.5.1" # Must match Kotlin version (1.9.0 -> 1.5.1)
activityCompose = "1.8.0"
lifecycleRuntimeKtx = "2.6.2"
lifecycleViewModelCompose = "2.6.2"
navigationCompose = "2.7.5"
material3 = "1.1.2"

# Dependency Injection
hiltAndroid = "2.48"
hiltCompiler = "2.48"
hiltNavigationCompose = "1.1.0"

# Data Persistence
room = "2.6.0"
datastorePreferences = "1.0.0"

# Background Tasks
workRuntimeKtx = "2.8.1"
workHilt = "1.1.0" # Hilt integration for WorkManager

# Ads
playServicesAds = "22.4.0"

# Logging
timber = "5.0.1"

# Testing
junit = "4.13.2"
androidxJunit = "1.1.5"
androidxEspresso = "3.5.1"
androidxUiTestJunit4 = "1.5.4"
androidxUiTooling = "1.5.4"
androidxUiTestManifest = "1.5.4"

[libraries]
# AndroidX Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "kotlin" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }

# Compose
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "material3" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewModelCompose" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hiltAndroid" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hiltCompiler" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }
hilt-work = { group = "androidx.hilt", name = "hilt-work", version.ref = "workHilt" }
hilt-work-compiler = { group = "androidx.hilt", name = "hilt-compiler", version.ref = "workHilt" } # Hilt WorkManager Annotation Processor

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# WorkManager
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workRuntimeKtx" }

# DataStore
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }

# Ads
play-services-ads = { group = "com.google.android.gms", name = "play-services-ads", version.ref = "playServicesAds" }

# Logging
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "androidxJunit" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "androidxEspresso" }
androidx-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }

# Debugging
androidx-ui-tooling-debug = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }

[plugins]
androidApplication = { id = "com.android.application", version.ref = "androidGradlePlugin" }
androidLibrary = { id = "com.android.library", version.ref = "androidGradlePlugin" }
kotlinAndroid = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlinKsp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hiltAndroid = { id = "com.google.dagger.hilt.android", version.ref = "hiltAndroid" }
googleServices = { id = "com.google.gms.google-services", version = "4.4.0" } # Version for google-services plugin
*/

plugins {
    // Apply common plugins here, but specify 'apply false' so they are not applied to the root project itself.
    // Instead, they will be applied to sub-modules via their build.gradle.kts files.
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinKsp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.googleServices) apply false
}

// Define common configuration for all sub-projects (modules)
subprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// Clean task for the entire project
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}