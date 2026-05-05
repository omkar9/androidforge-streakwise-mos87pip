package com.androidforge.streakwise.presentation.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidforge.streakwise.R
import com.androidforge.streakwise.presentation.ui.common.UiState
import com.androidforge.streakwise.presentation.ui.common.components.AppButton
import com.androidforge.streakwise.presentation.ui.common.components.ShimmerBox
import com.androidforge.streakwise.presentation.ui.common.theme.StreakWiseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val postNotificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            viewModel.showSnackbar(context.getString(R.string.permission_denied_message))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
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
                .padding(16.dp)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    SettingsShimmer()
                }
                is UiState.Success -> {
                    val settings = (uiState as UiState.Success).data
                    SettingsContent(
                        settings = settings,
                        onToggleReminders = { isChecked ->
                            if (isChecked) {
                                // Request permission if not granted and enabling reminders
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.toggleGlobalReminders(isChecked)
                                }
                            } else {
                                viewModel.toggleGlobalReminders(isChecked)
                            }
                        },
                        onNavigateToAbout = onNavigateToAbout,
                        onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy
                    )
                }
                is UiState.Error -> {
                    ErrorState(message = (uiState as UiState.Error).message, onRetry = viewModel::loadSettings)
                }
                is UiState.Empty -> {
                    // Settings should never be truly empty, maybe default values not loaded
                    ErrorState(message = stringResource(R.string.error_loading_settings), onRetry = viewModel::loadSettings)
                }
                is UiState.Offline -> {
                    ErrorState(message = stringResource(R.string.offline_message), onRetry = viewModel::loadSettings)
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    settings: SettingsScreenState,
    onToggleReminders: (Boolean) -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    Text(
        text = stringResource(R.string.general_settings),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    SettingItem(
        title = stringResource(R.string.enable_reminders),
        description = null,
        onClick = { onToggleReminders(!settings.isGlobalRemindersEnabled) }
    ) {
        Switch(
            checked = settings.isGlobalRemindersEnabled,
            onCheckedChange = onToggleReminders,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.semantics { contentDescription = "Toggle global reminders" }
        )
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

    Text(
        text = stringResource(R.string.legal),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    SettingItem(
        title = stringResource(R.string.about_app),
        onClick = onNavigateToAbout
    ) {
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.about_app))
    }

    SettingItem(
        title = stringResource(R.string.privacy_policy),
        onClick = onNavigateToPrivacyPolicy
    ) {
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.privacy_policy))
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String? = null,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailingContent()
    }
}

@Composable
fun SettingsShimmer() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ShimmerBox(width = 150.dp, height = 24.dp, shape = MaterialTheme.shapes.extraSmall)
        ShimmerBox(height = 56.dp, shape = MaterialTheme.shapes.small)
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        ShimmerBox(width = 100.dp, height = 24.dp, shape = MaterialTheme.shapes.extraSmall)
        ShimmerBox(height = 56.dp, shape = MaterialTheme.shapes.small)
        ShimmerBox(height = 56.dp, shape = MaterialTheme.shapes.small)
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
        Icon(
            painter = painterResource(id = R.drawable.ic_error_state),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.error_loading_settings),
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
fun SettingsScreenPreview() {
    StreakWiseTheme {
        SettingsScreen(
            onNavigateBack = {},
            onNavigateToAbout = {},
            onNavigateToPrivacyPolicy = {}
        )
    }
}