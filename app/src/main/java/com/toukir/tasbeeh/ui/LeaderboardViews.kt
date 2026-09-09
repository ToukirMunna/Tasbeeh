package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.LeaderboardEntry
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardTopBar(
    isEnabled: Boolean,
    onBack: () -> Unit,
    onUsernameEdit: () -> Unit,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.leaderboard_title), fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                StudioIcon(StudioIcons.ArrowBack, contentDescription = stringResource(R.string.cd_back))
            }
        },
        actions = {
            if (isEnabled) {
                IconButton(onClick = onUsernameEdit) {
                    StudioIcon(StudioIcons.Settings, contentDescription = stringResource(R.string.cd_settings))
                }
                IconButton(onClick = onRefresh) {
                    StudioIcon(StudioIcons.Refresh, contentDescription = stringResource(R.string.cd_refresh))
                }
            }
        }
    )
}

@Composable
fun LeaderboardScreenBody(
    padding: PaddingValues,
    isEnabled: Boolean,
    isLoading: Boolean,
    error: String?,
    entries: List<LeaderboardEntry>,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    onToggle: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    currentUserId: String,
    language: String,
    period: String
) {
    Column(modifier = Modifier.padding(padding)) {
        if (!isEnabled) {
            LeaderboardOptInView(onToggle = onToggle)
        } else {
            LeaderboardTabs(selectedTab = selectedTab, onTabSelect = onTabSelect)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(4.dp)) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
            when {
                error != null && entries.isEmpty() -> LeaderboardErrorView(error = error, onRetry = onRefresh)
                entries.isEmpty() && !isLoading -> LeaderboardEmptyView()
                else -> LeaderboardListContent(
                    entries = entries,
                    currentUserId = currentUserId,
                    language = language,
                    period = period
                )
            }
        }
    }
}

@Composable
fun LeaderboardTabs(selectedTab: Int, onTabSelect: (Int) -> Unit) {
    val tabs = listOf(
        stringResource(R.string.period_daily),
        stringResource(R.string.period_weekly),
        stringResource(R.string.period_monthly)
    )
    TabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelect(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun LeaderboardOptInView(onToggle: (Boolean) -> Unit) {
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
                textAlign = TextAlign.Center,
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
}

@Composable
fun LeaderboardErrorView(error: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.error_something_went_wrong),
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
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun LeaderboardEmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.leaderboard_empty))
    }
}

@Composable
fun LeaderboardListContent(
    entries: List<LeaderboardEntry>,
    currentUserId: String,
    language: String,
    period: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(entries, key = { _, entry -> entry.userId.ifEmpty { "entry_${entry.username}_${entry.lastUpdated}" } }) { index, entry ->
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
