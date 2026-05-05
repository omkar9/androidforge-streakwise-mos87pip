package com.androidforge.streakwise.presentation.ui.common.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp), // For smaller elements like chips or small buttons
    small = RoundedCornerShape(8.dp), // General small rounding
    medium = RoundedCornerShape(16.dp), // Buttons
    large = RoundedCornerShape(20.dp), // Cards
    extraLarge = RoundedCornerShape(32.dp) // Bottom Sheets (top corners)
)