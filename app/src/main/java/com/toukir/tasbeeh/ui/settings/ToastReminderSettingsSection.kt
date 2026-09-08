package com.toukir.tasbeeh.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@Composable
fun ToastReminderSettings(
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    text: String,
    onTextChange: (String) -> Unit,
    interval: Int,
    onIntervalChange: (Int) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.group_toast)) {
        SettingsSwitchItem(
            title = stringResource(R.string.setting_toast_enable),
            subtitle = stringResource(R.string.setting_toast_desc),
            icon = StudioIcons.Timer,
            checked = isEnabled,
            onCheckedChange = onEnabledChange
        )

        AnimatedVisibility(
            visible = isEnabled,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            ToastReminderExpandedFields(
                text = text,
                onTextChange = onTextChange,
                interval = interval,
                onIntervalChange = onIntervalChange
            )
        }
    }
}

@Composable
private fun ToastReminderExpandedFields(
    text: String,
    onTextChange: (String) -> Unit,
    interval: Int,
    onIntervalChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        SettingsDivider(modifier = Modifier.padding(bottom = 16.dp))
        
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text(stringResource(R.string.setting_toast_text)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { StudioIcon(StudioIcons.FormatQuote, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                StudioIcon(StudioIcons.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.setting_toast_interval),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            OutlinedTextField(
                value = if (interval == 0) "" else interval.toString(),
                onValueChange = { 
                    if (it.isEmpty()) onIntervalChange(0)
                    else it.toIntOrNull()?.let { valInt -> onIntervalChange(valInt) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(80.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
            )
        }
    }
}
