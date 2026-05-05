package com.androidforge.streakwise.presentation.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidforge.streakwise.R
import com.androidforge.streakwise.presentation.ui.common.components.AppButton
import com.androidforge.streakwise.presentation.ui.common.theme.StreakWiseTheme

data class OnboardingPage(
    val imageRes: Int,
    val titleRes: Int,
    val descriptionRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(R.drawable.ic_onboarding_welcome, R.string.onboarding_title_1, R.string.onboarding_description_1),
    OnboardingPage(R.drawable.ic_onboarding_track, R.string.onboarding_title_2, R.string.onboarding_description_2),
    OnboardingPage(R.drawable.ic_onboarding_reminders, R.string.onboarding_title_3, R.string.onboarding_description_3)
)

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val currentPageIndex by viewModel.currentPageIndex.collectAsState()
    val currentPage = onboardingPages[currentPageIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = currentPageIndex,
            transitionSpec = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400)) +
                        fadeIn(animationSpec = tween(400)) togetherWith
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400)) +
                        fadeOut(animationSpec = tween(400))
            },
            label = "onboardingContentAnimation"
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = onboardingPages[it].imageRes),
                    contentDescription = stringResource(id = onboardingPages[it].titleRes),
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = onboardingPages[it].titleRes),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = onboardingPages[it].descriptionRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                onboardingPages.forEachIndexed { index, _ ->
                    IndicatorDot(isSelected = index == currentPageIndex)
                    if (index < onboardingPages.lastIndex) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            if (currentPageIndex == onboardingPages.lastIndex) {
                AppButton(
                    onClick = { viewModel.completeOnboarding(onOnboardingComplete) },
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.get_started)
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    AppButton(
                        onClick = { viewModel.completeOnboarding(onOnboardingComplete) },
                        text = stringResource(R.string.skip),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AppButton(
                        onClick = { viewModel.nextPage() },
                        text = stringResource(R.string.next),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun IndicatorDot(isSelected: Boolean, modifier: Modifier = Modifier) {
    val color = animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(300),
        label = "indicatorDotColor"
    ).value
    val size by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "indicatorDotSize"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    StreakWiseTheme {
        OnboardingScreen(onOnboardingComplete = {})
    }
}