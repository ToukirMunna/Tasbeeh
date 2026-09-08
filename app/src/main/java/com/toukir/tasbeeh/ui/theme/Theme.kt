package com.toukir.tasbeeh.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 1. Toukir Studio Light Scheme (Cool Slate & Toukir Mint)
private val DayColorScheme = lightColorScheme(
    primary = ToukirMintLight,
    onPrimary = ToukirMintOnPrimaryLight,
    primaryContainer = ToukirMintContainerLight,
    onPrimaryContainer = ToukirMintOnContainerLight,
    background = LightCanvas,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightInset,
    onSurfaceVariant = LightTextSecondary,
    outline = LightHairlineRim,
    outlineVariant = LightHairlineRim,
    error = ToukirCrimsonLight,
    errorContainer = ToukirCrimsonContainerLight
)

// 2. Toukir Studio Dark Scheme (Velvet Charcoal & Sacred Gold)
private val NightColorScheme = darkColorScheme(
    primary = ToukirAmberDark,
    onPrimary = ToukirAmberOnPrimaryDark,
    primaryContainer = ToukirAmberContainerDark,
    onPrimaryContainer = ToukirAmberOnContainerDark,
    background = DarkCanvas,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkInset,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkHairlineRim,
    outlineVariant = DarkHairlineRim,
    error = ToukirCrimsonDark,
    errorContainer = ToukirCrimsonContainerDark
)

enum class AppTheme {
    Light,
    Dark
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
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
