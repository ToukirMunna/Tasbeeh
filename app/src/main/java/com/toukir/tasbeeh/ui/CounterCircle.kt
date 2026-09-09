package com.toukir.tasbeeh.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.utils.formatNumber

@Composable
fun CounterCircle(
    goal: TasbeehGoal,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val depthAnimation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 12.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "ButtonDepth"
    )
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "ButtonScale"
    )

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        val size = if (maxWidth < maxHeight) maxWidth * 0.72f else maxHeight * 0.72f
        val themeColor = MaterialTheme.colorScheme

        Box(
            modifier = Modifier.size(size + 40.dp),
            contentAlignment = Alignment.Center
        ) {
            CounterCircleBase(size = size, isPressed = isPressed, themeColor = themeColor)
            CounterKeyCap(
                size = size,
                depthAnimation = depthAnimation,
                scaleAnimation = scaleAnimation,
                themeColor = themeColor,
                interactionSource = interactionSource,
                onClick = onClick
            ) {
                CounterDigitsDisplay(
                    size = size,
                    goal = goal,
                    language = language,
                    isPressed = isPressed,
                    themeColor = themeColor
                )
            }
        }
    }
}

@Composable
private fun CounterCircleBase(size: Dp, isPressed: Boolean, themeColor: ColorScheme) {
    Box(
        modifier = Modifier
            .size(size)
            .offset(y = 12.dp)
            .shadow(
                elevation = if (isPressed) 4.dp else 16.dp,
                shape = SmoothOctagonShape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
    )
    Box(
        modifier = Modifier
            .size(size)
            .offset(y = 12.dp)
            .clip(SmoothOctagonShape)
            .background(themeColor.surfaceVariant)
    )
}

@Composable
private fun CounterKeyCap(
    size: Dp,
    depthAnimation: Dp,
    scaleAnimation: Float,
    themeColor: ColorScheme,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .offset(y = 12.dp - depthAnimation)
            .scale(scaleAnimation)
            .shadow(elevation = depthAnimation / 2, shape = SmoothOctagonShape, clip = false)
            .background(color = themeColor.surface, shape = SmoothOctagonShape)
            .border(width = 1.dp, color = themeColor.outlineVariant, shape = SmoothOctagonShape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun CounterDigitsDisplay(
    size: Dp,
    goal: TasbeehGoal,
    language: String,
    isPressed: Boolean,
    themeColor: ColorScheme
) {
    Box(
        modifier = Modifier
            .fillMaxSize(0.88f)
            .background(color = themeColor.surfaceVariant.copy(alpha = 0.4f), shape = SmoothOctagonShape)
            .border(width = 1.dp, color = themeColor.outlineVariant.copy(alpha = 0.6f), shape = SmoothOctagonShape),
        contentAlignment = Alignment.Center
    ) {
        val countText = formatNumber(goal.currentCount, language)
        val baseFontSize = size.value * 0.28
        val adjustedFontSize = when {
            countText.length > 6 -> baseFontSize * 0.5
            countText.length > 4 -> baseFontSize * 0.7
            else -> baseFontSize
        }.sp

        Text(
            text = countText,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = adjustedFontSize,
                letterSpacing = if (language == "bn") 0.sp else (-1).sp,
                lineHeight = adjustedFontSize
            ),
            fontWeight = FontWeight.Bold,
            color = if (isPressed) themeColor.primary else themeColor.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false
        )
    }
}
