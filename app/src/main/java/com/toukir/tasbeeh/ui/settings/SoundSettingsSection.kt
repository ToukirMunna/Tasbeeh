package com.toukir.tasbeeh.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun SoundSettings(
    isSoundEnabled: Boolean,
    onSoundEnabledChange: (Boolean) -> Unit,
    isVibrateTapEnabled: Boolean,
    onVibrateTapChange: (Boolean) -> Unit,
    isVibrate100Enabled: Boolean,
    onVibrate100Change: (Boolean) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.group_sound)) {
        SettingsSwitchItem(
            title = stringResource(R.string.setting_tap_sound),
            icon = StudioIcons.VolumeUp,
            checked = isSoundEnabled,
            onCheckedChange = onSoundEnabledChange
        )
        
        SettingsDivider()

        SettingsSwitchItem(
            title = stringResource(R.string.setting_vibrate_tap),
            subtitle = stringResource(R.string.setting_vibrate_tap_desc),
            icon = StudioIcons.PlayArrow,
            checked = isVibrateTapEnabled,
            onCheckedChange = onVibrateTapChange
        )
        
        SettingsDivider()

        SettingsSwitchItem(
            title = stringResource(R.string.setting_vibrate_100),
            subtitle = stringResource(R.string.setting_vibrate_100_desc),
            icon = StudioIcons.FlashOn,
            checked = isVibrate100Enabled,
            onCheckedChange = onVibrate100Change
        )
    }
}
