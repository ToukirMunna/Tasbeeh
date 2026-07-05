package com.toukir.tasbeeh.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.utils.formatNumber

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    userName: String,
    streak: Int,
    onGoalClick: (TasbeehGoal) -> Unit,
    onManageGoals: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    val goals by viewModel.savedGoals.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val dailyGoals = goals.filter { it.isGoal && it.duration == GoalDuration.DAILY }
    val customGoals = goals.filter { it.isGoal && it.duration != GoalDuration.DAILY }
    
    val totalDailyCount = dailyGoals.sumOf { it.currentCount }
    val totalDailyTarget = dailyGoals.sumOf { it.targetCount }
    
    val totalCustomCount = customGoals.sumOf { it.currentCount }
    val totalCustomTarget = customGoals.sumOf { it.targetCount }

    var isDailyVisible by remember { mutableStateOf(value = true) }
    
    val displayCount = if (isDailyVisible) totalDailyCount else totalCustomCount
    val displayTarget = if (isDailyVisible) totalDailyTarget else totalCustomTarget
    val progress = if (displayTarget > 0) (displayCount.toFloat() / displayTarget.toFloat()) else 0f

    val rotation by animateFloatAsState(
        targetValue = if (isDailyVisible) 0f else 180f,
        animationSpec = tween(durationMillis = 600),
        label = "GoalSectionFlip"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 2.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Mosque Background
                Image(
                    painter = painterResource(id = R.drawable.mosque),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.BottomCenter)
                        .alpha(0.2f),
                    contentScale = ContentScale.FillWidth,
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HeaderSection(
                        userName = userName,
                        streak = streak,
                        syncStatus = syncStatus,
                        onSyncClick = { viewModel.syncToCloud() },
                        onFlip = { isDailyVisible = !isDailyVisible },
                        onManageGoals = { onManageGoals(isDailyVisible) }
                    )

                    Box(
                        modifier = Modifier.padding(bottom = if (settings.showCounterCircle) 0.dp else 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (settings.showCounterCircle) {
                            RedesignedCircularProgress(
                                progress = progress,
                                currentCount = formatNumber(displayCount, language),
                                size = 170.dp
                            )
                        } else {
                            Text(
                                text = formatNumber(displayCount, language),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                softWrap = false,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 84.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-4).sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Goal list container takes all remaining space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 8.dp) // Small padding to avoid touching the nav bar directly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
            ) {
                if (rotation <= 90f) {
                    // Front side: Daily Goals
                    GoalSectionContent(
                        goals = dailyGoals,
                        onGoalClick = onGoalClick,
                        isCustom = false
                    )
                } else {
                    // Back side: Custom Goals
                    Column(Modifier.graphicsLayer { rotationY = 180f }) {
                        GoalSectionContent(
                            goals = customGoals,
                            onGoalClick = onGoalClick,
                            isCustom = true
                        )
                    }
                }
            }
        }
    }
}
