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

// Dark Theme (Velvet Charcoal & Sacred Gold / Illuminated Amber)
val DarkCanvas = Color(0xFF0C0D0E) // Deep Velvet Black Canvas
val DarkSurface = Color(0xFF16181B) // Elevated Warm Charcoal Card
val DarkInset = Color(0xFF212328) // Recessed Charcoal (Tracks/Bars)
val DarkHairlineRim = Color(0xFF2E3137) // 1dp Tactile Rim
val DarkTextPrimary = Color(0xFFF4F4F5) // Luminous Warm White (16.5:1 AAA)
val DarkTextSecondary = Color(0xFFA1A1AA) // Muted Warm Silver (5.8:1 AA)

val ToukirAmberDark = Color(0xFFF59E0B) // Sacred Gold / Illuminated Amber Primary
val ToukirAmberOnPrimaryDark = Color(0xFF0C0D0E) // Deep Velvet Charcoal on Gold
val ToukirAmberContainerDark = Color(0xFF3B1F08) // Warm Chestnut Container Dark
val ToukirAmberOnContainerDark = Color(0xFFFEF3C7) // Illuminated Parchment Gold

// Alert Exception: Toukir Crimson
val ToukirCrimsonLight = Color(0xFFDC2626)
val ToukirCrimsonContainerLight = Color(0xFFFEE2E2)
val ToukirCrimsonDark = Color(0xFFEF4444)
val ToukirCrimsonContainerDark = Color(0xFF450A0A)

// Backward-compatibility aliases
val DayPrimary = ToukirMintLight
val DayBackground = LightCanvas
val DaySurface = LightSurface
val NightPrimary = ToukirAmberDark
val NightBackground = DarkCanvas
val NightSurface = DarkSurface
val ToukirMintDark = Color(0xFF14B8A6)

