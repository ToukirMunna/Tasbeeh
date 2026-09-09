package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.toukir.tasbeeh.data.LeaderboardEntry

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
    val period = when (selectedTab) {
        0 -> "daily"
        1 -> "weekly"
        2 -> "monthly"
        else -> "daily"
    }

    LaunchedEffect(selectedTab, isEnabled) {
        if (isEnabled) {
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
                LeaderboardTopBar(
                    isEnabled = isEnabled,
                    onBack = onBack,
                    onUsernameEdit = onUsernameEdit,
                    onRefresh = { onRefresh(period) }
                )
            }
        ) { padding ->
            LeaderboardScreenBody(
                padding = padding,
                isEnabled = isEnabled,
                isLoading = isLoading,
                error = error,
                entries = entries,
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                onToggle = onToggle,
                onRefresh = { onRefresh(period) },
                currentUserId = currentUserId,
                language = language,
                period = period
            )
        }
    }
}
