package com.androidforge.streakwise.presentation.ui.common.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors (Organic Warm)
val PrimaryLight = Color(0xFF5F8575) // primary
val PrimaryVariantLight = Color(0xFF8AA69A) // primaryVariant
val SecondaryLight = Color(0xFFD96C4E) // secondary
val BackgroundLight = Color(0xFFFDFCF8) // background
val SurfaceLight = Color(0xFFF5F3ED) // surface
val SurfaceVariantLight = Color(0xFFE0DCD5) // surfaceVariant
val OnPrimaryLight = Color(0xFFFFFFFF) // onPrimary
val OnBackgroundLight = Color(0xFF2A2A2A) // onBackground
val OnSurfaceLight = Color(0xFF2A2A2A) // onSurface
val SuccessLight = Color(0xFF8BC34A) // success
val ErrorLight = Color(0xFFD32F2F) // error
val WarningLight = Color(0xFFFFC107) // warning

// Dark Theme Colors (Adapted for Organic Warm feel)
val PrimaryDark = Color(0xFF8AA69A) // primary (lighter for dark mode)
val PrimaryVariantDark = Color(0xFF5F8575) // primaryVariant (darker for dark mode)
val SecondaryDark = Color(0xFFE8987F) // secondary (lighter for dark mode)
val BackgroundDark = Color(0xFF2A2A2A) // background
val SurfaceDark = Color(0xFF383838) // surface
val SurfaceVariantDark = Color(0xFF4C4C4C) // surfaceVariant
val OnPrimaryDark = Color(0xFF000000) // onPrimary (dark text on light primary)
val OnBackgroundDark = Color(0xFFFDFCF8) // onBackground (light text on dark background)
val OnSurfaceDark = Color(0xFFFDFCF8) // onSurface (light text on dark surface)
val SuccessDark = Color(0xFFAED581) // success
val ErrorDark = Color(0xFFEF9A9A) // error
val WarningDark = Color(0xFFFFEB3B) // warning

// Generic Material3 colors (can be mapped to the above)
val md_theme_light_primary = PrimaryLight
val md_theme_light_onPrimary = OnPrimaryLight
val md_theme_light_primaryContainer = PrimaryVariantLight
val md_theme_light_onPrimaryContainer = OnBackgroundLight // Using onBackground for contrast
val md_theme_light_secondary = SecondaryLight
val md_theme_light_onSecondary = OnSecondaryLight // Define if needed, assuming black/white for now
val md_theme_light_secondaryContainer = Color(0xFFFFDAD4) // Default M3, adjust if specific needed
val md_theme_light_onSecondaryContainer = Color(0xFF400007) // Default M3
val md_theme_light_tertiary = Color(0xFF705C2E) // Default M3
val md_theme_light_onTertiary = Color(0xFFFFFFFF) // Default M3
val md_theme_light_tertiaryContainer = Color(0xFFFBDFA6) // Default M3
val md_theme_light_onTertiaryContainer = Color(0xFF261A00) // Default M3
val md_theme_light_error = ErrorLight
val md_theme_light_onError = OnErrorLight // Define if needed
val md_theme_light_errorContainer = Color(0xFFFFDAD4) // Default M3
val md_theme_light_onErrorContainer = Color(0xFF410002) // Default M3
val md_theme_light_background = BackgroundLight
val md_theme_light_onBackground = OnBackgroundLight
val md_theme_light_surface = SurfaceLight
val md_theme_light_onSurface = OnSurfaceLight
val md_theme_light_surfaceVariant = SurfaceVariantLight
val md_theme_light_onSurfaceVariant = OnSurfaceLight // Using OnSurfaceLight
val md_theme_light_outline = Color(0xFF8B9287) // Default M3
val md_theme_light_inverseOnSurface = Color(0xFFF0F1E8) // Default M3
val md_theme_light_inverseSurface = Color(0xFF30312C) // Default M3
val md_theme_light_inversePrimary = PrimaryDark // Using dark primary for inverse
val md_theme_light_shadow = Color(0xFF000000) // Default M3
val md_theme_light_surfaceTint = PrimaryLight
val md_theme_light_outlineVariant = Color(0xFFC0C8BC) // Default M3
val md_theme_light_scrim = Color(0xFF000000) // Default M3

val md_theme_dark_primary = PrimaryDark
val md_theme_dark_onPrimary = OnPrimaryDark
val md_theme_dark_primaryContainer = PrimaryVariantDark
val md_theme_dark_onPrimaryContainer = OnBackgroundDark // Using onBackground for contrast
val md_theme_dark_secondary = SecondaryDark
val md_theme_dark_onSecondary = OnSecondaryDark // Define if needed
val md_theme_dark_secondaryContainer = Color(0xFF5B110B) // Default M3
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDAD4) // Default M3
val md_theme_dark_tertiary = Color(0xFFDEC58C) // Default M3
val md_theme_dark_onTertiary = Color(0xFF3E2E04) // Default M3
val md_theme_dark_tertiaryContainer = Color(0xFF574519) // Default M3
val md_theme_dark_onTertiaryContainer = Color(0xFFFBDFA6) // Default M3
val md_theme_dark_error = ErrorDark
val md_theme_dark_onError = OnErrorDark // Define if needed
val md_theme_dark_errorContainer = Color(0xFF93000A) // Default M3
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD4) // Default M3
val md_theme_dark_background = BackgroundDark
val md_theme_dark_onBackground = OnBackgroundDark
val md_theme_dark_surface = SurfaceDark
val md_theme_dark_onSurface = OnSurfaceDark
val md_theme_dark_surfaceVariant = SurfaceVariantDark
val md_theme_dark_onSurfaceVariant = OnSurfaceDark // Using OnSurfaceDark
val md_theme_dark_outline = Color(0xFF9FA89D) // Default M3
val md_theme_dark_inverseOnSurface = Color(0xFF30312C) // Default M3
val md_theme_dark_inverseSurface = Color(0xFFE8E9E0) // Default M3
val md_theme_dark_inversePrimary = PrimaryLight // Using light primary for inverse
val md_theme_dark_shadow = Color(0xFF000000) // Default M3
val md_theme_dark_surfaceTint = PrimaryDark
val md_theme_dark_outlineVariant = Color(0xFF434C41) // Default M3
val md_theme_dark_scrim = Color(0xFF000000) // Default M3


val seed = Color(0xFF6750A4)
val Success = SuccessLight
val Warning = WarningLight

// Assuming OnSecondary and OnError are typically contrasting colors like black/white
val OnSecondaryLight = Color(0xFFFFFFFF)
val OnErrorLight = Color(0xFFFFFFFF)
val OnSecondaryDark = Color(0xFF000000)
val OnErrorDark = Color(0xFF000000)