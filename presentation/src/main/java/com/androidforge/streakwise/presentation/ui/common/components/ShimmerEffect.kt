package com.androidforge.streakwise.presentation.ui.common.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androidforge.streakwise.presentation.ui.common.theme.StreakWiseTheme

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    shape: RoundedCornerShape = MaterialTheme.shapes.small
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - 200f, y = translateAnimation.value - 200f),
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )

    Box(
        modifier = modifier
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier)
            .background(brush = brush, shape = shape)
    )
}

@Composable
fun ShimmerHabitCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ShimmerBox(width = 180.dp, height = 24.dp, shape = MaterialTheme.shapes.extraSmall)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerBox(width = 250.dp, height = 16.dp, shape = MaterialTheme.shapes.extraSmall)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(width = 200.dp, height = 16.dp, shape = MaterialTheme.shapes.extraSmall)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBox(width = 18.dp, height = 18.dp, shape = CircleShape)
                Spacer(modifier = Modifier.width(8.dp))
                ShimmerBox(width = 100.dp, height = 16.dp, shape = MaterialTheme.shapes.extraSmall)
                Spacer(modifier = Modifier.weight(1f))
                ShimmerBox(width = 48.dp, height = 48.dp, shape = CircleShape)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewShimmerHabitCard() {
    StreakWiseTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ShimmerHabitCard()
            ShimmerHabitCard()
        }
    }
}