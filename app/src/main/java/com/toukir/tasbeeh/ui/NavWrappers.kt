package com.toukir.tasbeeh.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.TasbeehHistory

@Composable
fun LeaderboardNavScreen(
    viewModel: MainViewModel,
    settings: AppSettings,
    onNavigate: (String) -> Unit,
    onOpenLeaderboardSettings: () -> Unit
) {
    val leaderboardData = viewModel.leaderboardData.value
    val isLeaderboardLoading = viewModel.isLeaderboardLoading.value
    val leaderboardError = viewModel.leaderboardError.value
    val isLeaderboardEnabled = viewModel.isLeaderboardEnabled.value

    LaunchedEffect(Unit) {
        if (isLeaderboardEnabled) {
            viewModel.fetchLeaderboard("daily")
        }
    }
    LeaderboardScreen(
        entries = leaderboardData,
        isLoading = isLeaderboardLoading,
        isEnabled = isLeaderboardEnabled,
        error = leaderboardError,
        onBack = { onNavigate("dashboard") },
        onRefresh = { period -> viewModel.fetchLeaderboard(period) },
        onToggle = { enabled -> viewModel.toggleLeaderboard(enabled) },
        onUsernameEdit = onOpenLeaderboardSettings,
        currentUserId = viewModel.firebaseManager.currentUser?.uid ?: "",
        language = settings.language
    )
}

@Composable
fun DashboardNavScreen(
    combinedHistory: List<TasbeehHistory>,
    savedGoals: List<TasbeehGoal>,
    currentStreak: Int,
    userName: String,
    userIsMale: Boolean,
    settings: AppSettings,
    onNavigate: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNameInput: () -> Unit,
    onSetHistoryTab: (Int) -> Unit
) {
    ProfileScreen(
        history = combinedHistory,
        goals = savedGoals,
        onSettingsClick = onOpenSettings,
        onStatisticsClick = { onNavigate("statistics") },
        onHistoryClick = {
            onSetHistoryTab(1)
            onNavigate("history")
        },
        onLeaderboardClick = { onNavigate("leaderboard") },
        currentStreak = currentStreak,
        userName = userName,
        isMale = userIsMale,
        onUserNameEdit = onOpenNameInput,
        language = settings.language
    )

    LaunchedEffect(userName) {
        if (userName.isEmpty()) {
            onOpenNameInput()
        }
    }
}
