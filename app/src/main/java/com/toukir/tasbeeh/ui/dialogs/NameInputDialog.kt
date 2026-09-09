package com.toukir.tasbeeh.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import com.toukir.tasbeeh.ui.common.UserAvatar

@Composable
fun NameInputDialog(
    currentName: String,
    currentIsMale: Boolean,
    onConfirm: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var isMale by remember { mutableStateOf(currentIsMale) }

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
            NameInputContent(
                name = name,
                onNameChange = { name = it },
                isMale = isMale,
                onGenderChange = { isMale = it },
                onConfirm = { if (name.isNotBlank()) onConfirm(name, isMale) }
            )
        }
    }
}

@Composable
private fun NameInputContent(
    name: String,
    onNameChange: (String) -> Unit,
    isMale: Boolean,
    onGenderChange: (Boolean) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.edit_profile), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        GenderSelectionRow(isMale = isMale, onGenderChange = onGenderChange)
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.label_your_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Button(
            onClick = onConfirm,
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.save), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GenderSelectionRow(isMale: Boolean, onGenderChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        GenderOption(label = stringResource(R.string.label_male), isMale = true, isSelected = isMale, onClick = { onGenderChange(true) }, modifier = Modifier.weight(1f))
        GenderOption(label = stringResource(R.string.label_female), isMale = false, isSelected = !isMale, onClick = { onGenderChange(false) }, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun GenderOption(
    label: String,
    isMale: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            UserAvatar(isMale = isMale, size = 48.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}
