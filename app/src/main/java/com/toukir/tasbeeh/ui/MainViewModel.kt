package com.toukir.tasbeeh.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarInfo
import com.toukir.tasbeeh.data.FirebaseManager
import com.toukir.tasbeeh.data.LeaderboardEntry
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.data.TasbeehRepository
import com.toukir.tasbeeh.data.repository.SettingsDataStore
import com.toukir.tasbeeh.ui.theme.AppColorTheme
import com.toukir.tasbeeh.ui.theme.AppTheme
import com.toukir.tasbeeh.ui.theme.GradientStyle
import com.toukir.tasbeeh.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

private fun List<TasbeehGoal>.todayDetailsByName(): Map<String, Int> {
    return groupBy { it.name }
        .mapValues { (_, goals) -> goals.maxOf { it.dailyCount } }
        .filterValues { it > 0 }
}

class MainViewModel(
    private val context: Context,
    private val repository: TasbeehRepository
) : ViewModel() {
    val firebaseManager = FirebaseManager(repository)
    private val settingsDataStore = SettingsDataStore(context)

    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus

    private val _isRestoring = MutableStateFlow(false)
    val isRestoring: StateFlow<Boolean> = _isRestoring

    val settings: StateFlow<AppSettings> = settingsDataStore.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    val savedGoals = repository.goalsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userName = repository.userNameFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val userIsMale = repository.userIsMaleFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val customDetails = repository.customDetailsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val leaderboardUsername = repository.leaderboardUsernameFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val isAnonymous = repository.isAnonymousFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val isLeaderboardEnabled = repository.isLeaderboardEnabledFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isToastReminderEnabled = repository.toastReminderEnabledFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val toastReminderText = repository.toastReminderTextFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Time for Dhikr")
    val toastReminderInterval = repository.toastReminderIntervalFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15)

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    private val _leaderboardData = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardData: StateFlow<List<LeaderboardEntry>> = _leaderboardData

    private val _isLeaderboardLoading = MutableStateFlow(false)
    val isLeaderboardLoading: StateFlow<Boolean> = _isLeaderboardLoading

    private val _leaderboardError = MutableStateFlow<String?>(null)
    val leaderboardError: StateFlow<String?> = _leaderboardError

    val combinedHistory: StateFlow<List<TasbeehHistory>> = combine(repository.historyFlow, savedGoals) { history, goals ->
        val today = LocalDate.now().toString()
        val todayDetails = goals.todayDetailsByName()
        val todayTotal = todayDetails.values.sum()
        if (todayTotal >= 0) {
            val todayEntry = TasbeehHistory(today, todayTotal, todayDetails)
            history.filter { it.date != today } + todayEntry
        } else {
            history
        }
    }.flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.runDataMigrationIfNeeded()
            updateStreak()
        }
    }

    fun updateStreak() {
        viewModelScope.launch {
            _currentStreak.value = repository.calculateCurrentStreak()
        }
    }

    fun incrementGoal(goal: TasbeehGoal) {
        viewModelScope.launch {
            val currentList = savedGoals.value.toMutableList()
            val updatedList = currentList.map {
                if (it.name == goal.name) {
                    it.copy(
                        currentCount = it.currentCount + 1,
                        dailyCount = it.dailyCount + 1,
                        totalCount = it.totalCount + 1
                    )
                } else it
            }
            repository.saveGoals(updatedList)
            updateStreak()
        }
    }

    fun checkAndResetDailyCounts() {
        viewModelScope.launch {
            repository.checkAndResetDailyCounts()
            updateStreak()
        }
    }

    fun resetTodayCounts() {
        viewModelScope.launch {
            repository.resetTodayCounts()
            updateStreak()
        }
    }

    fun saveGoals(goals: List<TasbeehGoal>) {
        viewModelScope.launch {
            repository.saveGoals(goals)
            updateStreak()
        }
    }

    fun saveUserProfile(name: String, isMale: Boolean) {
        viewModelScope.launch { repository.saveUserProfile(name, isMale) }
    }

    fun saveLeaderboardSettings(username: String, isAnonymous: Boolean) {
        viewModelScope.launch {
            repository.saveLeaderboardSettings(username, isAnonymous)
            if (firebaseManager.isLoggedIn) firebaseManager.syncToLeaderboard()
        }
    }

    fun toggleLeaderboard(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveLeaderboardEnabled(enabled)
            if (enabled) fetchLeaderboard("daily") else _leaderboardData.value = emptyList()
        }
    }

    fun fetchLeaderboard(period: String) {
        viewModelScope.launch {
            if (!isLeaderboardEnabled.value) {
                _leaderboardData.value = emptyList()
                return@launch
            }
            _isLeaderboardLoading.value = true
            _leaderboardError.value = null
            if (firebaseManager.isLoggedIn) firebaseManager.syncToLeaderboard()
            try {
                val data = firebaseManager.getLeaderboard(period)
                _leaderboardData.value = data
                if (data.isEmpty()) _leaderboardError.value = "No data found"
            } catch (e: Exception) {
                _leaderboardError.value = e.message ?: "Unknown error"
            } finally {
                _isLeaderboardLoading.value = false
            }
        }
    }

    fun saveAdhkarInfo(info: AdhkarInfo) {
        viewModelScope.launch { repository.saveAdhkarInfo(info) }
    }

    fun saveToastReminderSettings(enabled: Boolean, text: String, interval: Int) {
        viewModelScope.launch { repository.saveToastReminderSettings(enabled, text, interval) }
    }

    fun updateSetting(action: suspend (Context) -> Unit) {
        viewModelScope.launch { action(context) }
    }

    fun saveTheme(theme: AppTheme) { viewModelScope.launch { settingsDataStore.saveTheme(theme) } }
    fun saveColorTheme(colorTheme: AppColorTheme) { viewModelScope.launch { settingsDataStore.saveColorTheme(colorTheme) } }
    fun saveGradient(gradient: GradientStyle) { viewModelScope.launch { settingsDataStore.saveGradient(gradient) } }
    fun saveThickness(thickness: Float) { viewModelScope.launch { settingsDataStore.saveThickness(thickness) } }
    fun saveSoundEnabled(enabled: Boolean) { viewModelScope.launch { settingsDataStore.saveSoundEnabled(enabled) } }
    fun saveVibrateTapEnabled(enabled: Boolean) { viewModelScope.launch { settingsDataStore.saveVibrateTapEnabled(enabled) } }
    fun saveVibrate100Enabled(enabled: Boolean) { viewModelScope.launch { settingsDataStore.saveVibrate100Enabled(enabled) } }
    fun saveLanguage(language: String) { viewModelScope.launch { settingsDataStore.saveLanguage(language) } }
    fun saveShowCounterCircleEnabled(enabled: Boolean) { viewModelScope.launch { settingsDataStore.saveShowCounterCircleEnabled(enabled) } }

    fun backupData(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.saveBackupToUri(uri)
            onResult(success)
        }
    }

    fun restoreData(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.restoreFromBackup(uri)
            if (success) updateStreak()
            onResult(success)
        }
    }

    fun syncToCloud(onResult: (Boolean) -> Unit = {}) {
        if (!firebaseManager.isLoggedIn || !NetworkUtils.isInternetAvailable(context)) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.SYNCING
            val success = firebaseManager.syncToCloud()
            if (success) fetchLeaderboard("daily")
            _syncStatus.value = if (success) SyncStatus.SYNCED else SyncStatus.ERROR
            onResult(success)
            if (success) {
                delay(500)
                _syncStatus.value = SyncStatus.IDLE
            }
        }
    }

    fun restoreFromCloud(onResult: (Boolean) -> Unit) {
        if (!NetworkUtils.isInternetAvailable(context)) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            _isRestoring.value = true
            val success = firebaseManager.restoreFromCloud()
            if (success) updateStreak()
            _isRestoring.value = false
            onResult(success)
        }
    }
}
