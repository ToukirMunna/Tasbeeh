package com.toukir.tasbeeh.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AppColorTheme(
    val displayName: String,
    val primaryLight: Color,
    val onPrimaryLight: Color,
    val containerLight: Color,
    val onContainerLight: Color,
    val primaryDark: Color,
    val onPrimaryDark: Color,
    val containerDark: Color,
    val onContainerDark: Color,
    val previewColor: Color,
    val onPreviewColor: Color
) {
    Gold(
        displayName = "Gold",
        primaryLight = Color(0xFF92400E),
        onPrimaryLight = Color(0xFFFFFFFF),
        containerLight = Color(0xFFFEF3C7),
        onContainerLight = Color(0xFF78350F),
        primaryDark = Color(0xFFF59E0B),
        onPrimaryDark = Color(0xFF0C0D0E),
        containerDark = Color(0xFF3B1F08),
        onContainerDark = Color(0xFFFEF3C7),
        previewColor = Color(0xFFF59E0B),
        onPreviewColor = Color(0xFF0C0D0E)
    ),
    Mint(
        displayName = "Mint",
        primaryLight = Color(0xFF0F766E),
        onPrimaryLight = Color(0xFFFFFFFF),
        containerLight = Color(0xFFCCFBF1),
        onContainerLight = Color(0xFF134E4A),
        primaryDark = Color(0xFF14B8A6),
        onPrimaryDark = Color(0xFF0C0D0E),
        containerDark = Color(0xFF134E4A),
        onContainerDark = Color(0xFFCCFBF1),
        previewColor = Color(0xFF14B8A6),
        onPreviewColor = Color(0xFF0C0D0E)
    ),
    Sapphire(
        displayName = "Sapphire",
        primaryLight = Color(0xFF2563EB),
        onPrimaryLight = Color(0xFFFFFFFF),
        containerLight = Color(0xFFDBEAFE),
        onContainerLight = Color(0xFF1E3A8A),
        primaryDark = Color(0xFF3B82F6),
        onPrimaryDark = Color(0xFF0C0D0E),
        containerDark = Color(0xFF172554),
        onContainerDark = Color(0xFFDBEAFE),
        previewColor = Color(0xFF3B82F6),
        onPreviewColor = Color(0xFF0C0D0E)
    ),
    Amethyst(
        displayName = "Amethyst",
        primaryLight = Color(0xFF7C3AED),
        onPrimaryLight = Color(0xFFFFFFFF),
        containerLight = Color(0xFFF3E8FF),
        onContainerLight = Color(0xFF4C1D95),
        primaryDark = Color(0xFFA855F7),
        onPrimaryDark = Color(0xFF0C0D0E),
        containerDark = Color(0xFF3B0764),
        onContainerDark = Color(0xFFF3E8FF),
        previewColor = Color(0xFFA855F7),
        onPreviewColor = Color(0xFF0C0D0E)
    ),
    Rose(
        displayName = "Rose",
        primaryLight = Color(0xFFBE123C),
        onPrimaryLight = Color(0xFFFFFFFF),
        containerLight = Color(0xFFFFE4E6),
        onContainerLight = Color(0xFF881337),
        primaryDark = Color(0xFFFB7185),
        onPrimaryDark = Color(0xFF0C0D0E),
        containerDark = Color(0xFF4C0519),
        onContainerDark = Color(0xFFFFE4E6),
        previewColor = Color(0xFFFB7185),
        onPreviewColor = Color(0xFF0C0D0E)
    )
}

// Dynamic Scheme Generators
private fun getDayColorScheme(colorTheme: AppColorTheme) = lightColorScheme(
    primary = colorTheme.primaryLight,
    onPrimary = colorTheme.onPrimaryLight,
    primaryContainer = colorTheme.containerLight,
    onPrimaryContainer = colorTheme.onContainerLight,
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

private fun getNightColorScheme(colorTheme: AppColorTheme) = darkColorScheme(
    primary = colorTheme.primaryDark,
    onPrimary = colorTheme.onPrimaryDark,
    primaryContainer = colorTheme.containerDark,
    onPrimaryContainer = colorTheme.onContainerDark,
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
    colorTheme: AppColorTheme = AppColorTheme.Gold,
    typography: Typography = Typography,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.Light -> getDayColorScheme(colorTheme)
        AppTheme.Dark -> getNightColorScheme(colorTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
