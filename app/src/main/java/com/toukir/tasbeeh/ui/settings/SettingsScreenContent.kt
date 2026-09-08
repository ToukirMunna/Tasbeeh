package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.ui.theme.AppColorTheme
import com.toukir.tasbeeh.ui.theme.AppTheme

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
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
    isToastReminderEnabled: Boolean,
    onToastReminderEnabledChange: (Boolean) -> Unit,
    toastReminderText: String,
    onToastReminderTextChange: (String) -> Unit,
    toastReminderInterval: Int,
    onToastReminderIntervalChange: (Int) -> Unit,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    showCounterCircle: Boolean = true,
    onShowCounterCircleChange: (Boolean) -> Unit = {},
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        LanguageSettings(
            currentLanguage = currentLanguage,
            onLanguageChange = onLanguageChange
        )

        AppearanceSettings(
            currentTheme = currentTheme,
            onThemeChange = onThemeChange,
            currentColorTheme = currentColorTheme,
            onColorThemeChange = onColorThemeChange,
            showCounterCircle = showCounterCircle,
            onShowCounterCircleChange = onShowCounterCircleChange
        )

        SoundSettings(
            isSoundEnabled = isSoundEnabled,
            onSoundEnabledChange = onSoundEnabledChange,
            isVibrateTapEnabled = isVibrateTapEnabled,
            onVibrateTapChange = onVibrateTapChange,
            isVibrate100Enabled = isVibrate100Enabled,
            onVibrate100Change = onVibrate100Change
        )

        ToastReminderSettings(
            isEnabled = isToastReminderEnabled,
            onEnabledChange = onToastReminderEnabledChange,
            text = toastReminderText,
            onTextChange = onToastReminderTextChange,
            interval = toastReminderInterval,
            onIntervalChange = onToastReminderIntervalChange
        )

        DataSettings(
            onBackupClick = onBackupClick,
            onRestoreClick = onRestoreClick
        )

        CloudSyncSettings(
            isLoggedIn = isLoggedIn,
            isRestoring = isRestoring,
            userEmail = userEmail,
            userDisplayName = userDisplayName,
            userPhotoUrl = userPhotoUrl,
            onLoginClick = onLoginClick,
            onLogoutClick = onLogoutClick
        )

        LeaderboardSettings(
            isEnabled = isLeaderboardEnabled,
            onEnabledChange = onLeaderboardEnabledChange
        )

        AboutSettings()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
