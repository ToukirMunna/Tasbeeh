package com.toukir.tasbeeh.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.TasbeehGoal
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import com.toukir.tasbeeh.utils.formatNumber
import java.util.Locale

@Composable
fun SyncStatusIcon(
    status: SyncStatus,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "SyncRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SyncRotation"
    )

    AnimatedVisibility(
        visible = status != SyncStatus.IDLE,
        enter = fadeIn() + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally()
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp).pressClickEffect(),
            enabled = status != SyncStatus.SYNCING
        ) {
            when (status) {
                SyncStatus.SYNCING -> {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Sync,
                        contentDescription = "Syncing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).rotate(rotation)
                    )
                }
                SyncStatus.SYNCED -> {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.CloudDone,
                        contentDescription = "Synced",
                        tint = Color(0xFF4CAF50), // Green for success
                        modifier = Modifier.size(20.dp)
                    )
                }
                SyncStatus.ERROR -> {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.CloudOff,
                        contentDescription = "Offline/Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun DurationChip(duration: GoalDuration, color: Color) {
    val (text, chipColor) = when (duration) {
        GoalDuration.DAILY -> "Daily" to color
        GoalDuration.WEEKLY -> "Weekly" to MaterialTheme.colorScheme.primary
        GoalDuration.MONTHLY -> "Monthly" to Color(0xFF3B82F6) // Toukir Cobalt nuance
        GoalDuration.YEARLY -> "Yearly" to Color(0xFFF59E0B) // Toukir Amber nuance
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = chipColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HeaderSection(
    userName: String,
    streak: Int,
    syncStatus: SyncStatus,
    onSyncClick: () -> Unit,
    onFlip: () -> Unit,
    onManageGoals: () -> Unit
) {
    val currentLanguage = Locale.getDefault().language
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
            Text(
                text = "Assalamu Alaikum, $userName 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row(
            modifier = Modifier.padding(16.dp).align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SyncStatusIcon(
                status = syncStatus,
                onClick = onSyncClick
            )

            IconButton(
                onClick = onFlip,
                modifier = Modifier.size(32.dp).pressClickEffect()
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Flip,
                    contentDescription = "Switch View",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onManageGoals,
                modifier = Modifier.size(32.dp).pressClickEffect()
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Reorder,
                    contentDescription = "Edit Order",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatNumber(streak, currentLanguage),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun getIconForGoal(name: String): Pair<ImageVector, Color> {
    val mint = MaterialTheme.colorScheme.primary
    return when {
        name.contains("Allahu Akbar", true) -> Icons.Default.Flare to mint
        name.contains("La Ilaha Illallah", true) -> Icons.Default.Eco to mint
        name.contains("Astaghfirullah", true) -> Icons.Default.Flare to mint
        name.contains("SubhanAllah", true) -> Icons.Default.Favorite to mint
        name.contains("Durood", true) -> Icons.Default.NightsStay to mint
        else -> Icons.Default.Spa to mint
    }
}

@Composable
fun GoalProgressItem(
    goal: TasbeehGoal,
    displayName: String = goal.name,
    icon: ImageVector = Icons.Default.Spa,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    showDurationChip: Boolean = false,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    onClick: () -> Unit
) {
    val currentLanguage = Locale.getDefault().language
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
            // Leading Icon (TDS Inset Container)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(if (isCompact) 8.dp else 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.size(iconSize)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
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
            
            Spacer(modifier = Modifier.width(spacing))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
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
    }
}
