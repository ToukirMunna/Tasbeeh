package com.toukir.tasbeeh.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 1. Day (Light) Scheme
private val DayColorScheme = lightColorScheme(
    primary = DayPrimary,
    onPrimary = DayOnPrimary,
    primaryContainer = DayPrimaryContainer,
    onPrimaryContainer = DayOnPrimaryContainer,
    background = DayBackground,
    onBackground = DayOnBackground,
    surface = DaySurface,
    onSurface = DayOnSurface,
    surfaceVariant = DaySurfaceVariant,
    onSurfaceVariant = DayOnSurfaceVariant
)

// 2. Night (Dark) Scheme
private val NightColorScheme = darkColorScheme(
    primary = NightPrimary,
    onPrimary = NightOnPrimary,
    primaryContainer = NightPrimaryContainer,
    onPrimaryContainer = NightOnPrimaryContainer,
    background = NightBackground,
    onBackground = NightOnBackground,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightOnSurfaceVariant
)

// 3. Sepia Scheme (Warm)
private val SepiaColorScheme = lightColorScheme(
    primary = SepiaPrimary,
    onPrimary = SepiaOnPrimary,
    primaryContainer = SepiaPrimaryContainer,
    onPrimaryContainer = SepiaOnPrimaryContainer,
    background = SepiaBackground,
    onBackground = SepiaOnBackground,
    surface = SepiaSurface,
    onSurface = SepiaOnSurface,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaOnSurfaceVariant
)

enum class AppTheme {
    Light,
    Dark,
    Sepia
}

enum class GradientStyle(val colors: List<Color>, val nameStr: String) {
    Sunset(listOf(Color(0xFFF97794), Color(0xFFF9A880)), "Sunset"),
    Ocean(listOf(Color(0xFF2E3192), Color(0xFF1BFFFF)), "Ocean"),
    Nature(listOf(Color(0xFFD4FC79), Color(0xFF96E6A1)), "Nature"),
    Purple(listOf(Color(0xFFC33764), Color(0xFF1D2671)), "Purple"),
    MidnightNeon(listOf(Color(0xFF00F2FE), Color(0xFF4FACFE)), "Midnight Neon"),
    SoftRose(listOf(Color(0xFFFFA17F), Color(0xFF00223E)), "Soft Rose"),
    EmeraldForest(listOf(Color(0xFF00B09B), Color(0xFF96C93D)), "Emerald Forest");

    fun getBrush(): Brush {
        return Brush.linearGradient(colors = colors)
    }
}

@Composable
fun TasbeehTheme(
    theme: AppTheme = AppTheme.Light,
    typography: Typography = Typography,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.Light -> DayColorScheme
        AppTheme.Dark -> NightColorScheme
        AppTheme.Sepia -> SepiaColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
