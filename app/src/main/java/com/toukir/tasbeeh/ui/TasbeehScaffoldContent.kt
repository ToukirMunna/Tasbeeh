package com.toukir.tasbeeh.ui

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.data.AdhkarInfo

@Composable
fun TasbeehScaffoldLayout(
    appState: TasbeehAppState,
    viewModel: MainViewModel,
    settings: AppSettings,
    context: Context,
    savedGoals: List<TasbeehGoal>,
    combinedHistory: List<TasbeehHistory>,
    customDetails: Map<String, AdhkarInfo>,
    userName: String,
    userIsMale: Boolean,
    currentStreak: Int,
    onLoginClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!appState.isFullscreenOverlay) {
                TasbeehBottomNavBar(
                    currentScreen = appState.currentScreen,
                    onNavigate = {
                        appState.currentScreen = it
                        appState.selectedGoalId = null
                        appState.selectedTasbeehDetailId = null
                    }
                )
            }
        },
        floatingActionButton = {
            if (appState.currentScreen == "tasbeehs" && !appState.isFullscreenOverlay) {
                TasbeehFloatingActionButton(onClick = { appState.showAddGoalDialog = true })
            }
        }
    ) { innerPadding ->
        TasbeehScaffoldInner(
            innerPadding = innerPadding,
            appState = appState,
            viewModel = viewModel,
            settings = settings,
            context = context,
            savedGoals = savedGoals,
            combinedHistory = combinedHistory,
            customDetails = customDetails,
            userName = userName,
            userIsMale = userIsMale,
            currentStreak = currentStreak,
            onLoginClick = onLoginClick
        )
    }
}

@Composable
private fun TasbeehScaffoldInner(
    innerPadding: PaddingValues,
    appState: TasbeehAppState,
    viewModel: MainViewModel,
    settings: AppSettings,
    context: Context,
    savedGoals: List<TasbeehGoal>,
    combinedHistory: List<TasbeehHistory>,
    customDetails: Map<String, AdhkarInfo>,
    userName: String,
    userIsMale: Boolean,
    currentStreak: Int,
    onLoginClick: () -> Unit
) {
    TasbeehNavSection(
        innerPadding = innerPadding,
        appState = appState,
        viewModel = viewModel,
        settings = settings,
        savedGoals = savedGoals,
        combinedHistory = combinedHistory,
        customDetails = customDetails,
        userName = userName,
        userIsMale = userIsMale,
        currentStreak = currentStreak
    )

    TasbeehDialogSection(
        appState = appState,
        viewModel = viewModel,
        settings = settings,
        context = context,
        savedGoals = savedGoals,
        userName = userName,
        userIsMale = userIsMale,
        onLoginClick = onLoginClick
    )
}

@Composable
private fun TasbeehNavSection(
    innerPadding: PaddingValues,
    appState: TasbeehAppState,
    viewModel: MainViewModel,
    settings: AppSettings,
    savedGoals: List<TasbeehGoal>,
    combinedHistory: List<TasbeehHistory>,
    customDetails: Map<String, AdhkarInfo>,
    userName: String,
    userIsMale: Boolean,
    currentStreak: Int
) {
    AppNavHost(
        currentScreen = appState.currentScreen,
        selectedGoalId = appState.selectedGoalId,
        selectedTasbeehDetailId = appState.selectedTasbeehDetailId,
        savedGoals = savedGoals,
        combinedHistory = combinedHistory,
        customDetails = customDetails,
        userName = userName,
        userIsMale = userIsMale,
        currentStreak = currentStreak,
        historyInitialTab = appState.historyInitialTab,
        settings = settings,
        viewModel = viewModel,
        bottomPadding = innerPadding.calculateBottomPadding(),
        onNavigate = { appState.currentScreen = it },
        onSelectGoal = { appState.selectedGoalId = it },
        onSelectDetail = { appState.selectedTasbeehDetailId = it },
        onEditGoal = { appState.goalToEdit = it },
        onAddToGoal = { appState.tasbeehToAddToGoal = it },
        onManageGoals = { appState.managingGoalsType = it },
        onEditAdhkarInfo = { appState.adhkarInfoToEdit = it },
        onOpenSettings = { appState.showSettingsDialog = true },
        onOpenNameInput = { appState.showNameInputDialog = true },
        onOpenLeaderboardSettings = { appState.showLeaderboardSettingsDialog = true },
        onSetHistoryTab = { appState.historyInitialTab = it }
    )
}

@Composable
private fun TasbeehDialogSection(
    appState: TasbeehAppState,
    viewModel: MainViewModel,
    settings: AppSettings,
    context: Context,
    savedGoals: List<TasbeehGoal>,
    userName: String,
    userIsMale: Boolean,
    onLoginClick: () -> Unit
) {
    MainDialogHost(
        context = context,
        viewModel = viewModel,
        settings = settings,
        savedGoals = savedGoals,
        userName = userName,
        userIsMale = userIsMale,
        currentScreen = appState.currentScreen,
        adhkarInfoToEdit = appState.adhkarInfoToEdit,
        onDismissAdhkarInfo = { appState.adhkarInfoToEdit = null },
        showNameInputDialog = appState.showNameInputDialog,
        onDismissNameInput = { appState.showNameInputDialog = false },
        showLeaderboardSettingsDialog = appState.showLeaderboardSettingsDialog,
        onDismissLeaderboardSettings = { appState.showLeaderboardSettingsDialog = false },
        showSettingsDialog = appState.showSettingsDialog,
        onDismissSettings = { appState.showSettingsDialog = false },
        showAddGoalDialog = appState.showAddGoalDialog,
        onDismissAddGoal = { appState.showAddGoalDialog = false },
        tasbeehToAddToGoal = appState.tasbeehToAddToGoal,
        onDismissAddToGoal = { appState.tasbeehToAddToGoal = null },
        goalToEdit = appState.goalToEdit,
        onDismissEditGoal = { appState.goalToEdit = null },
        managingGoalsType = appState.managingGoalsType,
        onDismissManageGoals = { appState.managingGoalsType = null },
        isLoggedIn = appState.isLoggedIn,
        onLoginClick = onLoginClick,
        onLogoutClick = {
            viewModel.firebaseManager.signOut()
            appState.isLoggedIn = false
        }
    )
}
