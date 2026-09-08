package com.toukir.tasbeeh.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.theme.AppColorTheme
import com.toukir.tasbeeh.ui.theme.AppTheme
import androidx.compose.material3.AlertDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    currentColorTheme: AppColorTheme = AppColorTheme.Gold,
    onColorThemeChange: (AppColorTheme) -> Unit = {},
    isSoundEnabled: Boolean,
    onSoundEnabledChange: (Boolean) -> Unit,
    isVibrateTapEnabled: Boolean,
    onVibrateTapChange: (Boolean) -> Unit,
    isVibrate100Enabled: Boolean,
    onVibrate100Change: (Boolean) -> Unit,
    currentLanguage: String = "en",
    onLanguageChange: (String) -> Unit = {},
    showCounterCircle: Boolean = true,
    onShowCounterCircleChange: (Boolean) -> Unit = {},
    isToastReminderEnabled: Boolean,
    onToastReminderEnabledChange: (Boolean) -> Unit,
    toastReminderText: String,
    onToastReminderTextChange: (String) -> Unit,
    toastReminderInterval: Int,
    onToastReminderIntervalChange: (Int) -> Unit,
    onBackup: (Uri) -> Unit,
    onRestore: (Uri) -> Unit,
    onDismiss: () -> Unit,
    context: Context,
    isLoggedIn: Boolean = false,
    isRestoring: Boolean = false,
    userEmail: String? = null,
    userDisplayName: String? = null,
    userPhotoUrl: String? = null,
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    isLeaderboardEnabled: Boolean = false,
    onLeaderboardEnabledChange: (Boolean) -> Unit = {}
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            onBackup(uri)
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onRestore(uri)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings_title)) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            StudioIcon(StudioIcons.ArrowBack, contentDescription = "Back")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            SettingsScreenContent(
                modifier = Modifier.padding(innerPadding),
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                currentColorTheme = currentColorTheme,
                onColorThemeChange = onColorThemeChange,
                isSoundEnabled = isSoundEnabled,
                onSoundEnabledChange = onSoundEnabledChange,
                isVibrateTapEnabled = isVibrateTapEnabled,
                onVibrateTapChange = onVibrateTapChange,
                isVibrate100Enabled = isVibrate100Enabled,
                onVibrate100Change = onVibrate100Change,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                showCounterCircle = showCounterCircle,
                onShowCounterCircleChange = onShowCounterCircleChange,
                isToastReminderEnabled = isToastReminderEnabled,
                onToastReminderEnabledChange = onToastReminderEnabledChange,
                toastReminderText = toastReminderText,
                onToastReminderTextChange = onToastReminderTextChange,
                toastReminderInterval = toastReminderInterval,
                onToastReminderIntervalChange = onToastReminderIntervalChange,
                onBackupClick = { backupLauncher.launch("tasbeeh_backup.json") },
                onRestoreClick = { restoreLauncher.launch(arrayOf("application/json")) },
                isLoggedIn = isLoggedIn,
                isRestoring = isRestoring,
                userEmail = userEmail,
                userDisplayName = userDisplayName,
                userPhotoUrl = userPhotoUrl,
                onLoginClick = onLoginClick,
                onLogoutClick = { showLogoutConfirmation = true },
                isLeaderboardEnabled = isLeaderboardEnabled,
                onLeaderboardEnabledChange = onLeaderboardEnabledChange
            )
        }

        if (showLogoutConfirmation) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmation = false },
                title = { Text(stringResource(R.string.logout_confirm_title)) },
                text = { Text(stringResource(R.string.logout_confirm_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutConfirmation = false
                            onLogoutClick()
                        }
                    ) {
                        Text(stringResource(R.string.logout_btn), color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirmation = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
