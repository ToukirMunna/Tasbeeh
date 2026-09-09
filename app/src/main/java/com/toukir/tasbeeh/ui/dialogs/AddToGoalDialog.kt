package com.toukir.tasbeeh.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.DurationSelector
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun AddToGoalDialog(
    tasbeehName: String,
    onDismiss: () -> Unit,
    onConfirm: (GoalDuration, Int) -> Unit
) {
    var selectedDuration by remember { mutableStateOf(GoalDuration.DAILY) }
    var targetCount by remember { mutableStateOf("100") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            AddToGoalContent(
                tasbeehName = tasbeehName,
                selectedDuration = selectedDuration,
                onDurationChange = { selectedDuration = it },
                targetCount = targetCount,
                onTargetCountChange = { targetCount = it },
                onDismiss = onDismiss,
                onConfirm = {
                    val count = targetCount.toIntOrNull() ?: 100
                    onConfirm(selectedDuration, count)
                }
            )
        }
    }
}

@Composable
private fun AddToGoalContent(
    tasbeehName: String,
    selectedDuration: GoalDuration,
    onDurationChange: (GoalDuration) -> Unit,
    targetCount: String,
    onTargetCountChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AddToGoalHeader(tasbeehName = tasbeehName, onDismiss = onDismiss)
        AddToGoalDurationSection(selectedDuration = selectedDuration, onDurationChange = onDurationChange)
        AddToGoalTargetSection(targetCount = targetCount, onTargetCountChange = onTargetCountChange)
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.dialog_confirm_goal), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddToGoalHeader(tasbeehName: String, onDismiss: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.dialog_add_to_goals_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                StudioIcon(StudioIcons.Close, contentDescription = stringResource(R.string.close))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.dialog_configure_goal, tasbeehName), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AddToGoalDurationSection(selectedDuration: GoalDuration, onDurationChange: (GoalDuration) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.dialog_duration_frequency), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        DurationSelector(selectedDuration = selectedDuration, onDurationSelected = onDurationChange)
    }
}

@Composable
private fun AddToGoalTargetSection(targetCount: String, onTargetCountChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.dialog_target_count), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = targetCount,
            onValueChange = { if (it.all { char -> char.isDigit() }) onTargetCountChange(it) },
            placeholder = { Text(stringResource(R.string.dialog_target_count_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant)
        )
    }
}
