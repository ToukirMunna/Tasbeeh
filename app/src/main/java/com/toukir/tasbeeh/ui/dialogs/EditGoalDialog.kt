package com.toukir.tasbeeh.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun EditGoalDialog(
    goal: TasbeehGoal,
    isHomeSource: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var text by remember { mutableStateOf(goal.name) }

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
            EditGoalContent(
                text = text,
                onTextChange = { text = it },
                isHomeSource = isHomeSource,
                onDismiss = onDismiss,
                onDelete = onDelete,
                onRename = onRename
            )
        }
    }
}

@Composable
private fun EditGoalContent(
    text: String,
    onTextChange: (String) -> Unit,
    isHomeSource: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditGoalHeader(isHomeSource = isHomeSource, onDismiss = onDismiss)
        if (!isHomeSource) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                label = { Text(stringResource(R.string.label_tasbeeh_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
        } else {
            Text(stringResource(R.string.dialog_remove_confirm), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        EditGoalActionButtons(isHomeSource = isHomeSource, text = text, onDelete = onDelete, onRename = onRename)
    }
}

@Composable
private fun EditGoalHeader(isHomeSource: Boolean, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isHomeSource) stringResource(R.string.dialog_manage_goal) else stringResource(R.string.dialog_edit_tasbeeh),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            StudioIcon(StudioIcons.Close, contentDescription = stringResource(R.string.close))
        }
    }
}

@Composable
private fun EditGoalActionButtons(isHomeSource: Boolean, text: String, onDelete: () -> Unit, onRename: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onDelete,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
        ) {
            Text(if (isHomeSource) stringResource(R.string.btn_remove_goal) else stringResource(R.string.delete), fontWeight = FontWeight.Bold)
        }
        if (!isHomeSource) {
            Button(
                onClick = { if (text.isNotBlank()) onRename(text) },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.save), fontWeight = FontWeight.Bold)
            }
        }
    }
}
