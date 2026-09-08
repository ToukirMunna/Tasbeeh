package com.toukir.tasbeeh.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.TasbeehHistory
import java.time.LocalDate
import java.time.YearMonth

private fun List<TasbeehGoal>.uniqueByTasbeehName(): List<TasbeehGoal> {
    return groupBy { it.name }.values.map { goals ->
        goals.maxBy { it.totalCount }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    history: List<TasbeehHistory>,
    goals: List<TasbeehGoal>,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onLeaderboardClick: () -> Unit = {},
    currentStreak: Int = 0,
    userName: String = "",
    isMale: Boolean = true,
    onUserNameEdit: () -> Unit = {},
    language: String = "en"
) {
    val context = LocalContext.current

    val uniqueGoals = remember(goals) { goals.uniqueByTasbeehName() }
    val totalAllTime = remember(uniqueGoals) { uniqueGoals.sumOf { it.totalCount } }
    val topTasbeehName = remember(uniqueGoals) {
        uniqueGoals.maxByOrNull { it.totalCount }?.let { AdhkarLibrary.getLocalizedName(context, it.name) } ?: ""
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.profile_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section (Streak + Welcome)
            item {
                ProfileHeaderSection(
                    userName = userName,
                    currentStreak = currentStreak,
                    isMale = isMale,
                    onNameClick = onUserNameEdit,
                    language = language
                )
            }

        // "At a Glance" Stats
        item {
            SummaryStatsRow(
                totalCount = totalAllTime,
                bestTasbeeh = topTasbeehName,
                language = language
            )
        }

        // Weekly Activity
        item {
            SectionHeader(title = stringResource(R.string.weekly_activity))
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    WeeklyBarChart(history = history, language = language)
                }
            }
        }

        // Navigation Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onStatisticsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .pressClickEffect(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        StudioIcon(
                            iconRes = StudioIcons.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.stats_title), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onHistoryClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .pressClickEffect(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        StudioIcon(
                            iconRes = StudioIcons.History,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.history_title), fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onLeaderboardClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .pressClickEffect(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        StudioIcon(
                            iconRes = StudioIcons.Leaderboard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.leaderboard_title), fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .pressClickEffect(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    StudioIcon(iconRes = StudioIcons.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.settings_title), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Top Used Tasbeeh
        item {
            SectionHeader(title = stringResource(R.string.top_used_tasbeeh))
            Spacer(modifier = Modifier.height(4.dp))
            val topTasbeehs = uniqueGoals.sortedByDescending { it.totalCount }.take(3)
            if (topTasbeehs.isEmpty()) {
                Text(
                    stringResource(R.string.no_tasbeeh_msg),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    topTasbeehs.forEach { goal ->
                        val localizedName = AdhkarLibrary.getLocalizedName(context, goal.name)
                        TopTasbeehItem(goal, localizedName, language)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
