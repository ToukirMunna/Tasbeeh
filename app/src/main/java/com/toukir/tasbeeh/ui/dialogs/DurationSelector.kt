package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationSelector(
    selectedDuration: GoalDuration,
    onDurationSelected: (GoalDuration) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GoalDuration.entries.forEach { duration ->
            val textRes = when (duration) {
                GoalDuration.DAILY -> R.string.duration_daily
                GoalDuration.WEEKLY -> R.string.duration_weekly
                GoalDuration.MONTHLY -> R.string.duration_monthly
                GoalDuration.YEARLY -> R.string.duration_yearly
            }
            FilterChip(
                selected = selectedDuration == duration,
                onClick = { onDurationSelected(duration) },
                label = {
                    Text(
                        stringResource(textRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedDuration == duration,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
