package com.toukir.tasbeeh.ui

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
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun LeaderboardSettingsDialog(
    currentUsername: String,
    currentIsAnonymous: Boolean,
    onConfirm: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(currentUsername) }
    var isAnonymous by remember { mutableStateOf(currentIsAnonymous) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            LeaderboardSettingsContent(
                username = username,
                onUsernameChange = { username = it },
                isAnonymous = isAnonymous,
                onAnonymousChange = { isAnonymous = it },
                onConfirm = { onConfirm(username, isAnonymous) },
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun LeaderboardSettingsContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    isAnonymous: Boolean,
    onAnonymousChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.leaderboard_settings), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                StudioIcon(StudioIcons.Close, contentDescription = stringResource(R.string.cd_close))
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(stringResource(R.string.leaderboard_username)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            enabled = !isAnonymous
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAnonymous, onCheckedChange = onAnonymousChange)
            Text(stringResource(R.string.anonymous), style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = isAnonymous || username.isNotBlank()
        ) {
            Text(stringResource(R.string.save), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
