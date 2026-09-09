package com.toukir.tasbeeh.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber
import java.util.Locale

@Composable
fun DurationChip(duration: GoalDuration, color: Color) {
    val (textRes, chipColor) = when (duration) {
        GoalDuration.DAILY -> R.string.duration_daily to color
        GoalDuration.WEEKLY -> R.string.duration_weekly to MaterialTheme.colorScheme.primary
        GoalDuration.MONTHLY -> R.string.duration_monthly to Color(0xFF3B82F6)
        GoalDuration.YEARLY -> R.string.duration_yearly to Color(0xFFF59E0B)
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = chipColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun getIconForGoal(name: String): Pair<Int, Color> {
    val mint = MaterialTheme.colorScheme.primary
    return when {
        name.contains("Allahu Akbar", true) -> StudioIcons.FlashOn to mint
        name.contains("La Ilaha Illallah", true) -> StudioIcons.Healing to mint
        name.contains("Astaghfirullah", true) -> StudioIcons.RestartAlt to mint
        name.contains("SubhanAllah", true) -> StudioIcons.AutoAwesome to mint
        name.contains("Durood", true) -> StudioIcons.Bookmark to mint
        else -> StudioIcons.Lightbulb to mint
    }
}

@Composable
fun GoalProgressItem(
    goal: TasbeehGoal,
    displayName: String = goal.name,
    @DrawableRes iconRes: Int = StudioIcons.Lightbulb,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    showDurationChip: Boolean = false,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    language: String = "",
    onClick: () -> Unit
) {
    val currentLanguage = language.ifEmpty { Locale.getDefault().language }
    val verticalPadding = if (isCompact) 6.dp else 12.dp
    val iconSize = if (isCompact) 36.dp else 44.dp
    val spacing = if (isCompact) 8.dp else 14.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pressClickEffect()
            .clickable { onClick() },
        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoalLeadingIcon(
                iconRes = iconRes,
                iconColor = iconColor,
                iconSize = iconSize,
                isCompact = isCompact,
                showDurationChip = showDurationChip,
                goal = goal
            )
            Spacer(modifier = Modifier.width(spacing))
            GoalProgressContent(
                displayName = displayName,
                goal = goal,
                isCompact = isCompact,
                currentLanguage = currentLanguage,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GoalLeadingIcon(
    @DrawableRes iconRes: Int,
    iconColor: Color,
    iconSize: Dp,
    isCompact: Boolean,
    showDurationChip: Boolean,
    goal: TasbeehGoal
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(if (isCompact) 8.dp else 12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.size(iconSize)
        ) {
            Box(contentAlignment = Alignment.Center) {
                StudioIcon(
                    iconRes = iconRes,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                )
            }
        }
        if (showDurationChip && goal.duration != GoalDuration.DAILY) {
            DurationChip(goal.duration, iconColor)
        }
    }
}

@Composable
private fun GoalProgressContent(
    displayName: String,
    goal: TasbeehGoal,
    isCompact: Boolean,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = displayName,
                style = if (isCompact) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = "${formatNumber(goal.currentCount, currentLanguage)} / ${formatNumber(goal.targetCount, currentLanguage)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))
        LinearProgressIndicator(
            progress = { if (goal.targetCount > 0) goal.currentCount.toFloat() / goal.targetCount.toFloat() else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isCompact) 4.dp else 6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}
