package com.toukir.tasbeeh.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import androidx.compose.ui.graphics.graphicsLayer
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
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
                    StudioIcon(
                        iconRes = StudioIcons.Refresh,
                        contentDescription = stringResource(R.string.cd_syncing),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).rotate(rotation)
                    )
                }
                SyncStatus.SYNCED -> {
                    StudioIcon(
                        iconRes = StudioIcons.CheckCircle,
                        contentDescription = stringResource(R.string.cd_synced),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                SyncStatus.ERROR -> {
                    StudioIcon(
                        iconRes = StudioIcons.Block,
                        contentDescription = stringResource(R.string.cd_offline),
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
fun HeaderSection(
    userName: String,
    streak: Int,
    syncStatus: SyncStatus,
    onSyncClick: () -> Unit,
    onFlip: () -> Unit,
    onManageGoals: () -> Unit,
    language: String = "en"
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        HeaderGreeting(userName = userName)
        HeaderActionsStreak(
            streak = streak,
            syncStatus = syncStatus,
            onSyncClick = onSyncClick,
            onFlip = onFlip,
            onManageGoals = onManageGoals,
            language = language,
            modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun HeaderGreeting(userName: String) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
        val greeting = stringResource(R.string.greeting)
        val displayName = userName.ifEmpty { stringResource(R.string.user) }
        Text(
            text = "$greeting $displayName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HeaderActionsStreak(
    streak: Int,
    syncStatus: SyncStatus,
    onSyncClick: () -> Unit,
    onFlip: () -> Unit,
    onManageGoals: () -> Unit,
    language: String = "en",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SyncStatusIcon(status = syncStatus, onClick = onSyncClick)
        IconButton(onClick = onFlip, modifier = Modifier.size(32.dp).pressClickEffect()) {
            StudioIcon(
                iconRes = StudioIcons.Refresh,
                contentDescription = stringResource(R.string.cd_switch_view),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        IconButton(onClick = onManageGoals, modifier = Modifier.size(32.dp).pressClickEffect()) {
            StudioIcon(
                iconRes = StudioIcons.Checklist,
                contentDescription = stringResource(R.string.cd_edit_order),
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
                StudioIcon(
                    iconRes = StudioIcons.LocalFireDepartment,
                    contentDescription = stringResource(R.string.cd_streak),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatNumber(streak, language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun HomeGoalFlipper(
    rotation: Float,
    dailyGoals: List<TasbeehGoal>,
    customGoals: List<TasbeehGoal>,
    onGoalClick: (TasbeehGoal) -> Unit,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
        ) {
            if (rotation <= 90f) {
                GoalSectionContent(
                    goals = dailyGoals,
                    onGoalClick = onGoalClick,
                    isCustom = false,
                    language = language
                )
            } else {
                Column(Modifier.graphicsLayer { rotationY = 180f }) {
                    GoalSectionContent(
                        goals = customGoals,
                        onGoalClick = onGoalClick,
                        isCustom = true,
                        language = language
                    )
                }
            }
        }
    }
}
