package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.LeaderboardEntry
import com.toukir.tasbeeh.ui.theme.TasbeehTheme

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    TasbeehTheme {
        LeaderboardScreen(
            entries = listOf(
                LeaderboardEntry("1", "User One", 100, 500, 2000, isMale = true),
                LeaderboardEntry("2", "User Two", 80, 400, 1500, isMale = false),
                LeaderboardEntry("current", "Me", 120, 600, 2500, isMale = true)
            ),
            isLoading = false,
            isEnabled = true,
            error = null,
            onBack = {},
            onRefresh = {},
            onToggle = {},
            onUsernameEdit = {},
            currentUserId = "current",
            language = "en"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    isLoading: Boolean,
    isEnabled: Boolean,
    error: String?,
    onBack: () -> Unit,
    onRefresh: (String) -> Unit,
    onToggle: (Boolean) -> Unit,
    onUsernameEdit: () -> Unit,
    currentUserId: String,
    language: String
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.period_daily),
        stringResource(R.string.period_weekly),
        stringResource(R.string.period_monthly)
    )

    LaunchedEffect(selectedTab, isEnabled) {
        if (isEnabled) {
            val period = when (selectedTab) {
                0 -> "daily"
                1 -> "weekly"
                2 -> "monthly"
                else -> "daily"
            }
            onRefresh(period)
        }
    }

    if (isLoading && entries.isEmpty() && isEnabled) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.leaderboard_title), fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            StudioIcon(StudioIcons.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (isEnabled) {
                            IconButton(onClick = onUsernameEdit) {
                                StudioIcon(StudioIcons.Settings, contentDescription = "Settings")
                            }
                            IconButton(onClick = {
                                val period = when (selectedTab) {
                                    0 -> "daily"
                                    1 -> "weekly"
                                    2 -> "monthly"
                                    else -> "daily"
                                }
                                onRefresh(period)
                            }) {
                                StudioIcon(StudioIcons.Refresh, contentDescription = "Refresh")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (!isEnabled) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            StudioIcon(
                                iconRes = StudioIcons.Leaderboard,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.leaderboard_opt_in_msg),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { onToggle(true) },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            ) {
                                Text(stringResource(R.string.leaderboard_opt_in_btn))
                            }
                        }
                    }
                } else {
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        ) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    if (error != null && entries.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Something went wrong",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { 
                                    val period = when (selectedTab) {
                                        0 -> "daily"
                                        1 -> "weekly"
                                        2 -> "monthly"
                                        else -> "daily"
                                    }
                                    onRefresh(period)
                                }) {
                                    Text("Retry")
                                }
                            }
                        }
                    } else if (entries.isEmpty() && !isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.leaderboard_empty))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            itemsIndexed(entries) { index, entry ->
                                val period = when (selectedTab) {
                                    0 -> "daily"
                                    1 -> "weekly"
                                    2 -> "monthly"
                                    else -> "daily"
                                }
                                LeaderboardRankItem(
                                    entry = entry,
                                    rank = index + 1,
                                    isCurrentUser = entry.userId == currentUserId,
                                    language = language,
                                    period = period
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
