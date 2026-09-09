package com.toukir.tasbeeh.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.data.AdhkarInfo

@Composable
fun AppNavDestinationRouter(
    screen: String,
    goal: TasbeehGoal?,
    detail: TasbeehGoal?,
    viewModel: MainViewModel,
    settings: AppSettings,
    savedGoals: List<TasbeehGoal>,
    combinedHistory: List<TasbeehHistory>,
    customDetails: Map<String, AdhkarInfo>,
    userName: String,
    userIsMale: Boolean,
    currentStreak: Int,
    historyInitialTab: Int,
    onNavigate: (String) -> Unit,
    onSelectGoal: (Int?) -> Unit,
    onSelectDetail: (Int?) -> Unit,
    onEditGoal: (TasbeehGoal) -> Unit,
    onAddToGoal: (TasbeehGoal) -> Unit,
    onManageGoals: (Boolean) -> Unit,
    onEditAdhkarInfo: (AdhkarInfo) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNameInput: () -> Unit,
    onOpenLeaderboardSettings: () -> Unit,
    onSetHistoryTab: (Int) -> Unit
) {
    when {
        goal != null || detail != null -> DetailAndCounterContent(
            goal = goal,
            detail = detail,
            viewModel = viewModel,
            settings = settings,
            combinedHistory = combinedHistory,
            customDetails = customDetails,
            onSelectGoal = onSelectGoal,
            onSelectDetail = onSelectDetail,
            onEditAdhkarInfo = onEditAdhkarInfo
        )
        screen in listOf("statistics", "history", "leaderboard") -> SecondaryScreensContent(
            screen = screen,
            history = combinedHistory,
            historyInitialTab = historyInitialTab,
            settings = settings,
            viewModel = viewModel,
            onNavigate = onNavigate,
            onOpenLeaderboardSettings = onOpenLeaderboardSettings
        )
        else -> PrimaryTabContent(
            screen = screen,
            viewModel = viewModel,
            savedGoals = savedGoals,
            combinedHistory = combinedHistory,
            currentStreak = currentStreak,
            userName = userName,
            userIsMale = userIsMale,
            settings = settings,
            onNavigate = onNavigate,
            onSelectGoal = onSelectGoal,
            onSelectDetail = onSelectDetail,
            onEditGoal = onEditGoal,
            onAddToGoal = onAddToGoal,
            onManageGoals = onManageGoals,
            onOpenSettings = onOpenSettings,
            onOpenNameInput = onOpenNameInput,
            onSetHistoryTab = onSetHistoryTab
        )
    }
}

@Composable
fun PrimaryTabContent(
    screen: String,
    viewModel: MainViewModel,
    savedGoals: List<TasbeehGoal>,
    combinedHistory: List<TasbeehHistory>,
    currentStreak: Int,
    userName: String,
    userIsMale: Boolean,
    settings: AppSettings,
    onNavigate: (String) -> Unit,
    onSelectGoal: (Int?) -> Unit,
    onSelectDetail: (Int?) -> Unit,
    onEditGoal: (TasbeehGoal) -> Unit,
    onAddToGoal: (TasbeehGoal) -> Unit,
    onManageGoals: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNameInput: () -> Unit,
    onSetHistoryTab: (Int) -> Unit
) {
    when (screen) {
        "home" -> HomeScreen(
            viewModel = viewModel,
            userName = userName,
            streak = currentStreak,
            onGoalClick = { onSelectGoal(it.id) },
            onManageGoals = onManageGoals,
            language = settings.language
        )
        "tasbeehs" -> TasbeehsListScreen(
            goals = savedGoals,
            onGoalClick = { onSelectDetail(it.id) },
            onEditGoal = onEditGoal,
            onAddToGoal = onAddToGoal
        )
        "dashboard" -> DashboardNavScreen(
            combinedHistory = combinedHistory,
            savedGoals = savedGoals,
            currentStreak = currentStreak,
            userName = userName,
            userIsMale = userIsMale,
            settings = settings,
            onNavigate = onNavigate,
            onOpenSettings = onOpenSettings,
            onOpenNameInput = onOpenNameInput,
            onSetHistoryTab = onSetHistoryTab
        )
    }
}

@Composable
fun DetailAndCounterContent(
    goal: TasbeehGoal?,
    detail: TasbeehGoal?,
    viewModel: MainViewModel,
    settings: AppSettings,
    combinedHistory: List<TasbeehHistory>,
    customDetails: Map<String, AdhkarInfo>,
    onSelectGoal: (Int?) -> Unit,
    onSelectDetail: (Int?) -> Unit,
    onEditAdhkarInfo: (AdhkarInfo) -> Unit
) {
    if (goal != null) {
        CounterScreen(
            goal = goal,
            onBack = {
                onSelectGoal(null)
                viewModel.syncToCloud()
            },
            onIncrement = { viewModel.incrementGoal(goal) },
            onDetailsClick = {
                onSelectDetail(goal.id)
                onSelectGoal(null)
            },
            isSoundEnabled = settings.isSoundEnabled,
            isVibrateTapEnabled = settings.isVibrateTapEnabled,
            isVibrate100Enabled = settings.isVibrate100Enabled,
            language = settings.language
        )
    } else if (detail != null) {
        TasbeehDetailsScreen(
            goal = detail,
            history = combinedHistory,
            customDetails = customDetails,
            onBack = { onSelectDetail(null) },
            onCountClick = { onSelectGoal(detail.id) },
            onEditDetails = onEditAdhkarInfo
        )
    }
}

@Composable
fun SecondaryScreensContent(
    screen: String,
    history: List<TasbeehHistory>,
    historyInitialTab: Int,
    settings: AppSettings,
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    onOpenLeaderboardSettings: () -> Unit
) {
    when (screen) {
        "statistics" -> StatisticsScreen(
            history = history,
            onBack = { onNavigate("dashboard") },
            language = settings.language
        )
        "history" -> HistoryScreen(
            history = history,
            initialTab = historyInitialTab,
            onBack = { onNavigate("dashboard") },
            language = settings.language
        )
        "leaderboard" -> LeaderboardNavScreen(
            viewModel = viewModel,
            settings = settings,
            onNavigate = onNavigate,
            onOpenLeaderboardSettings = onOpenLeaderboardSettings
        )
    }
}
