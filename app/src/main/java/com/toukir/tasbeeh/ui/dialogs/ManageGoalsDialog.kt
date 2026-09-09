package com.toukir.tasbeeh.ui

import com.toukir.tasbeeh.ui.dialogs.ManageGoalRowItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import java.util.Collections

@Composable
fun ManageGoalsDialog(
    goals: List<TasbeehGoal>,
    onDismiss: () -> Unit,
    onUpdateGoals: (List<TasbeehGoal>) -> Unit
) {
    var currentGoals by remember { mutableStateOf(goals) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            ManageGoalsContent(
                currentGoals = currentGoals,
                onDismiss = onDismiss,
                onGoalsChange = { currentGoals = it },
                onSave = { onUpdateGoals(currentGoals) }
            )
        }
    }
}

@Composable
private fun ManageGoalsContent(
    currentGoals: List<TasbeehGoal>,
    onDismiss: () -> Unit,
    onGoalsChange: (List<TasbeehGoal>) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ManageGoalsHeader(onDismiss = onDismiss)

        Text(
            stringResource(R.string.dialog_manage_goals_desc), 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ManageGoalsLazyList(
            currentGoals = currentGoals,
            onGoalsChange = onGoalsChange,
            modifier = Modifier
                .weight(1f, fill = false)
                .heightIn(max = 550.dp)
        )

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                stringResource(R.string.save),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ManageGoalsLazyList(
    currentGoals: List<TasbeehGoal>,
    onGoalsChange: (List<TasbeehGoal>) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingItemOffset by remember { mutableStateOf(0f) }

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(currentGoals, key = { _, goal -> goal.id }) { index, goal ->
            val currentIndex by rememberUpdatedState(index)
            ManageGoalRowItem(
                goal = goal,
                index = index,
                isDragging = index == draggingItemIndex,
                draggingOffset = draggingItemOffset,
                onStartDrag = {
                    draggingItemIndex = currentIndex
                    draggingItemOffset = 0f
                },
                onDragChange = { dragAmountY ->
                    val (newIdx, newOff) = handleDragStep(draggingItemOffset, dragAmountY, currentIndex, currentGoals, onGoalsChange)
                    draggingItemIndex = newIdx
                    draggingItemOffset = newOff
                },
                onStopDrag = {
                    draggingItemIndex = null
                    draggingItemOffset = 0f
                },
                onUpdateTargetCount = { countStr ->
                    updateGoalTarget(index, countStr, currentGoals, onGoalsChange)
                },
                onUpdateDuration = { duration ->
                    updateGoalDuration(index, duration, currentGoals, onGoalsChange)
                },
                onRemove = {
                    val updatedList = currentGoals.toMutableList().apply { remove(goal) }
                    onGoalsChange(updatedList)
                }
            )
        }
    }
}

private fun handleDragStep(
    offset: Float,
    dragAmount: Float,
    currentIndex: Int,
    goals: List<TasbeehGoal>,
    onGoalsChange: (List<TasbeehGoal>) -> Unit
): Pair<Int?, Float> {
    val newOffset = offset + dragAmount
    if (kotlin.math.abs(newOffset) > 66f) {
        val direction = if (newOffset > 0) 1 else -1
        val target = currentIndex + direction
        if (target in goals.indices) {
            val newList = goals.toMutableList()
            Collections.swap(newList, currentIndex, target)
            onGoalsChange(newList)
            return Pair(target, 0f)
        }
    }
    return Pair(currentIndex, newOffset)
}

private fun updateGoalTarget(
    index: Int,
    newCount: String,
    goals: List<TasbeehGoal>,
    onGoalsChange: (List<TasbeehGoal>) -> Unit
) {
    val count = newCount.toIntOrNull() ?: return
    val newList = goals.toMutableList()
    newList[index] = newList[index].copy(targetCount = count)
    onGoalsChange(newList)
}

private fun updateGoalDuration(
    index: Int,
    duration: GoalDuration,
    goals: List<TasbeehGoal>,
    onGoalsChange: (List<TasbeehGoal>) -> Unit
) {
    val newList = goals.toMutableList()
    newList[index] = newList[index].copy(duration = duration)
    onGoalsChange(newList)
}

@Composable
private fun ManageGoalsHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.dialog_manage_goals_title), 
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            StudioIcon(StudioIcons.Close, contentDescription = stringResource(R.string.close))
        }
    }
}
