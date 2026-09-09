package com.toukir.tasbeeh.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal

@Composable
fun DiscreetSystemBarsEffect(isDiscreetMode: Boolean, context: Context, view: View) {
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
}

class CounterFeedbackState(
    private val soundPool: SoundPool,
    private val soundId: Int,
    private val isSoundLoaded: () -> Boolean,
    private val vibrator: Vibrator,
    private val isSoundEnabled: Boolean,
    private val isVibrateTapEnabled: Boolean,
    private val isVibrate100Enabled: Boolean,
    private val onIncrement: () -> Unit,
    private val sparkles: SnapshotStateList<Sparkle>
) {
    fun increment(goal: TasbeehGoal, isDiscreetMode: Boolean, offset: Offset? = null) {
        if (isSoundEnabled && isSoundLoaded()) soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        val nextCount = goal.currentCount + 1
        if (isVibrate100Enabled && nextCount > 0 && nextCount % 100 == 0) {
            vibrateStrong(vibrator)
        } else if (isVibrateTapEnabled) {
            vibrateTick(vibrator)
        }
        if (isDiscreetMode && offset != null) {
            sparkles.add(Sparkle(System.currentTimeMillis(), offset, Animatable(0.4f)))
        }
        onIncrement()
    }
}

@Composable
fun rememberCounterFeedback(
    context: Context,
    isSoundEnabled: Boolean,
    isVibrateTapEnabled: Boolean,
    isVibrate100Enabled: Boolean,
    sparkles: SnapshotStateList<Sparkle>,
    onIncrement: () -> Unit
): CounterFeedbackState {
    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attributes).build()
    }
    var soundId by remember { mutableIntStateOf(0) }
    var isSoundLoaded by remember { mutableStateOf(false) }
    DisposableEffect(soundPool) {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (sampleId == soundId && status == 0) isSoundLoaded = true
        }
        soundId = soundPool.load(context, R.raw.tap, 1)
        onDispose { soundPool.release() }
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

    return remember(isSoundEnabled, isVibrateTapEnabled, isVibrate100Enabled, isSoundLoaded, soundId) {
        CounterFeedbackState(
            soundPool = soundPool,
            soundId = soundId,
            isSoundLoaded = { isSoundLoaded },
            vibrator = vibrator,
            isSoundEnabled = isSoundEnabled,
            isVibrateTapEnabled = isVibrateTapEnabled,
            isVibrate100Enabled = isVibrate100Enabled,
            onIncrement = onIncrement,
            sparkles = sparkles
        )
    }
}
