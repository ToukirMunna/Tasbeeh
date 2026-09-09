package com.toukir.tasbeeh.ui.dialogs

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.ui.DurationSelector
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun ManageGoalRowItem(
    goal: TasbeehGoal,
    index: Int,
    isDragging: Boolean,
    draggingOffset: Float,
    onStartDrag: () -> Unit,
    onDragChange: (Float) -> Unit,
    onStopDrag: () -> Unit,
    onUpdateTargetCount: (String) -> Unit,
    onUpdateDuration: (GoalDuration) -> Unit,
    onRemove: () -> Unit
) {
    val rowModifier = if (isDragging) {
        Modifier.zIndex(1f).graphicsLayer { translationY = draggingOffset }.shadow(8.dp, RoundedCornerShape(16.dp))
    } else Modifier

    Surface(
        modifier = rowModifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDragging) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        border = if (isDragging) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            ManageGoalRowHeader(
                goal = goal,
                onStartDrag = onStartDrag,
                onDragChange = onDragChange,
                onStopDrag = onStopDrag,
                onUpdateTargetCount = onUpdateTargetCount,
                onRemove = onRemove
            )
            Spacer(modifier = Modifier.height(8.dp))
            DurationSelector(selectedDuration = goal.duration, onDurationSelected = onUpdateDuration)
        }
    }
}

@Composable
private fun ManageGoalRowHeader(
    goal: TasbeehGoal,
    onStartDrag: () -> Unit,
    onDragChange: (Float) -> Unit,
    onStopDrag: () -> Unit,
    onUpdateTargetCount: (String) -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StudioIcon(
            iconRes = StudioIcons.Checklist,
            contentDescription = stringResource(R.string.dialog_reorder_content_desc),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp).pointerInput(goal.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onStartDrag() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragChange(dragAmount.y)
                    },
                    onDragEnd = { onStopDrag() },
                    onDragCancel = { onStopDrag() }
                )
            }
        )
        Text(
            AdhkarLibrary.getLocalizedName(context, goal.name),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        OutlinedTextField(
            value = goal.targetCount.toString(),
            onValueChange = onUpdateTargetCount,
            placeholder = { Text(stringResource(R.string.dialog_target_count_placeholder), style = MaterialTheme.typography.bodySmall) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(75.dp).height(50.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent)
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
            StudioIcon(StudioIcons.Delete, stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error)
        }
    }
}
