package com.toukir.tasbeeh.ui

import android.app.Activity
import android.media.AudioAttributes
import android.content.Context
import android.content.ContextWrapper
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Sparkle(
    val id: Long,
    val offset: Offset,
    val alpha: Animatable<Float, *>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CounterScreen(
    goal: TasbeehGoal,
    onBack: () -> Unit,
    onIncrement: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSoundEnabled: Boolean,
    isVibrateTapEnabled: Boolean,
    isVibrate100Enabled: Boolean,
    language: String = "en"
) {
    var isDiscreetMode by remember { mutableStateOf(false) }
    var showMeaning by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val adhkarInfo = remember(goal.name) { 
        AdhkarLibrary.getLocalizedInfo(context, goal.name) ?: AdhkarLibrary.getInfo(goal.name) 
    }
    
    val displayGoalName = remember(goal.name) {
        AdhkarLibrary.getLocalizedName(context, goal.name)
    }

    val view = LocalView.current
    DisposableEffect(isDiscreetMode) {
        val activity = generateSequence(context) { (it as? ContextWrapper)?.baseContext }
            .firstOrNull { it is Activity } as? Activity
        val window = activity?.window
        val insetsController = if (window != null) WindowCompat.getInsetsController(window, view) else null

        if (isDiscreetMode) {
            insetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController?.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            if (isDiscreetMode) {
                insetsController?.show(WindowInsetsCompat.Type.systemBars())
                insetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(attributes)
            .build()
    }
    var soundId by remember { mutableIntStateOf(0) }
    var isSoundLoaded by remember { mutableStateOf(false) }
    DisposableEffect(soundPool) {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (sampleId == soundId && status == 0) {
                isSoundLoaded = true
            }
        }
        soundId = soundPool.load(context, R.raw.tap, 1)
        onDispose {
            soundPool.release()
        }
    }
    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    val sparkles = remember { mutableStateListOf<Sparkle>() }

    fun handleIncrement(offset: Offset? = null) {
        if (isSoundEnabled && isSoundLoaded) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
        
        val nextCount = goal.currentCount + 1
        
        if (isVibrate100Enabled && nextCount > 0 && nextCount % 100 == 0) {
             vibrateStrong(vibrator)
        } else if (isVibrateTapEnabled) {
             vibrateTick(vibrator)
        }
        
        if (isDiscreetMode && offset != null) {
            val sparkleId = System.currentTimeMillis()
            val sparkle = Sparkle(sparkleId, offset, Animatable(0.4f))
            sparkles.add(sparkle)
        }
        
        onIncrement()
    }

    BackHandler {
        if (isDiscreetMode) {
            isDiscreetMode = false
        } else {
            onBack()
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier.fillMaxSize()) {
        if (isDiscreetMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                handleIncrement(offset)
                            }
                        )
                    }
            ) {
                // Use a copy for iteration to avoid ConcurrentModificationException
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
                            // Draw a faint sparkle effect: several small dots/lines
                            val random = Random(sparkle.id)
                            repeat(5) {
                                val angle = random.nextFloat() * 360f
                                val distance = random.nextFloat() * 20.dp.toPx()
                                val x = sparkle.offset.x + distance * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                                val y = sparkle.offset.y + distance * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                                val radius = random.nextFloat() * 2.dp.toPx()
                                
                                drawCircle(
                                    color = Color.White.copy(alpha = alpha),
                                    radius = radius,
                                    center = Offset(x, y)
                                )
                            }
                            // Also a small central cross or dot
                            drawCircle(
                                color = Color.White.copy(alpha = alpha),
                                radius = 3.dp.toPx(),
                                center = sparkle.offset,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CounterHeader(
                    goalName = displayGoalName,
                    onBack = onBack,
                    onDetailsClick = onDetailsClick,
                    onMeaningClick = { showMeaning = !showMeaning }
                )
                
                if (showMeaning && adhkarInfo != null) {
                    MeaningDisplay(translation = adhkarInfo.translation)
                }

                CounterCircle(
                    goal = goal,
                    onClick = { handleIncrement() },
                    interactionSource = interactionSource,
                    modifier = Modifier.weight(1f),
                    language = language
                )

                Surface(
                    onClick = { isDiscreetMode = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.height(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = stringResource(R.string.pure_black),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun vibrateStrong(vibrator: Vibrator) {
    // For milestones (every 100 counts), use a solid 400ms vibration at maximum intensity.
    // This is much stronger than a "click" and provides clear milestone feedback.
    vibrator.vibrate(VibrationEffect.createOneShot(400, 255))
}

private fun vibrateTick(vibrator: Vibrator) {
    // For every tap, use the lightest possible haptic feedback (TICK).
    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
}
