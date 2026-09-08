package com.toukir.tasbeeh.ui.theme

import androidx.compose.ui.graphics.Color

// Toukir Studio Unified Design System (TDS) Palette
// 90% Neutral Workhorses + 10% Toukir Mint Category Brand Signature

// Light Theme (Cool Slate & Toukir Mint)
val LightCanvas = Color(0xFFF0F2F5) // Cool Slate Canvas
val LightSurface = Color(0xFFFFFFFF) // Pure Luminous Card
val LightInset = Color(0xFFE4E8EE) // Recessed Slate (Tracks/Bars)
val LightHairlineRim = Color(0xFFD2D8E2) // 1dp Tactile Rim
val LightTextPrimary = Color(0xFF111827) // Deep Ink (16.5:1 AAA)
val LightTextSecondary = Color(0xFF4B5563) // Muted Slate (5.8:1 AA)

val ToukirMintLight = Color(0xFF0D9488) // Toukir Mint Primary Light
val ToukirMintOnPrimaryLight = Color(0xFFFFFFFF)
val ToukirMintContainerLight = Color(0xFFCCFBF1) // Pill Container Light
val ToukirMintOnContainerLight = Color(0xFF134E4A)

// Dark Theme (Deep Obsidian Slate & Glowing Mint)
val DarkCanvas = Color(0xFF111317) // Deep Obsidian Slate Canvas
val DarkSurface = Color(0xFF1A1D23) // Elevated Charcoal Card
val DarkInset = Color(0xFF232730) // Recessed Dark (Tracks/Bars)
val DarkHairlineRim = Color(0xFF2D323E) // 1dp Tactile Rim
val DarkTextPrimary = Color(0xFFF3F4F6) // Luminous White (16.5:1 AAA)
val DarkTextSecondary = Color(0xFF9CA3AF) // Muted Silver (5.8:1 AA)

val ToukirMintDark = Color(0xFF14B8A6) // Toukir Mint Primary Dark
val ToukirMintOnPrimaryDark = Color(0xFF111317)
val ToukirMintContainerDark = Color(0xFF134E4A) // Pill Container Dark
val ToukirMintOnContainerDark = Color(0xFFCCFBF1)

// Alert Exception: Toukir Crimson
val ToukirCrimsonLight = Color(0xFFDC2626)
val ToukirCrimsonContainerLight = Color(0xFFFEE2E2)
val ToukirCrimsonDark = Color(0xFFEF4444)
val ToukirCrimsonContainerDark = Color(0xFF450A0A)

// Backward-compatibility aliases if needed
val DayPrimary = ToukirMintLight
val DayBackground = LightCanvas
val DaySurface = LightSurface
val NightPrimary = ToukirMintDark
val NightBackground = DarkCanvas
val NightSurface = DarkSurface

