package com.toukir.tasbeeh.ui

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class Sparkle(
    val id: Long,
    val offset: Offset,
    val alpha: Animatable<Float, *>
)

@Composable
fun CounterOledBlackoutView(
    sparkles: MutableList<Sparkle>,
    onTap: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onTap = onTap)
            }
    ) {
        val sparklesCopy = sparkles.toList()
        sparklesCopy.forEach { sparkle ->
            LaunchedEffect(sparkle.id) {
                sparkle.alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 600, easing = LinearEasing)
                )
                sparkles.remove(sparkle)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            sparkles.forEach { sparkle ->
                val alpha = sparkle.alpha.value
                if (alpha > 0) {
                    val random = Random(sparkle.id)
                    repeat(5) {
                        val angle = random.nextFloat() * 360f
                        val distance = random.nextFloat() * 20.dp.toPx()
                        val x = sparkle.offset.x + distance * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                        val y = sparkle.offset.y + distance * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                        val radius = random.nextFloat() * 2.dp.toPx()
                        drawCircle(color = Color.White.copy(alpha = alpha), radius = radius, center = Offset(x, y))
                    }
                    drawCircle(color = Color.White.copy(alpha = alpha), radius = 3.dp.toPx(), center = sparkle.offset, style = Stroke(width = 1.dp.toPx()))
                }
            }
        }
    }
}

fun vibrateStrong(vibrator: Vibrator) {
    vibrator.vibrate(VibrationEffect.createOneShot(400, 255))
}

fun vibrateTick(vibrator: Vibrator) {
    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
}
