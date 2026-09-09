package com.toukir.tasbeeh.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarInfo

class TasbeehAppState(
    val viewModel: MainViewModel
) {
    var currentScreen by mutableStateOf("home")
    var historyInitialTab by mutableIntStateOf(0)
    var selectedGoalId by mutableStateOf<Int?>(null)
    var selectedTasbeehDetailId by mutableStateOf<Int?>(null)

    var goalToEdit by mutableStateOf<TasbeehGoal?>(null)
    var tasbeehToAddToGoal by mutableStateOf<TasbeehGoal?>(null)
    var showAddGoalDialog by mutableStateOf(false)
    var managingGoalsType by mutableStateOf<Boolean?>(null)
    var showSettingsDialog by mutableStateOf(false)
    var showNameInputDialog by mutableStateOf(false)
    var showLeaderboardSettingsDialog by mutableStateOf(false)
    var adhkarInfoToEdit by mutableStateOf<AdhkarInfo?>(null)
    var isLoggedIn by mutableStateOf(viewModel.firebaseManager.isLoggedIn)

    val isFullscreenOverlay: Boolean
        get() = selectedGoalId != null || selectedTasbeehDetailId != null

    fun handleBack() {
        when {
            selectedGoalId != null -> {
                selectedGoalId = null
                viewModel.syncToCloud()
            }
            selectedTasbeehDetailId != null -> selectedTasbeehDetailId = null
            currentScreen in listOf("statistics", "history", "leaderboard") -> currentScreen = "dashboard"
            else -> currentScreen = "home"
        }
    }

    val isBackEnabled: Boolean
        get() = selectedGoalId != null || selectedTasbeehDetailId != null || currentScreen != "home"
}

@Composable
fun rememberTasbeehAppState(viewModel: MainViewModel): TasbeehAppState {
    return remember(viewModel) { TasbeehAppState(viewModel) }
}
