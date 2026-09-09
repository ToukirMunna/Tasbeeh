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
import com.toukir.tasbeeh.data.AdhkarInfo
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun EditTasbeehDetailsDialog(
    initialInfo: AdhkarInfo,
    onDismiss: () -> Unit,
    onSave: (AdhkarInfo) -> Unit
) {
    var arabic by remember { mutableStateOf(initialInfo.arabic) }
    var translation by remember { mutableStateOf(initialInfo.translation) }
    var virtue by remember { mutableStateOf(initialInfo.virtue) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            EditTasbeehDetailsContent(
                arabic = arabic,
                onArabicChange = { arabic = it },
                translation = translation,
                onTranslationChange = { translation = it },
                virtue = virtue,
                onVirtueChange = { virtue = it },
                onDismiss = onDismiss,
                onSave = { onSave(initialInfo.copy(arabic = arabic, translation = translation, virtue = virtue)) }
            )
        }
    }
}

@Composable
private fun EditTasbeehDetailsContent(
    arabic: String,
    onArabicChange: (String) -> Unit,
    translation: String,
    onTranslationChange: (String) -> Unit,
    virtue: String,
    onVirtueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditDetailsHeader(onDismiss = onDismiss)
        EditDetailsFields(
            arabic = arabic,
            onArabicChange = onArabicChange,
            translation = translation,
            onTranslationChange = onTranslationChange,
            virtue = virtue,
            onVirtueChange = onVirtueChange
        )
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.save), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EditDetailsHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.dialog_edit_details), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            StudioIcon(StudioIcons.Close, contentDescription = stringResource(R.string.close))
        }
    }
}

@Composable
private fun EditDetailsFields(
    arabic: String,
    onArabicChange: (String) -> Unit,
    translation: String,
    onTranslationChange: (String) -> Unit,
    virtue: String,
    onVirtueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(value = arabic, onValueChange = onArabicChange, label = { Text(stringResource(R.string.label_arabic)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), minLines = 2)
        OutlinedTextField(value = translation, onValueChange = onTranslationChange, label = { Text(stringResource(R.string.label_translation)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), minLines = 2)
        OutlinedTextField(value = virtue, onValueChange = onVirtueChange, label = { Text(stringResource(R.string.label_virtue)) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), minLines = 3)
    }
}
