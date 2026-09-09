package com.toukir.tasbeeh.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber
import java.util.Locale

@Composable
fun TasbeehListCard(
    goal: TasbeehGoal,
    displayName: String,
    allGoalsForThisName: List<TasbeehGoal>,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onAddToGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeDurations = allGoalsForThisName.filter { it.isGoal }.map { it.duration }
    val currentLanguage = Locale.getDefault().language

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pressClickEffect()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TasbeehCardInfo(
                goal = goal,
                displayName = displayName,
                activeDurations = activeDurations,
                currentLanguage = currentLanguage,
                onAddToGoal = onAddToGoal,
                modifier = Modifier.weight(1f)
            )
            TasbeehCardEditButton(onEditClick = onEditClick)
        }
    }
}

@Composable
private fun TasbeehCardInfo(
    goal: TasbeehGoal,
    displayName: String,
    activeDurations: List<GoalDuration>,
    currentLanguage: String,
    onAddToGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (activeDurations.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                DurationBadges(activeDurations)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TasbeehCardMetrics(goal = goal, currentLanguage = currentLanguage)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onAddToGoal,
            modifier = Modifier.height(34.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(
                text = stringResource(R.string.action_add_to_goals, stringResource(R.string.dialog_add_to_goals_title)),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DurationBadges(activeDurations: List<GoalDuration>) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        activeDurations.forEach { duration ->
            val color = when (duration) {
                GoalDuration.DAILY -> MaterialTheme.colorScheme.primary
                GoalDuration.WEEKLY -> Color(0xFF10B981)
                GoalDuration.MONTHLY -> Color(0xFF3B82F6)
                GoalDuration.YEARLY -> Color(0xFFF59E0B)
            }
            val labelRes = when (duration) {
                GoalDuration.DAILY -> R.string.badge_daily
                GoalDuration.WEEKLY -> R.string.badge_weekly
                GoalDuration.MONTHLY -> R.string.badge_monthly
                GoalDuration.YEARLY -> R.string.badge_yearly
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = color,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun TasbeehCardMetrics(goal: TasbeehGoal, currentLanguage: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StudioIcon(iconRes = StudioIcons.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = formatNumber(goal.currentCount, currentLanguage), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.width(16.dp))
        StudioIcon(iconRes = StudioIcons.History, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = formatNumber(goal.totalCount, currentLanguage), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
    }
}

@Composable
private fun TasbeehCardEditButton(onEditClick: () -> Unit) {
    Surface(
        onClick = onEditClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.size(38.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            StudioIcon(
                iconRes = StudioIcons.Edit,
                contentDescription = stringResource(R.string.cd_edit),
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
