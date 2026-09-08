package com.toukir.tasbeeh.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.theme.AppTheme

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
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

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
            letterSpacing = 1.sp
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp),
                content = content
            )
        }
    }
}

@Composable
fun LanguageSettings(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.group_language)) {
        SettingsItem(
            title = stringResource(R.string.setting_language),
            icon = Icons.Outlined.Translate
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageChip(
                    selected = currentLanguage == "en",
                    label = "English",
                    onClick = { onLanguageChange("en") },
                    modifier = Modifier.weight(1f)
                )
                LanguageChip(
                    selected = currentLanguage == "bn",
                    label = "বাংলা",
                    onClick = { onLanguageChange("bn") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LanguageChip(
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
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AppearanceSettings(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    showCounterCircle: Boolean,
    onShowCounterCircleChange: (Boolean) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.group_appearance)) {
        SettingsItem(
            title = stringResource(R.string.setting_theme),
            icon = Icons.Outlined.Palette
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTheme.entries.forEach { theme ->
                    ThemeChip(
                        selected = theme == currentTheme,
                        label = if (theme == AppTheme.Light) "Light (Slate)" else "Dark (Obsidian)",
                        onClick = { onThemeChange(theme) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        SettingsDivider()
        
        SettingsSwitchItem(
            title = stringResource(R.string.setting_show_circle),
            subtitle = stringResource(R.string.setting_show_circle_desc),
            icon = Icons.Outlined.RadioButtonChecked,
            checked = showCounterCircle,
            onCheckedChange = onShowCounterCircleChange
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
            icon = Icons.AutoMirrored.Outlined.VolumeUp,
            checked = isSoundEnabled,
            onCheckedChange = onSoundEnabledChange
        )
        
        SettingsDivider()

        SettingsSwitchItem(
            title = stringResource(R.string.setting_vibrate_tap),
            subtitle = stringResource(R.string.setting_vibrate_tap_desc),
            icon = Icons.Outlined.TouchApp,
            checked = isVibrateTapEnabled,
            onCheckedChange = onVibrateTapChange
        )
        
        SettingsDivider()

        SettingsSwitchItem(
            title = stringResource(R.string.setting_vibrate_100),
            subtitle = stringResource(R.string.setting_vibrate_100_desc),
            icon = Icons.Outlined.Vibration,
            checked = isVibrate100Enabled,
            onCheckedChange = onVibrate100Change
        )
    }
}

@Composable
fun ToastReminderSettings(
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    text: String,
    onTextChange: (String) -> Unit,
    interval: Int,
    onIntervalChange: (Int) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.group_toast)) {
        SettingsSwitchItem(
            title = stringResource(R.string.setting_toast_enable),
            subtitle = stringResource(R.string.setting_toast_desc),
            icon = Icons.Outlined.NotificationsActive,
            checked = isEnabled,
            onCheckedChange = onEnabledChange
        )

        AnimatedVisibility(
            visible = isEnabled,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsDivider(modifier = Modifier.padding(bottom = 16.dp))
                
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text(stringResource(R.string.setting_toast_text)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Message, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.setting_toast_interval),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    OutlinedTextField(
                        value = if (interval == 0) "" else interval.toString(),
                        onValueChange = { 
                            if (it.isEmpty()) onIntervalChange(0)
                            else it.toIntOrNull()?.let { valInt -> onIntervalChange(valInt) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DataSettings(
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    SettingsGroup(title = stringResource(R.string.group_data)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.setting_backup_restore),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(
                    onClick = onBackupClick,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_backup))
                }
                OutlinedButton(
                    onClick = onRestoreClick,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_restore))
                }
            }
        }
    }
}

@Composable
fun CloudSyncSettings(
    isLoggedIn: Boolean,
    isRestoring: Boolean = false,
    userEmail: String? = null,
    userDisplayName: String? = null,
    userPhotoUrl: String? = null,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    SettingsGroup(title = "Cloud Sync") {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!isLoggedIn) {
                Text(
                    text = "Login with Google to sync your data automatically across devices.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.Cloud, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Login with Google")
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        if (userPhotoUrl != null) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userDisplayName ?: "User",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userEmail ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isRestoring) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Restoring data from cloud...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Sync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Syncing automatically when you close the app.",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.logout_btn), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun LeaderboardSettings(
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.leaderboard_title)) {
        SettingsSwitchItem(
            title = stringResource(R.string.leaderboard_toggle_label),
            subtitle = if (isEnabled) "Active" else "Inactive",
            icon = Icons.Outlined.Leaderboard,
            checked = isEnabled,
            onCheckedChange = onEnabledChange
        )
    }
}

@Composable
fun AboutSettings() {
    SettingsGroup(title = stringResource(R.string.group_about)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Toukir Studio Monogram & App Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "TS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Toukir Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tasbeeh • v4.1.0 (TDS Build)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Privacy Pledge Card (TDS Universal Standard)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Zero Ads. Zero Telemetry. Crafted by Toukir Studio.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.about_text),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Studio Fleet Siblings
            Text(
                text = "TOUKIR STUDIO APPS FLEET",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val studioSiblings = listOf("PhoneUsage", "LexiCore", "Audia", "DrinkWater")
                studioSiblings.forEach { appName ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = appName,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        ListItem(
            headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
            leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
        content()
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
