package com.toukir.tasbeeh.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary

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
    val view = LocalView.current
    val sparkles = remember { mutableStateListOf<Sparkle>() }

    val adhkarInfo = remember(goal.name, language, context) {
        AdhkarLibrary.getLocalizedInfo(context, goal.name) ?: AdhkarLibrary.getInfo(goal.name)
    }
    val displayGoalName = remember(goal.name, language, context) {
        AdhkarLibrary.getLocalizedName(context, goal.name)
    }

    DiscreetSystemBarsEffect(isDiscreetMode = isDiscreetMode, context = context, view = view)

    val feedback = rememberCounterFeedback(
        context = context,
        isSoundEnabled = isSoundEnabled,
        isVibrateTapEnabled = isVibrateTapEnabled,
        isVibrate100Enabled = isVibrate100Enabled,
        sparkles = sparkles,
        onIncrement = onIncrement
    )

    BackHandler {
        if (isDiscreetMode) isDiscreetMode = false else onBack()
    }

    val interactionSource = remember { MutableInteractionSource() }

    CounterScreenContent(
        modifier = modifier,
        isDiscreetMode = isDiscreetMode,
        sparkles = sparkles,
        feedback = feedback,
        goal = goal,
        displayGoalName = displayGoalName,
        showMeaning = showMeaning,
        translation = adhkarInfo?.translation,
        language = language,
        interactionSource = interactionSource,
        onBack = onBack,
        onDetailsClick = onDetailsClick,
        onMeaningToggle = { showMeaning = !showMeaning },
        onEnterDiscreet = { isDiscreetMode = true }
    )
}

@Composable
private fun CounterScreenContent(
    modifier: Modifier,
    isDiscreetMode: Boolean,
    sparkles: androidx.compose.runtime.snapshots.SnapshotStateList<Sparkle>,
    feedback: CounterFeedbackState,
    goal: TasbeehGoal,
    displayGoalName: String,
    showMeaning: Boolean,
    translation: String?,
    language: String,
    interactionSource: MutableInteractionSource,
    onBack: () -> Unit,
    onDetailsClick: () -> Unit,
    onMeaningToggle: () -> Unit,
    onEnterDiscreet: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isDiscreetMode) {
            CounterOledBlackoutView(
                sparkles = sparkles,
                onTap = { offset -> feedback.increment(goal, true, offset) }
            )
        } else {
            CounterStandardView(
                displayGoalName = displayGoalName,
                goal = goal,
                showMeaning = showMeaning,
                translation = translation,
                language = language,
                interactionSource = interactionSource,
                onBack = onBack,
                onDetailsClick = onDetailsClick,
                onMeaningToggle = onMeaningToggle,
                onIncrement = { feedback.increment(goal, false) },
                onEnterDiscreet = onEnterDiscreet
            )
        }
    }
}

@Composable
private fun CounterStandardView(
    displayGoalName: String,
    goal: TasbeehGoal,
    showMeaning: Boolean,
    translation: String?,
    language: String,
    interactionSource: MutableInteractionSource,
    onBack: () -> Unit,
    onDetailsClick: () -> Unit,
    onMeaningToggle: () -> Unit,
    onIncrement: () -> Unit,
    onEnterDiscreet: () -> Unit
) {
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
            onMeaningClick = onMeaningToggle
        )
        if (showMeaning && translation != null) {
            MeaningDisplay(translation = translation)
        }
        CounterCircle(
            goal = goal,
            onClick = onIncrement,
            interactionSource = interactionSource,
            modifier = Modifier.weight(1f),
            language = language
        )
        Surface(
            onClick = onEnterDiscreet,
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
