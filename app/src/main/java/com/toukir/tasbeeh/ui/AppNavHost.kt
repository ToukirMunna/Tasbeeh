package com.toukir.tasbeeh.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.data.AdhkarInfo

@Composable
fun AppNavHost(
    currentScreen: String,
    selectedGoalId: Int?,
    selectedTasbeehDetailId: Int?,
    savedGoals: List<TasbeehGoal>,
    combinedHistory: List<TasbeehHistory>,
    customDetails: Map<String, AdhkarInfo>,
    userName: String,
    userIsMale: Boolean,
    currentStreak: Int,
    historyInitialTab: Int,
    settings: AppSettings,
    viewModel: MainViewModel,
    bottomPadding: Dp,
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
    AnimatedContent(
        targetState = Triple(currentScreen, selectedGoalId, selectedTasbeehDetailId),
        transitionSpec = appNavTransitionSpec(),
        label = "ScreenTransition"
    ) { (screen, goalId, detailId) ->
        val goal = goalId?.let { id -> savedGoals.firstOrNull { it.id == id } }
        val detail = detailId?.let { id -> savedGoals.firstOrNull { it.id == id } }

        Box(modifier = Modifier.padding(bottom = bottomPadding)) {
            AppNavDestinationRouter(
                screen = screen,
                goal = goal,
                detail = detail,
                viewModel = viewModel,
                settings = settings,
                savedGoals = savedGoals,
                combinedHistory = combinedHistory,
                customDetails = customDetails,
                userName = userName,
                userIsMale = userIsMale,
                currentStreak = currentStreak,
                historyInitialTab = historyInitialTab,
                onNavigate = onNavigate,
                onSelectGoal = onSelectGoal,
                onSelectDetail = onSelectDetail,
                onEditGoal = onEditGoal,
                onAddToGoal = onAddToGoal,
                onManageGoals = onManageGoals,
                onEditAdhkarInfo = onEditAdhkarInfo,
                onOpenSettings = onOpenSettings,
                onOpenNameInput = onOpenNameInput,
                onOpenLeaderboardSettings = onOpenLeaderboardSettings,
                onSetHistoryTab = onSetHistoryTab
            )
        }
    }
}
