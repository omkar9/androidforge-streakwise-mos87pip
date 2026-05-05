package com.androidforge.streakwise.presentation.ui.habitdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidforge.streakwise.R
import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.domain.model.HabitCompletion
import com.androidforge.streakwise.presentation.ui.common.UiState
import com.androidforge.streakwise.presentation.ui.common.components.AppButton
import com.androidforge.streakwise.presentation.ui.common.components.ShimmerBox
import com.androidforge.streakwise.presentation.ui.common.theme.StreakWiseTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEditHabit: (Long) -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentMonth by viewModel.currentMonth.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHabitDetails(habitId)
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.habit_detail_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                ) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEditHabit(habitId) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_habit),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { showArchiveDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = stringResource(R.string.archive_habit),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_habit),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                is UiState.Loading -> {
                    HabitDetailShimmer()
                }
                is UiState.Success -> {
                    val data = (uiState as UiState.Success).data
                    HabitDetailContent(
                        habit = data.habit,
                        currentStreak = data.currentStreak,
                        longestStreak = data.longestStreak,
                        completions = data.completions,
                        currentMonth = currentMonth,
                        onPreviousMonth = viewModel::previousMonth,
                        onNextMonth = viewModel::nextMonth,
                        onToggleCompletion = viewModel::toggleHabitCompletionForDate
                    )

                    if (showDeleteDialog) {
                        ConfirmActionDialog(
                            title = stringResource(R.string.confirm_delete_title),
                            message = stringResource(R.string.confirm_delete_message, data.habit.name),
                            confirmButtonText = stringResource(R.string.delete),
                            onConfirm = { viewModel.deleteHabit(habitId); onNavigateBack() },
                            onDismiss = { showDeleteDialog = false }
                        )
                    }

                    if (showArchiveDialog) {
                        ConfirmActionDialog(
                            title = stringResource(R.string.confirm_archive_title),
                            message = stringResource(R.string.confirm_archive_message, data.habit.name),
                            confirmButtonText = stringResource(R.string.archive),
                            onConfirm = { viewModel.archiveHabit(habitId); onNavigateBack() },
                            onDismiss = { showArchiveDialog = false }
                        )
                    }
                }
                is UiState.Error -> {
                    ErrorState(message = (uiState as UiState.Error).message, onRetry = { viewModel.loadHabitDetails(habitId) })
                }
                is UiState.Empty -> {
                    EmptyState(
                        title = stringResource(R.string.no_completion_history_title),
                        description = stringResource(R.string.no_completion_history_description),
                        onRetry = { viewModel.loadHabitDetails(habitId) }
                    )
                }
                is UiState.Offline -> {
                    ErrorState(message = stringResource(R.string.offline_message), onRetry = { viewModel.loadHabitDetails(habitId) })
                }
            }
        }
    }
}

@Composable
fun HabitDetailContent(
    habit: Habit,
    currentStreak: Int,
    longestStreak: Int,
    completions: List<HabitCompletion>,
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleCompletion: (Long, LocalDate, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = habit.name,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (habit.description.isNotBlank()) {
            Text(
                text = habit.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakDisplay(label = stringResource(R.string.current_streak), streak = currentStreak)
            StreakDisplay(label = stringResource(R.string.longest_streak), streak = longestStreak)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.history),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        CalendarView(
            currentMonth = currentMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
            completions = completions,
            habit = habit,
            onToggleCompletion = onToggleCompletion
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Additional details like frequency, reminder time if needed
        Text(
            text = "Frequency: ${habit.frequencyType}" + if (habit.frequencyType == Habit.FrequencyType.WEEKLY) " on ${habit.frequencyValue.joinToString { DayOfWeek.of(it).getDisplayName(TextStyle.SHORT, Locale.getDefault()) }}" else "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        habit.reminderTime?.let { time ->
            Text(
                text = "Reminder: ${time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StreakDisplay(label: String, streak: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = streak.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    completions: List<HabitCompletion>,
    habit: Habit,
    onToggleCompletion: (Long, LocalDate, Boolean) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val daysInMonth = (1..lastDayOfMonth.dayOfMonth).toList()

    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday, 1 for Monday

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.previous_month))
            }
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.year,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.next_month), modifier = Modifier.scale(scaleX = -1f))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        val daysOfWeek = DayOfWeek.values().map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false, // Disable scrolling for the grid itself
            modifier = Modifier.height(300.dp) // Fixed height to prevent excessive scrolling issues
        ) {
            items(daysOfWeek) { day ->
                Text(
                    text = day,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            // Empty cells for days before the 1st of the month
            items(firstDayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            items(daysInMonth, key = { it }) {
                val date = currentMonth.atDay(it)
                val isCompleted = completions.any { c -> c.completionDate == date && c.isCompleted }
                val isToday = date == LocalDate.now()
                val isClickable = date <= LocalDate.now()

                CalendarDayCell(
                    dayOfMonth = it,
                    isCompleted = isCompleted,
                    isToday = isToday,
                    isClickable = isClickable,
                    onToggleCompletion = { isChecked ->
                        onToggleCompletion(habit.id, date, isChecked)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarDayCell(
    dayOfMonth: Int,
    isCompleted: Boolean,
    isToday: Boolean,
    isClickable: Boolean,
    onToggleCompletion: (Boolean) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            isToday -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            else -> Color.Transparent
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "calendarCellBackground"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isToday -> MaterialTheme.colorScheme.secondary
            else -> Color.Transparent
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "calendarCellBorder"
    )

    val textColor by animateColorAsState(
        targetValue = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "calendarCellText"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Makes cells square
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .border(1.dp, borderColor, MaterialTheme.shapes.small)
            .let { m ->
                if (isClickable) {
                    m.combinedClickable(onLongClick = { onToggleCompletion(!isCompleted) }) { onToggleCompletion(!isCompleted) }
                } else m
            }
            .semantics { contentDescription = stringResource(R.string.calendar_day_description, dayOfMonth, if (isCompleted) stringResource(R.string.completion_status_completed) else stringResource(R.string.completion_status_missed)) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
            AnimatedVisibility(
                visible = isCompleted,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ConfirmActionDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(message, style = MaterialTheme.typography.bodyLarge) },
        confirmButton = {
            AppButton(
                onClick = onConfirm,
                text = confirmButtonText,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        },
        dismissButton = {
            AppButton(
                onClick = onDismiss,
                text = stringResource(R.string.cancel),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

@Composable
fun HabitDetailShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ShimmerBox(width = 250.dp, height = 40.dp, shape = MaterialTheme.shapes.extraSmall)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerBox(width = 300.dp, height = 20.dp, shape = MaterialTheme.shapes.extraSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ShimmerBox(width = 80.dp, height = 16.dp, shape = MaterialTheme.shapes.extraSmall)
                Spacer(modifier = Modifier.height(4.dp))
                ShimmerBox(width = 50.dp, height = 30.dp, shape = MaterialTheme.shapes.extraSmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ShimmerBox(width = 80.dp, height = 16.dp, shape = MaterialTheme.shapes.extraSmall)
                Spacer(modifier = Modifier.height(4.dp))
                ShimmerBox(width = 50.dp, height = 30.dp, shape = MaterialTheme.shapes.extraSmall)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerBox(width = 150.dp, height = 24.dp, shape = MaterialTheme.shapes.extraSmall)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 300.dp, shape = MaterialTheme.shapes.large)
    }
}

@Composable
fun EmptyState(title: String, description: String, onRetry: () -> Unit) {
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
            onClick = onRetry,
            text = stringResource(R.string.retry)
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
            text = stringResource(R.string.habit_detail_error),
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

@Preview(showBackground = true)
@Composable
fun HabitDetailScreenPreview() {
    StreakWiseTheme {
        HabitDetailScreen(
            habitId = 1L,
            onNavigateBack = {},
            onNavigateToEditHabit = {}
        )
    }
}