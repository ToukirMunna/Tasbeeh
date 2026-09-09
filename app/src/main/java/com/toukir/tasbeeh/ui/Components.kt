package com.toukir.tasbeeh.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.R
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun Modifier.pressClickEffect() = this.composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "PressEffect"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                isPressed = true
                tryAwaitRelease()
                isPressed = false
            }
        )
    }
}

@Composable
fun Modifier.cascadeItemAnimation(index: Int) = this.composed {
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        launch {
            kotlinx.coroutines.delay(index * 50L)
            alpha.animateTo(1f, animationSpec = tween(400))
        }
        launch {
            kotlinx.coroutines.delay(index * 50L)
            translateY.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
        }
    }

    this.graphicsLayer {
        this.alpha = alpha.value
        this.translationY = translateY.value
    }
}

@Composable
fun RedesignedCircularProgress(
    progress: Float,
    currentCount: String,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val strokeWidth = 14.dp
    val primaryGradient = Brush.linearGradient(
        colors = listOf(colorScheme.primary, colorScheme.primaryContainer)
    )
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "CircularProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressTrackAndGlow(
            strokeWidth = strokeWidth,
            trackColor = colorScheme.surfaceVariant,
            primaryGradient = primaryGradient,
            progress = animatedProgress
        )
        CircularProgressCenterCount(currentCount = currentCount)
    }
}

@Composable
private fun CircularProgressTrackAndGlow(
    strokeWidth: Dp,
    trackColor: androidx.compose.ui.graphics.Color,
    primaryGradient: Brush,
    progress: Float
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .shadow(8.dp, CircleShape, spotColor = colorScheme.primary.copy(alpha = 0.2f))
            .border(1.dp, colorScheme.outlineVariant, CircleShape)
            .background(colorScheme.surface, CircleShape)
    )

    Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2 + 10.dp)) {
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            brush = primaryGradient,
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun CircularProgressCenterCount(currentCount: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = currentCount,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = if (currentCount.length > 5) 
                    MaterialTheme.typography.displayMedium.fontSize * 0.7f 
                else 
                    MaterialTheme.typography.displayMedium.fontSize
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun ThemedCircularProgress(
    progress: Float,
    size: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val backgroundGray = colorScheme.onSurface.copy(alpha = 0.05f)
    val strokeWidth = 12.dp

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ), 
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
                // Background circle
                drawArc(
                    color = backgroundGray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                // Foreground arc (Themed Primary Color)
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        // Inner Content
        content()
    }
}
