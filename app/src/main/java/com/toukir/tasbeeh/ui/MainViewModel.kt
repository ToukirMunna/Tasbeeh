package com.toukir.tasbeeh.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarInfo
import com.toukir.tasbeeh.data.FirebaseManager
import com.toukir.tasbeeh.data.LeaderboardEntry
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.data.TasbeehRepository
import com.toukir.tasbeeh.data.settingsDataStore
import com.toukir.tasbeeh.ui.theme.AppTheme
import com.toukir.tasbeeh.ui.theme.GradientStyle
import com.toukir.tasbeeh.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

enum class SyncStatus { IDLE, SYNCING, SYNCED, ERROR }

private val THEME_KEY = intPreferencesKey("app_theme")
private val GRADIENT_KEY = stringPreferencesKey("app_gradient")
private val THICKNESS_KEY = floatPreferencesKey("app_thickness")
private val SOUND_KEY = booleanPreferencesKey("app_sound")
private val VIBRATE_TAP_KEY = booleanPreferencesKey("app_vibrate_tap")
private val VIBRATE_100_KEY = booleanPreferencesKey("app_vibrate_100")
private val LANGUAGE_KEY = stringPreferencesKey("app_language")
private val SHOW_COUNTER_CIRCLE_KEY = booleanPreferencesKey("app_show_counter_circle")

data class AppSettings(
    val theme: AppTheme = AppTheme.Light,
    val gradient: GradientStyle = GradientStyle.Sunset,
    val thickness: Float = 20f,
    val isSoundEnabled: Boolean = true,
    val isVibrateTapEnabled: Boolean = false,
    val isVibrate100Enabled: Boolean = true,
    val language: String = "en",
    val showCounterCircle: Boolean = true
)

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

    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus

    private val _isRestoring = MutableStateFlow(false)
    val isRestoring: StateFlow<Boolean> = _isRestoring

    private val initialSettings = runBlocking {
        try {
            val prefs = context.settingsDataStore.data.first()
            AppSettings(
                theme = AppTheme.entries.getOrElse(prefs[THEME_KEY] ?: 0) { AppTheme.Light },
                gradient = try { GradientStyle.valueOf(prefs[GRADIENT_KEY] ?: GradientStyle.Sunset.name) } catch (e: Exception) { GradientStyle.Sunset },
                thickness = prefs[THICKNESS_KEY] ?: 20f,
                isSoundEnabled = prefs[SOUND_KEY] ?: true,
                isVibrateTapEnabled = prefs[VIBRATE_TAP_KEY] ?: false,
                isVibrate100Enabled = prefs[VIBRATE_100_KEY] ?: true,
                language = prefs[LANGUAGE_KEY] ?: "en",
                showCounterCircle = prefs[SHOW_COUNTER_CIRCLE_KEY] ?: true
            )
        } catch (e: Exception) {
            AppSettings()
        }
    }

    val settings: StateFlow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            theme = AppTheme.entries.getOrElse(prefs[THEME_KEY] ?: 0) { AppTheme.Light },
            gradient = try { GradientStyle.valueOf(prefs[GRADIENT_KEY] ?: GradientStyle.Sunset.name) } catch (e: Exception) { GradientStyle.Sunset },
            thickness = prefs[THICKNESS_KEY] ?: 20f,
            isSoundEnabled = prefs[SOUND_KEY] ?: true,
            isVibrateTapEnabled = prefs[VIBRATE_TAP_KEY] ?: false,
            isVibrate100Enabled = prefs[VIBRATE_100_KEY] ?: true,
            language = prefs[LANGUAGE_KEY] ?: "en",
            showCounterCircle = prefs[SHOW_COUNTER_CIRCLE_KEY] ?: true
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialSettings)

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
    }.flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
        }
    }

    fun incrementGoal(goal: TasbeehGoal) {
        viewModelScope.launch {
            val currentList = savedGoals.value.toMutableList()
            // Increment ALL goals with the same name to keep progress in sync
            val updatedList = currentList.map { 
                if (it.name == goal.name) {
                    it.copy(
                        currentCount = it.currentCount + 1,
                        dailyCount = it.dailyCount + 1,
                        totalCount = it.totalCount + 1
                    )
                } else {
                    it
                }
            }
            repository.saveGoals(updatedList)
            
            // Mark sync as idle/pending since local data changed
            if (_syncStatus.value == SyncStatus.SYNCED) {
                _syncStatus.value = SyncStatus.IDLE
            }
            
            val intent = Intent(context, TasbeehWidget::class.java).apply {
                action = TasbeehWidget.ACTION_REFRESH
            }
            context.sendBroadcast(intent)
        }
    }

    fun updateGoal(updatedGoal: TasbeehGoal) {
        viewModelScope.launch {
            val currentList = savedGoals.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == updatedGoal.id }
            if (index != -1) {
                currentList[index] = updatedGoal
                repository.saveGoals(currentList)
            }
        }
    }

    fun saveUserProfile(name: String, isMale: Boolean) {
        viewModelScope.launch {
            repository.saveUserProfile(name, isMale)
        }
    }

    fun saveLeaderboardSettings(username: String, isAnonymous: Boolean) {
        viewModelScope.launch {
            repository.saveLeaderboardSettings(username, isAnonymous)
            syncToCloud()
        }
    }

    fun toggleLeaderboard(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveLeaderboardEnabled(enabled)
            if (enabled) {
                firebaseManager.syncToLeaderboard()
                fetchLeaderboard("daily")
            } else {
                firebaseManager.removeFromLeaderboard()
                _leaderboardData.value = emptyList()
            }
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
            
            // Force a sync TO the leaderboard before fetching, to ensure current user exists
            if (firebaseManager.isLoggedIn) {
                firebaseManager.syncToLeaderboard()
            }

            try {
                val data = firebaseManager.getLeaderboard(period)
                _leaderboardData.value = data
                if (data.isEmpty()) {
                    _leaderboardError.value = "No data found"
                }
            } catch (e: Exception) {
                _leaderboardError.value = e.message ?: "Unknown error"
            } finally {
                _isLeaderboardLoading.value = false
            }
        }
    }

    fun saveAdhkarInfo(info: AdhkarInfo) {
        viewModelScope.launch {
            repository.saveAdhkarInfo(info)
        }
    }

    fun saveToastReminderSettings(enabled: Boolean, text: String, interval: Int) {
        viewModelScope.launch {
            repository.saveToastReminderSettings(enabled, text, interval)
        }
    }

    fun updateSetting(action: suspend (Context) -> Unit) {
        viewModelScope.launch {
            action(context)
        }
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.settingsDataStore.edit { it[THEME_KEY] = theme.ordinal }
    }

    suspend fun saveGradient(gradient: GradientStyle) {
        context.settingsDataStore.edit { it[GRADIENT_KEY] = gradient.name }
    }

    suspend fun saveThickness(thickness: Float) {
        context.settingsDataStore.edit { it[THICKNESS_KEY] = thickness }
    }

    suspend fun saveSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SOUND_KEY] = enabled }
    }

    suspend fun saveVibrateTapEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[VIBRATE_TAP_KEY] = enabled }
    }

    suspend fun saveVibrate100Enabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[VIBRATE_100_KEY] = enabled }
    }

    suspend fun saveLanguage(language: String) {
        context.settingsDataStore.edit { it[LANGUAGE_KEY] = language }
    }

    suspend fun saveShowCounterCircleEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SHOW_COUNTER_CIRCLE_KEY] = enabled }
    }

    fun backupData(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.saveBackupToUri(uri)
            onResult(success)
        }
    }

    fun restoreData(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.restoreFromBackup(uri)
            if (success) {
                updateStreak()
            }
            onResult(success)
        }
    }

    fun syncToCloud(onResult: (Boolean) -> Unit = {}) {
        if (!firebaseManager.isLoggedIn) {
            onResult(false)
            return
        }
        
        // Only trigger sync if internet is available
        if (!NetworkUtils.isInternetAvailable(context)) {
            onResult(false)
            return
        }
        
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.SYNCING
            val success = firebaseManager.syncToCloud()
            
            // Force a leaderboard refresh after a successful sync
            if (success) {
                fetchLeaderboard("daily")
            }

            _syncStatus.value = if (success) SyncStatus.SYNCED else SyncStatus.ERROR
            onResult(success)
            
            // Revert to IDLE after a short delay if successful to "hide" the completion state
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
            if (success) {
                updateStreak()
            }
            _isRestoring.value = false
            onResult(success)
        }
    }
}
