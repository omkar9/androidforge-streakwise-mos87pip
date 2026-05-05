package com.androidforge.streakwise.presentation.ui.addedit

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidforge.streakwise.R
import com.androidforge.streakwise.domain.model.Habit
import com.androidforge.streakwise.presentation.ui.common.UiState
import com.androidforge.streakwise.presentation.ui.common.components.AppButton
import com.androidforge.streakwise.presentation.ui.common.components.ShimmerBox
import com.androidforge.streakwise.presentation.ui.common.theme.StreakWiseTheme
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitScreen(
    habitId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: AddEditHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val postNotificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Optionally show a message if permission is denied
            viewModel.showSnackbar(context.getString(R.string.permission_denied_message))
        }
    }

    LaunchedEffect(Unit) {
        if (habitId != -1L && habitId != null) {
            viewModel.loadHabit(habitId)
        }
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle save success navigation
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onNavigateBack()
            if (habitId == -1L || habitId == null) {
                viewModel.triggerInterstitialAd(activity)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = if (habitId == -1L || habitId == null) R.string.add_edit_habit_title_add else R.string.add_edit_habit_title_edit),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    AddEditHabitShimmer()
                }
                is UiState.Error -> {
                    val errorMessage = (uiState as UiState.Error).message
                    ErrorState(message = errorMessage, onRetry = { viewModel.loadHabit(habitId) })
                }
                is UiState.Success -> {
                    val state = (uiState as UiState.Success).data
                    HabitForm(
                        state = state,
                        onHabitNameChange = viewModel::onHabitNameChange,
                        onHabitDescriptionChange = viewModel::onHabitDescriptionChange,
                        onFrequencyTypeChange = viewModel::onFrequencyTypeChange,
                        onToggleDayOfWeek = viewModel::onToggleDayOfWeek,
                        onReminderTimeChange = { hour, minute ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.onReminderTimeChange(hour, minute)
                            }
                        },
                        onClearReminderTime = viewModel::onClearReminderTime,
                        onSaveHabit = viewModel::saveHabit
                    )
                }
                is UiState.Empty -> {
                    // Should not happen for add/edit, but handle defensively
                    Text(stringResource(R.string.error_loading_habits))
                }
                is UiState.Offline -> {
                    // For add/edit, offline might prevent saving new habits or updating existing ones
                    ErrorState(message = stringResource(R.string.offline_message), onRetry = { viewModel.loadHabit(habitId) })
                }
            }
        }
    }
}

@Composable
fun HabitForm(
    state: AddEditHabitScreenState,
    onHabitNameChange: (String) -> Unit,
    onHabitDescriptionChange: (String) -> Unit,
    onFrequencyTypeChange: (Habit.FrequencyType) -> Unit,
    onToggleDayOfWeek: (DayOfWeek) -> Unit,
    onReminderTimeChange: (Int, Int) -> Unit,
    onClearReminderTime: () -> Unit,
    onSaveHabit: () -> Unit
) {
    val context = LocalContext.current

    OutlinedTextField(
        value = state.habitName,
        onValueChange = onHabitNameChange,
        label = { Text(stringResource(R.string.habit_name_label)) },
        placeholder = { Text(stringResource(R.string.habit_name_placeholder)) },
        modifier = Modifier.fillMaxWidth(),
        isError = state.habitNameError,
        supportingText = {
            if (state.habitNameError) {
                Text(stringResource(R.string.habit_name_empty_error))
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )

    OutlinedTextField(
        value = state.habitDescription,
        onValueChange = onHabitDescriptionChange,
        label = { Text(stringResource(R.string.habit_description_label)) },
        placeholder = { Text(stringResource(R.string.habit_description_placeholder)) },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    Text(
        text = stringResource(R.string.habit_frequency_label),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AppButton(
            onClick = { onFrequencyTypeChange(Habit.FrequencyType.DAILY) },
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.frequency_daily),
            containerColor = if (state.frequencyType == Habit.FrequencyType.DAILY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (state.frequencyType == Habit.FrequencyType.DAILY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        AppButton(
            onClick = { onFrequencyTypeChange(Habit.FrequencyType.WEEKLY) },
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.frequency_weekly),
            containerColor = if (state.frequencyType == Habit.FrequencyType.WEEKLY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (state.frequencyType == Habit.FrequencyType.WEEKLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    AnimatedVisibility(
        visible = state.frequencyType == Habit.FrequencyType.WEEKLY,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DayOfWeek.entries.forEach { dayOfWeek ->
                val isSelected = state.frequencyValue.contains(dayOfWeek.value)
                AppButton(
                    onClick = { onToggleDayOfWeek(dayOfWeek) },
                    modifier = Modifier.weight(1f),
                    text = dayOfWeek.toShortString(context),
                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.reminder_time_label),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth()
    )

    val timePickerDialog = TimePickerDialog(
        context,
        {
            _, hourOfDay, minute ->
            onReminderTimeChange(hourOfDay, minute)
        },
        state.reminderTime?.hour ?: LocalTime.now().hour,
        state.reminderTime?.minute ?: LocalTime.now().minute,
        true // 24 hour format
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clickable { timePickerDialog.show() }
                .semantics { contentDescription = "Set reminder time" },
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = state.reminderTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: stringResource(R.string.set_time),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        AnimatedVisibility(
            visible = state.reminderTime != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            IconButton(
                onClick = onClearReminderTime,
                modifier = Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Clear reminder time" }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.clear_time),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    AppButton(
        onClick = onSaveHabit,
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.save_habit)
    )
}

@Composable
fun AddEditHabitShimmer() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ShimmerBox(height = 56.dp, shape = MaterialTheme.shapes.small)
        ShimmerBox(height = 56.dp, shape = MaterialTheme.shapes.small)
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerBox(width = 150.dp, height = 24.dp, shape = MaterialTheme.shapes.extraSmall)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerBox(modifier = Modifier.weight(1f), height = 48.dp, shape = MaterialTheme.shapes.medium)
            ShimmerBox(modifier = Modifier.weight(1f), height = 48.dp, shape = MaterialTheme.shapes.medium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerBox(width = 180.dp, height = 24.dp, shape = MaterialTheme.shapes.extraSmall)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) {
                ShimmerBox(modifier = Modifier.weight(1f), height = 48.dp, shape = MaterialTheme.shapes.medium)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerBox(height = 48.dp, shape = MaterialTheme.shapes.medium)
    }
}

@Composable
fun DayOfWeek.toShortString(context: Context): String {
    return when (this) {
        DayOfWeek.MONDAY -> stringResource(R.string.monday_short)
        DayOfWeek.TUESDAY -> stringResource(R.string.tuesday_short)
        DayOfWeek.WEDNESDAY -> stringResource(R.string.wednesday_short)
        DayOfWeek.THURSDAY -> stringResource(R.string.thursday_short)
        DayOfWeek.FRIDAY -> stringResource(R.string.friday_short)
        DayOfWeek.SATURDAY -> stringResource(R.string.saturday_short)
        DayOfWeek.SUNDAY -> stringResource(R.string.sunday_short)
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditHabitScreenPreview() {
    StreakWiseTheme {
        AddEditHabitScreen(
            habitId = null,
            onNavigateBack = {}
        )
    }
}