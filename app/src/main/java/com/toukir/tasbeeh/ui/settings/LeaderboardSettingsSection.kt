package com.toukir.tasbeeh.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun LeaderboardSettings(
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.leaderboard_title)) {
        SettingsSwitchItem(
            title = stringResource(R.string.leaderboard_toggle_label),
            subtitle = if (isEnabled) stringResource(R.string.leaderboard_status_active) else stringResource(R.string.leaderboard_status_inactive),
            icon = StudioIcons.Leaderboard,
            checked = isEnabled,
            onCheckedChange = onEnabledChange
        )
    }
}
