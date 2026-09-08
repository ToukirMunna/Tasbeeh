package com.toukir.tasbeeh.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.R

private val LightBarColor = Color(0xFF111827) // TDS Light Deep Ink
private val DarkBarColor = Color(0xFFF3F4F6) // TDS Dark Luminous White
private val DiamondTopColor = Color(0xFF14B8A6) // Toukir Mint
private val DiamondBottomColor = Color(0xFF0D9488) // Toukir Mint Base
private val DiamondGlowColor = Color(0x4014B8A6) // Toukir Mint Radiant Glow

@Composable
fun SplashScreen(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SplashTasbeehLogo(
            isDark = isDark,
            modifier = Modifier
                .size(288.dp)
                .align(Alignment.Center)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 110.dp)
        )
    }
}

@Composable
private fun SplashTasbeehLogo(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "TasbeehSplashAnim")

    // Gentle breathing pulse starting at 1.0f for instant 0ms pixel lock
    val pulse by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "JewelPulse"
    )

    // Radiant breathing glow alpha
    val glowPulse by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    val barColor = if (isDark) DarkBarColor else LightBarColor

    Canvas(modifier = modifier) {
        drawCalligraphyPillars(barColor = barColor)
        drawDiamondJewel(
            pulse = pulse,
            glowAlpha = glowPulse,
            isDark = isDark
        )
    }
}

private fun DrawScope.drawCalligraphyPillars(barColor: Color) {
    val strokeWidth = 32.dp.toPx()
    val stroke = Stroke(
        width = strokeWidth,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
    )

    // 1. U-Shape: Ha and Lam 2 Connected Base
    val uPath = Path().apply {
        moveTo(81.33f.dp.toPx(), 157.33f.dp.toPx())
        lineTo(81.33f.dp.toPx(), 189.33f.dp.toPx())
        arcTo(
            rect = Rect(
                left = 81.33f.dp.toPx(),
                top = 168.0f.dp.toPx(),
                right = 124.0f.dp.toPx(),
                bottom = 210.67f.dp.toPx()
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
        lineTo(124.0f.dp.toPx(), 136.0f.dp.toPx())
    }
    drawPath(path = uPath, color = barColor, style = stroke)

    // 2. Lam 1: Middle Pillar
    drawLine(
        color = barColor,
        start = Offset(166.7f.dp.toPx(), 128.0f.dp.toPx()),
        end = Offset(166.7f.dp.toPx(), 210.7f.dp.toPx()),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    // 3. Alif: Rightmost Tall Pillar
    drawLine(
        color = barColor,
        start = Offset(209.3f.dp.toPx(), 98.7f.dp.toPx()),
        end = Offset(209.3f.dp.toPx(), 210.7f.dp.toPx()),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawDiamondJewel(
    pulse: Float,
    glowAlpha: Float,
    isDark: Boolean
) {
    val center = Offset(148.4f.dp.toPx(), 85.1f.dp.toPx())
    val baseSide = 36.dp.toPx()
    val baseRadius = 7.5f.dp.toPx()

    val side = baseSide * pulse
    val cornerRadius = CornerRadius(baseRadius * pulse, baseRadius * pulse)

    val topLeft = Offset(center.x - side / 2f, center.y - side / 2f)
    val size = Size(side, side)

    // Sacred ambient glow aura
    if (isDark) {
        val glowSide = side * 1.55f
        val glowRadius = CornerRadius(baseRadius * 1.55f, baseRadius * 1.55f)
        val glowTopLeft = Offset(center.x - glowSide / 2f, center.y - glowSide / 2f)
        rotate(degrees = 45f, pivot = center) {
            drawRoundRect(
                color = DiamondGlowColor.copy(alpha = DiamondGlowColor.alpha * glowAlpha),
                topLeft = glowTopLeft,
                size = Size(glowSide, glowSide),
                cornerRadius = glowRadius
            )
        }
    }

    // Radiant amber-coral prayer jewel
    rotate(degrees = 45f, pivot = center) {
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(DiamondTopColor, DiamondBottomColor),
                start = topLeft,
                end = Offset(topLeft.x + size.width, topLeft.y + size.height)
            ),
            topLeft = topLeft,
            size = size,
            cornerRadius = cornerRadius
        )
    }
}
