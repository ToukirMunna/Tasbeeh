package com.toukir.tasbeeh.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.utils.formatNumber
import kotlin.math.cos
import kotlin.math.sin

/**
 * Creates a truly rounded polygon path.
 */
private fun createRoundedOctagonPath(size: androidx.compose.ui.geometry.Size, cornerRadius: Float): Path {
    val path = Path()
    val sides = 8
    val radius = minOf(size.width, size.height) / 2f
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val angleStep = (2 * Math.PI / sides).toFloat()
    val startAngle = (-Math.PI / 2).toFloat()

    // Points of the octagon
    val points = List(sides) { i ->
        val angle = startAngle + i * angleStep
        androidx.compose.ui.geometry.Offset(
            centerX + radius * cos(angle),
            centerY + radius * sin(angle)
        )
    }

    if (radius <= 0f) {
        path.addOval(androidx.compose.ui.geometry.Rect(androidx.compose.ui.geometry.Offset.Zero, size))
        return path
    }

    for (i in 0 until sides) {
        val p1 = points[i]
        val p2 = points[(i + 1) % sides]
        val p3 = points[(i + 2) % sides]

        // Vector p1 -> p2
        val dx1 = p2.x - p1.x
        val dy1 = p2.y - p1.y
        val len1 = kotlin.math.sqrt(dx1 * dx1 + dy1 * dy1)
        
        // Vector p2 -> p3
        val dx2 = p3.x - p2.x
        val dy2 = p3.y - p2.y
        val len2 = kotlin.math.sqrt(dx2 * dx2 + dy2 * dy2)

        if (len1 > 0 && len2 > 0) {
            // Points for the curve
            val startX = p2.x - (dx1 / len1) * cornerRadius
            val startY = p2.y - (dy1 / len1) * cornerRadius
            val endX = p2.x + (dx2 / len2) * cornerRadius
            val endY = p2.y + (dy2 / len2) * cornerRadius

            if (i == 0) {
                path.moveTo(startX, startY)
            } else {
                path.lineTo(startX, startY)
            }
            path.quadraticTo(p2.x, p2.y, endX, endY)
        }
    }
    path.close()
    return path
}

val SmoothOctagonShape = GenericShape { size, _ ->
    if (size.width > 0 && size.height > 0) {
        addPath(createRoundedOctagonPath(size, cornerRadius = size.width * 0.12f))
    }
}

@Composable
fun CounterHeader(
    goalName: String,
    onBack: () -> Unit,
    onDetailsClick: () -> Unit,
    onMeaningClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = goalName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMeaningClick) {
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = "Show Meaning",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onDetailsClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MeaningDisplay(
    translation: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = translation,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

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
            // 1. Soft Ambient Shadow
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

            // 2. The Base (Physical Side Walls) - TDS Inset Surface
            Box(
                modifier = Modifier
                    .size(size)
                    .offset(y = 12.dp)
                    .clip(SmoothOctagonShape)
                    .background(themeColor.surfaceVariant)
            )

            // 3. The smooth main key cap (TDS Surface with 1dp Hairline Rim)
            Box(
                modifier = Modifier
                    .size(size)
                    .offset(y = (12.dp - depthAnimation))
                    .scale(scaleAnimation)
                    .shadow(
                        elevation = depthAnimation / 2,
                        shape = SmoothOctagonShape,
                        clip = false
                    )
                    .background(
                        color = themeColor.surface,
                        shape = SmoothOctagonShape
                    )
                    .border(
                        width = 1.dp,
                        color = themeColor.outlineVariant,
                        shape = SmoothOctagonShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inset detail area for the number (TDS 1dp tactile inset)
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.88f)
                        .background(
                            color = themeColor.surfaceVariant.copy(alpha = 0.4f),
                            shape = SmoothOctagonShape
                        )
                        .border(
                            width = 1.dp,
                            color = themeColor.outlineVariant.copy(alpha = 0.6f),
                            shape = SmoothOctagonShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val countText = formatNumber(goal.currentCount, language)
                    val baseFontSize = (size.value * 0.28)
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
        }
    }
}
