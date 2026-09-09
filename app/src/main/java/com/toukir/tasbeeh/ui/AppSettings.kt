package com.toukir.tasbeeh.ui

import com.toukir.tasbeeh.ui.theme.AppColorTheme
import com.toukir.tasbeeh.ui.theme.AppTheme
import com.toukir.tasbeeh.ui.theme.GradientStyle

enum class SyncStatus { IDLE, SYNCING, SYNCED, ERROR }

data class AppSettings(
    val theme: AppTheme = AppTheme.Light,
    val colorTheme: AppColorTheme = AppColorTheme.Gold,
    val gradient: GradientStyle = GradientStyle.Sunset,
    val thickness: Float = 20f,
    val isSoundEnabled: Boolean = true,
    val isVibrateTapEnabled: Boolean = false,
    val isVibrate100Enabled: Boolean = true,
    val language: String = "en",
    val showCounterCircle: Boolean = true
)
