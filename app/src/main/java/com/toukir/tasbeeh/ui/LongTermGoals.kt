package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@Composable
fun GoalSectionContent(
    goals: List<TasbeehGoal>,
    onGoalClick: (TasbeehGoal) -> Unit,
    isCustom: Boolean,
    language: String = "en"
) {
    val context = LocalContext.current
    val isCompact = goals.size > 6
    
    Column(
        verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (goals.isEmpty()) {
            Text(
                if (isCustom) stringResource(R.string.no_long_term_goals) else stringResource(R.string.no_goals),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
        } else {
            goals.forEach { goal ->
                val localizedName = remember(goal.name, language, context) {
                    AdhkarLibrary.getLocalizedName(context, goal.name)
                }
                val iconInfo = getIconForGoal(goal.name)
                GoalProgressItem(
                    goal = goal,
                    displayName = localizedName,
                    iconRes = iconInfo.first,
                    iconColor = iconInfo.second,
                    showDurationChip = isCustom,
                    isCompact = isCompact,
                    language = language,
                    onClick = { onGoalClick(goal) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                )
            }
        }
    }
}
