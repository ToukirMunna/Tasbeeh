package com.toukir.tasbeeh.ui

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

    fun updateTargetCount(index: Int, newCount: String) {
        val count = newCount.toIntOrNull() ?: return
        val newList = currentGoals.toMutableList()
        newList[index] = newList[index].copy(targetCount = count)
        currentGoals = newList
    }

    fun updateDuration(index: Int, duration: GoalDuration) {
        val newList = currentGoals.toMutableList()
        newList[index] = newList[index].copy(duration = duration)
        currentGoals = newList
    }

    fun removeGoal(goal: TasbeehGoal) {
        val updatedList = currentGoals.toMutableList()
        updatedList.remove(goal)
        currentGoals = updatedList
    }

    fun moveItem(from: Int, to: Int) {
        if (from == to || to < 0 || to >= currentGoals.size) return
        val newList = currentGoals.toMutableList()
        Collections.swap(newList, from, to)
        currentGoals = newList
    }

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
                onUpdateTargetCount = ::updateTargetCount,
                onUpdateDuration = ::updateDuration,
                onRemoveGoal = ::removeGoal,
                onMoveItem = ::moveItem,
                onSave = { onUpdateGoals(currentGoals) }
            )
        }
    }
}

@Composable
private fun ManageGoalsContent(
    currentGoals: List<TasbeehGoal>,
    onDismiss: () -> Unit,
    onUpdateTargetCount: (Int, String) -> Unit,
    onUpdateDuration: (Int, GoalDuration) -> Unit,
    onRemoveGoal: (TasbeehGoal) -> Unit,
    onMoveItem: (Int, Int) -> Unit,
    onSave: () -> Unit
) {
    val listState = rememberLazyListState()
    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingItemOffset by remember { mutableStateOf(0f) }

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

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f, fill = false)
                .heightIn(max = 550.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(currentGoals, key = { _, goal -> goal.id }) { index, goal ->
                val currentIndex by rememberUpdatedState(index)
                val isDragging = index == draggingItemIndex

                ManageGoalRowItem(
                    goal = goal,
                    index = index,
                    isDragging = isDragging,
                    draggingOffset = draggingItemOffset,
                    onStartDrag = {
                        draggingItemIndex = currentIndex
                        draggingItemOffset = 0f
                    },
                    onDragChange = { dragAmountY ->
                        draggingItemOffset += dragAmountY
                        val currentOffset = draggingItemOffset
                        if (kotlin.math.abs(currentOffset) > 66f) {
                            val direction = if (currentOffset > 0) 1 else -1
                            val targetIndex = currentIndex + direction
                            if (targetIndex in currentGoals.indices) {
                                onMoveItem(currentIndex, targetIndex)
                                draggingItemIndex = targetIndex
                                draggingItemOffset = 0f
                            }
                        }
                    },
                    onStopDrag = {
                        draggingItemIndex = null
                        draggingItemOffset = 0f
                    },
                    onUpdateTargetCount = { onUpdateTargetCount(index, it) },
                    onUpdateDuration = { onUpdateDuration(index, it) },
                    onRemove = { onRemoveGoal(goal) }
                )
            }
        }

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
