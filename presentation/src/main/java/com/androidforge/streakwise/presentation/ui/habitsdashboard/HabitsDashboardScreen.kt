package com.androidforge.streakwise.presentation.ui.habitsdashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidforge.streakwise.R
import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitWithCompletionStatus
import com.androidforge.streakwise.presentation.ui.common.UiState
import com.androidforge.streakwise.presentation.ui.common.components.AdBanner
import com.androidforge.streakwise.presentation.ui.common.components.AppButton
import com.androidforge.streakwise.presentation.ui.common.components.HabitCard
import com.androidforge.streakwise.presentation.ui.common.components.ShimmerHabitCard
import com.androidforge.streakwise.presentation.ui.common.theme.StreakWiseTheme
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsDashboardScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAddEditHabit: (Long?) -> Unit,
    onNavigateToHabitDetail: (Long) -> Unit,
    viewModel: HabitsDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(networkStatus) {
        if (networkStatus == com.androidforge.streakwise.core.network.NetworkStatus.Lost) {
            snackbarHostState.showSnackbar(
                message = "You are offline. Data might be outdated.",
                withDismissAction = true
            )
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.habits_dashboard_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEditHabit(null) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                shape = MaterialTheme.shapes.extraLarge, // Pill shape
                modifier = Modifier.semantics { contentDescription = "Add new habit" }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_habit)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AdBanner(
                modifier = Modifier.fillMaxWidth(),
                adUnitId = stringResource(R.string.ad_unit_id_banner)
            )
        }
    ) {\ paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                is UiState.Loading -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(5) { ShimmerHabitCard() }
                    }
                }
                is UiState.Success -> {
                    val habits = (uiState as UiState.Success).data
                    if (habits.isEmpty()) {
                        EmptyState(
                            title = stringResource(R.string.no_active_habits_title),
                            description = stringResource(R.string.no_active_habits_description),
                            onAddHabitClick = { onNavigateToAddEditHabit(null) }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(habits, key = { it.habit.id }) {
                                HabitCard(
                                    habitWithStatus = it,
                                    onToggleComplete = viewModel::toggleHabitCompletion,
                                    onCardClick = onNavigateToHabitDetail,
                                    onCardLongPress = { habitId ->
                                        // Optionally add a context menu or quick edit here
                                        onNavigateToAddEditHabit(habitId)
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorState(
                        message = (uiState as UiState.Error).message,
                        onRetry = viewModel::loadHabits
                    )
                }
                is UiState.Empty -> {
                    EmptyState(
                        title = stringResource(R.string.no_active_habits_title),
                        description = stringResource(R.string.no_active_habits_description),
                        onAddHabitClick = { onNavigateToAddEditHabit(null) }
                    )
                }
                is UiState.Offline -> {
                    // If offline and there's cached data, it will be shown via Success state
                    // This Offline state is more for when no data can be loaded AND offline
                    OfflineState(message = stringResource(R.string.offline_message), onRetry = viewModel::loadHabits)
                }
            }

            // Show network status banner if offline but still displaying content (e.g., cached data)
            AnimatedVisibility(
                visible = networkStatus == com.androidforge.streakwise.core.network.NetworkStatus.Lost && uiState is UiState.Success,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
                    .padding(vertical = 4.dp)
                    .align(Alignment.TopCenter)
                ) {
                    Text(
                        text = stringResource(R.string.offline_message),
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(title: String, description: String, onAddHabitClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_state),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(
            onClick = onAddHabitClick,
            text = stringResource(R.string.add_habit)
        )
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error_state),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.error_loading_habits),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(
            onClick = onRetry,
            text = stringResource(R.string.retry),
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    }
}

@Composable
fun OfflineState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_offline_state),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.offline_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        AppButton(
            onClick = onRetry,
            text = stringResource(R.string.retry)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HabitsDashboardScreenPreview() {
    StreakWiseTheme {
        HabitsDashboardScreen(
            onNavigateToSettings = {},
            onNavigateToAddEditHabit = { _ -> },
            onNavigateToHabitDetail = { _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HabitsDashboardEmptyStatePreview() {
    StreakWiseTheme {
        EmptyState(
            title = stringResource(R.string.no_active_habits_title),
            description = stringResource(R.string.no_active_habits_description),
            onAddHabitClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HabitsDashboardErrorStatePreview() {
    StreakWiseTheme {
        ErrorState(
            message = stringResource(R.string.error_loading_habits),
            onRetry = {}
        )
    }
}