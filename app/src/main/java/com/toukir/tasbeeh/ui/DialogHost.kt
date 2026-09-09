package com.toukir.tasbeeh.ui

import com.toukir.tasbeeh.ui.dialogs.*

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarInfo

@Composable
fun MainDialogHost(
    context: Context,
    viewModel: MainViewModel,
    settings: AppSettings,
    savedGoals: List<TasbeehGoal>,
    userName: String,
    userIsMale: Boolean,
    currentScreen: String,
    adhkarInfoToEdit: AdhkarInfo?,
    onDismissAdhkarInfo: () -> Unit,
    showNameInputDialog: Boolean,
    onDismissNameInput: () -> Unit,
    showLeaderboardSettingsDialog: Boolean,
    onDismissLeaderboardSettings: () -> Unit,
    showSettingsDialog: Boolean,
    onDismissSettings: () -> Unit,
    showAddGoalDialog: Boolean,
    onDismissAddGoal: () -> Unit,
    tasbeehToAddToGoal: TasbeehGoal?,
    onDismissAddToGoal: () -> Unit,
    goalToEdit: TasbeehGoal?,
    onDismissEditGoal: () -> Unit,
    managingGoalsType: Boolean?,
    onDismissManageGoals: () -> Unit,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ProfileAndInfoDialogs(
        viewModel = viewModel,
        userName = userName,
        userIsMale = userIsMale,
        adhkarInfoToEdit = adhkarInfoToEdit,
        onDismissAdhkarInfo = onDismissAdhkarInfo,
        showNameInputDialog = showNameInputDialog,
        onDismissNameInput = onDismissNameInput,
        showLeaderboardSettingsDialog = showLeaderboardSettingsDialog,
        onDismissLeaderboardSettings = onDismissLeaderboardSettings
    )

    if (showSettingsDialog) {
        SettingsDialogContainer(
            context = context,
            viewModel = viewModel,
            settings = settings,
            isLoggedIn = isLoggedIn,
            onLoginClick = onLoginClick,
            onLogoutClick = onLogoutClick,
            onDismiss = onDismissSettings
        )
    }

    GoalDialogHost(
        savedGoals = savedGoals,
        currentScreen = currentScreen,
        showAddGoalDialog = showAddGoalDialog,
        onDismissAddGoal = onDismissAddGoal,
        tasbeehToAddToGoal = tasbeehToAddToGoal,
        onDismissAddToGoal = onDismissAddToGoal,
        goalToEdit = goalToEdit,
        onDismissEditGoal = onDismissEditGoal,
        managingGoalsType = managingGoalsType,
        onDismissManageGoals = onDismissManageGoals,
        viewModel = viewModel
    )
}

@Composable
private fun ProfileAndInfoDialogs(
    viewModel: MainViewModel,
    userName: String,
    userIsMale: Boolean,
    adhkarInfoToEdit: AdhkarInfo?,
    onDismissAdhkarInfo: () -> Unit,
    showNameInputDialog: Boolean,
    onDismissNameInput: () -> Unit,
    showLeaderboardSettingsDialog: Boolean,
    onDismissLeaderboardSettings: () -> Unit
) {
    if (adhkarInfoToEdit != null) {
        EditTasbeehDetailsDialog(
            initialInfo = adhkarInfoToEdit,
            onDismiss = onDismissAdhkarInfo,
            onSave = { updatedInfo ->
                viewModel.saveAdhkarInfo(updatedInfo)
                onDismissAdhkarInfo()
            }
        )
    }

    if (showNameInputDialog) {
        NameInputDialog(
            currentName = userName,
            currentIsMale = userIsMale,
            onConfirm = { newName, newIsMale ->
                viewModel.saveUserProfile(newName, newIsMale)
                onDismissNameInput()
            },
            onDismiss = onDismissNameInput
        )
    }

    if (showLeaderboardSettingsDialog) {
        LeaderboardSettingsDialog(
            currentUsername = viewModel.leaderboardUsername.value,
            currentIsAnonymous = viewModel.isAnonymous.value,
            onConfirm = { name, anon ->
                viewModel.saveLeaderboardSettings(name, anon)
                onDismissLeaderboardSettings()
            },
            onDismiss = onDismissLeaderboardSettings
        )
    }
}

@Composable
private fun SettingsDialogContainer(
    context: Context,
    viewModel: MainViewModel,
    settings: AppSettings,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val isToastReminderEnabled = viewModel.isToastReminderEnabled.value
    val toastReminderText = viewModel.toastReminderText.value
    val toastReminderInterval = viewModel.toastReminderInterval.value
    val user = viewModel.firebaseManager.currentUser

    SettingsDialog(
        currentTheme = settings.theme,
        onThemeChange = { viewModel.saveTheme(it) },
        currentColorTheme = settings.colorTheme,
        onColorThemeChange = { viewModel.saveColorTheme(it) },
        isSoundEnabled = settings.isSoundEnabled,
        onSoundEnabledChange = { viewModel.saveSoundEnabled(it) },
        isVibrateTapEnabled = settings.isVibrateTapEnabled,
        onVibrateTapChange = { viewModel.saveVibrateTapEnabled(it) },
        isVibrate100Enabled = settings.isVibrate100Enabled,
        onVibrate100Change = { viewModel.saveVibrate100Enabled(it) },
        currentLanguage = settings.language,
        onLanguageChange = { viewModel.saveLanguage(it) },
        showCounterCircle = settings.showCounterCircle,
        onShowCounterCircleChange = { viewModel.saveShowCounterCircleEnabled(it) },
        isToastReminderEnabled = isToastReminderEnabled,
        onToastReminderEnabledChange = { viewModel.saveToastReminderSettings(it, toastReminderText, toastReminderInterval) },
        toastReminderText = toastReminderText,
        onToastReminderTextChange = { viewModel.saveToastReminderSettings(isToastReminderEnabled, it, toastReminderInterval) },
        toastReminderInterval = toastReminderInterval,
        onToastReminderIntervalChange = { viewModel.saveToastReminderSettings(isToastReminderEnabled, toastReminderText, it) },
        onBackup = { uri -> handleBackup(context, viewModel, uri) },
        onRestore = { uri -> handleRestore(context, viewModel, uri) },
        onDismiss = onDismiss,
        context = context,
        isLoggedIn = isLoggedIn,
        isRestoring = viewModel.isRestoring.value,
        userEmail = user?.email,
        userDisplayName = user?.displayName,
        userPhotoUrl = user?.photoUrl?.toString(),
        onLoginClick = onLoginClick,
        onLogoutClick = onLogoutClick,
        isLeaderboardEnabled = viewModel.isLeaderboardEnabled.value,
        onLeaderboardEnabledChange = { viewModel.toggleLeaderboard(it) }
    )
}

private fun handleBackup(context: Context, viewModel: MainViewModel, uri: android.net.Uri) {
    viewModel.backupData(uri) { success ->
        val msg = if (success) R.string.toast_backup_saved else R.string.toast_backup_failed
        Toast.makeText(context, context.getString(msg), Toast.LENGTH_SHORT).show()
    }
}

private fun handleRestore(context: Context, viewModel: MainViewModel, uri: android.net.Uri) {
    viewModel.restoreData(uri) { success ->
        val msg = if (success) R.string.toast_restore_success else R.string.toast_restore_failed
        Toast.makeText(context, context.getString(msg), Toast.LENGTH_SHORT).show()
    }
}
