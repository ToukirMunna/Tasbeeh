package com.toukir.tasbeeh.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.AppColorTheme
import com.toukir.tasbeeh.ui.theme.AppTheme
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun AppearanceSettings(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    currentColorTheme: AppColorTheme,
    onColorThemeChange: (AppColorTheme) -> Unit,
    showCounterCircle: Boolean,
    onShowCounterCircleChange: (Boolean) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.group_appearance)) {
        SettingsItem(
            title = stringResource(R.string.setting_theme),
            icon = StudioIcons.AutoAwesome
        ) {
            ThemeSelectorRow(
                currentTheme = currentTheme,
                onThemeChange = onThemeChange
            )
        }
        
        SettingsDivider()

        SettingsItem(
            title = stringResource(R.string.setting_accent_color),
            icon = StudioIcons.AutoAwesome
        ) {
            ColorThemeSelectorRow(
                currentColorTheme = currentColorTheme,
                onColorThemeChange = onColorThemeChange
            )
        }
        
        SettingsDivider()
        
        SettingsSwitchItem(
            title = stringResource(R.string.setting_show_circle),
            subtitle = stringResource(R.string.setting_show_circle_desc),
            icon = StudioIcons.CheckCircle,
            checked = showCounterCircle,
            onCheckedChange = onShowCounterCircleChange
        )
    }
}

@Composable
private fun ThemeSelectorRow(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTheme.entries.forEach { theme ->
            val label = if (theme == AppTheme.Light) stringResource(R.string.theme_light) else stringResource(R.string.theme_dark)
            ThemeChip(
                selected = theme == currentTheme,
                label = label,
                onClick = { onThemeChange(theme) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ColorThemeSelectorRow(
    currentColorTheme: AppColorTheme,
    onColorThemeChange: (AppColorTheme) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppColorTheme.entries.forEach { colorOption ->
            ColorSwatch(
                color = colorOption.previewColor,
                onColor = colorOption.onPreviewColor,
                label = colorOption.displayName,
                isSelected = colorOption == currentColorTheme,
                onClick = { onColorThemeChange(colorOption) }
            )
        }
    }
}

@Composable
fun ColorSwatch(
    color: Color,
    onColor: Color,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                StudioIcon(
                    StudioIcons.Check,
                    contentDescription = null,
                    tint = onColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ThemeChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.height(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
