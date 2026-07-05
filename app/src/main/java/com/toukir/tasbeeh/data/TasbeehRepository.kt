package com.toukir.tasbeeh.data

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.TasbeehGoal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// Backup Data Classes (Versioned)
data class UserProfileBackup(
    @SerializedName("name") val name: String,
    @SerializedName("isMale") val isMale: Boolean
)

data class SettingsBackup(
    @SerializedName("theme") val theme: Int,
    @SerializedName("gradient") val gradient: String,
    @SerializedName("thickness") val thickness: Float,
    @SerializedName("isSoundEnabled") val isSoundEnabled: Boolean,
    @SerializedName("isVibrateTapEnabled") val isVibrateTapEnabled: Boolean,
    @SerializedName("isVibrate100Enabled") val isVibrate100Enabled: Boolean,
    @SerializedName("language") val language: String,
    @SerializedName("showCounterCircle") val showCounterCircle: Boolean,
    @SerializedName("toastReminderEnabled") val toastReminderEnabled: Boolean,
    @SerializedName("toastReminderText") val toastReminderText: String,
    @SerializedName("toastReminderInterval") val toastReminderInterval: Int
)

data class BackupData(
    @SerializedName("goals") val goals: List<TasbeehGoal>,
    @SerializedName("history") val history: List<TasbeehHistory>,
    @SerializedName("earnedAchievements") val earnedAchievements: Map<String, Int>? = null,
    @SerializedName("lastResetDate") val lastResetDate: String? = null,
    @SerializedName("userProfile") val userProfile: UserProfileBackup? = null,
    @SerializedName("customDetails") val customDetails: Map<String, AdhkarInfo>? = null,
    @SerializedName("settings") val settings: SettingsBackup? = null,
    @SerializedName("leaderboardUsername") val leaderboardUsername: String? = null,
    @SerializedName("isAnonymous") val isAnonymous: Boolean? = null,
    @SerializedName("isLeaderboardEnabled") val isLeaderboardEnabled: Boolean? = null,
    @SerializedName("version") val version: Int = 3,
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

class TasbeehRepository(private val context: Context) {
    private val gson = Gson()
    
    // Tasbeeh Data Keys
    private val GOALS_KEY = stringPreferencesKey("tasbeeh_goals_v5")
    private val HISTORY_KEY = stringPreferencesKey("tasbeeh_history")
    private val EARNED_ACHIEVEMENTS_COUNTS_KEY = stringPreferencesKey("earned_achievements_counts")
    private val LAST_RESET_DATE_KEY = stringPreferencesKey("last_reset_date")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_IS_MALE_KEY = booleanPreferencesKey("user_is_male")
    private val CUSTOM_DETAILS_KEY = stringPreferencesKey("custom_adhkar_details")
    private val SYNC_V1_COMPLETED_KEY = booleanPreferencesKey("sync_v1_completed")
    private val TOAST_REMINDER_ENABLED_KEY = booleanPreferencesKey("toast_reminder_enabled")
    private val TOAST_REMINDER_TEXT_KEY = stringPreferencesKey("toast_reminder_text")
    private val TOAST_REMINDER_INTERVAL_KEY = intPreferencesKey("toast_reminder_interval")
    private val LEADERBOARD_USERNAME_KEY = stringPreferencesKey("leaderboard_username")
    private val IS_ANONYMOUS_KEY = booleanPreferencesKey("is_anonymous")
    private val IS_LEADERBOARD_ENABLED_KEY = booleanPreferencesKey("is_leaderboard_enabled")

    // Settings Keys
    private val SETTINGS_THEME_KEY = intPreferencesKey("app_theme")
    private val SETTINGS_GRADIENT_KEY = stringPreferencesKey("app_gradient")
    private val SETTINGS_THICKNESS_KEY = floatPreferencesKey("app_thickness")
    private val SETTINGS_SOUND_KEY = booleanPreferencesKey("app_sound")
    private val SETTINGS_VIBRATE_TAP_KEY = booleanPreferencesKey("app_vibrate_tap")
    private val SETTINGS_VIBRATE_100_KEY = booleanPreferencesKey("app_vibrate_100")
    private val SETTINGS_LANGUAGE_KEY = stringPreferencesKey("app_language")
    private val SETTINGS_SHOW_COUNTER_CIRCLE_KEY = booleanPreferencesKey("app_show_counter_circle")

    private fun getDefaultGoals(): List<TasbeehGoal> {
        return AdhkarLibrary.adhkarList.mapIndexed { index, info ->
            TasbeehGoal(
                id = index + 1,
                name = info.name,
                targetCount = 100,
                currentCount = 0,
                totalCount = 0,
                isGoal = index < 6
            )
        }
    }

    private fun parseGoals(json: String?): List<TasbeehGoal> {
        val baseGoals = if (json != null) {
            val type = object : TypeToken<List<TasbeehGoal>>() {}.type
            try {
                gson.fromJson<List<TasbeehGoal>>(json, type)
            } catch (e: Exception) {
                getDefaultGoals()
            }
        } else {
            getDefaultGoals()
        }

        // Ensure all items currently in AdhkarLibrary are present in the list
        val libraryNames = AdhkarLibrary.adhkarList.map { it.name }.toSet()
        val existingNames = baseGoals.map { it.name }.toSet()
        val missingNames = libraryNames - existingNames

        val combinedGoals = if (missingNames.isNotEmpty()) {
            val mutable = baseGoals.toMutableList()
            var nextId = (mutable.maxOfOrNull { it.id } ?: 0) + 1
            missingNames.forEach { name ->
                mutable.add(
                    TasbeehGoal(
                        id = nextId++,
                        name = name,
                        targetCount = 100,
                        currentCount = 0,
                        totalCount = 0,
                        isGoal = false
                    )
                )
            }
            mutable
        } else {
            baseGoals
        }

        return combinedGoals.map { goal ->
            @Suppress("SENSELESS_COMPARISON")
            val duration = if (goal.duration == null) GoalDuration.DAILY else goal.duration
            val lastReset = if (goal.lastResetDate == 0L) System.currentTimeMillis() else goal.lastResetDate
            
            // Migrate: For DAILY goals, dailyCount should match currentCount if it's 0
            val dailyCount = if (duration == GoalDuration.DAILY && goal.dailyCount == 0) {
                goal.currentCount
            } else {
                goal.dailyCount
            }
            
            goal.copy(duration = duration, lastResetDate = lastReset, dailyCount = dailyCount)
        }
    }

    private fun List<TasbeehGoal>.distinctDailyTotal(): Int {
        return groupBy { it.name }.values.sumOf { goals -> goals.maxOf { it.dailyCount } }
    }

    private fun List<TasbeehGoal>.distinctTotalCount(): Int {
        return groupBy { it.name }.values.sumOf { goals -> goals.maxOf { it.totalCount } }
    }

    private fun List<TasbeehGoal>.dailyDetailsByName(): Map<String, Int> {
        return groupBy { it.name }
            .mapValues { (_, goals) -> goals.maxOf { it.dailyCount } }
            .filterValues { it > 0 }
    }

    val goalsFlow: Flow<List<TasbeehGoal>> = context.tasbeehDataStore.data
        .map { preferences -> parseGoals(preferences[GOALS_KEY]) }
        
    val historyFlow: Flow<List<TasbeehHistory>> = context.tasbeehDataStore.data
        .map { preferences ->
            val json = preferences[HISTORY_KEY]
            if (json != null) {
                val type = object : TypeToken<List<TasbeehHistory>>() {}.type
                gson.fromJson(json, type)
            } else {
                emptyList()
            }
        }

    val earnedAchievementsCountsFlow: Flow<Map<String, Int>> = context.tasbeehDataStore.data
        .map { preferences ->
            val json = preferences[EARNED_ACHIEVEMENTS_COUNTS_KEY]
            if (json != null) {
                val type = object : TypeToken<Map<String, Int>>() {}.type
                gson.fromJson(json, type)
            } else {
                 emptyMap()
            }
        }

    val userNameFlow: Flow<String> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }

    val userIsMaleFlow: Flow<Boolean> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[USER_IS_MALE_KEY] ?: true
        }

    val customDetailsFlow: Flow<Map<String, AdhkarInfo>> = context.tasbeehDataStore.data
        .map { preferences ->
            val json = preferences[CUSTOM_DETAILS_KEY]
            if (json != null) {
                val type = object : TypeToken<Map<String, AdhkarInfo>>() {}.type
                gson.fromJson(json, type)
            } else {
                emptyMap()
            }
        }
    
    val toastReminderEnabledFlow: Flow<Boolean> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[TOAST_REMINDER_ENABLED_KEY] ?: false
        }
    
    val toastReminderTextFlow: Flow<String> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[TOAST_REMINDER_TEXT_KEY] ?: "Time for Dhikr"
        }

    val toastReminderIntervalFlow: Flow<Int> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[TOAST_REMINDER_INTERVAL_KEY] ?: 15
        }

    val leaderboardUsernameFlow: Flow<String> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[LEADERBOARD_USERNAME_KEY] ?: ""
        }

    val isAnonymousFlow: Flow<Boolean> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[IS_ANONYMOUS_KEY] ?: false
        }

    val isLeaderboardEnabledFlow: Flow<Boolean> = context.tasbeehDataStore.data
        .map { preferences ->
            preferences[IS_LEADERBOARD_ENABLED_KEY] ?: false
        }

    suspend fun saveToastReminderSettings(enabled: Boolean, text: String, interval: Int) {
        context.tasbeehDataStore.edit { preferences ->
            preferences[TOAST_REMINDER_ENABLED_KEY] = enabled
            preferences[TOAST_REMINDER_TEXT_KEY] = text
            preferences[TOAST_REMINDER_INTERVAL_KEY] = interval
        }
    }

    suspend fun saveAdhkarInfo(info: AdhkarInfo) {
        val current = customDetailsFlow.first().toMutableMap()
        current[info.name] = info
        val json = gson.toJson(current)
        context.tasbeehDataStore.edit { preferences ->
            preferences[CUSTOM_DETAILS_KEY] = json
        }
    }

    suspend fun saveUserProfile(name: String, isMale: Boolean) {
        context.tasbeehDataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            preferences[USER_IS_MALE_KEY] = isMale
            
            // Auto-update leaderboard username if it's currently empty
            if (preferences[LEADERBOARD_USERNAME_KEY].isNullOrEmpty()) {
                preferences[LEADERBOARD_USERNAME_KEY] = name
            }
        }
    }

    suspend fun saveLeaderboardSettings(username: String, isAnonymous: Boolean) {
        context.tasbeehDataStore.edit { preferences ->
            preferences[LEADERBOARD_USERNAME_KEY] = username
            preferences[IS_ANONYMOUS_KEY] = isAnonymous
        }
    }

    suspend fun saveLeaderboardEnabled(enabled: Boolean) {
        context.tasbeehDataStore.edit { preferences ->
            preferences[IS_LEADERBOARD_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun calculateTotalHistoryForPeriod(startDate: LocalDate, endDate: LocalDate): Int {
        val history = historyFlow.first()
        return history.filter {
            try {
                // Remove any unexpected whitespace and handle potential date format differences
                val cleanDate = it.date.trim()
                val date = if (cleanDate.contains("-")) {
                    LocalDate.parse(cleanDate)
                } else if (cleanDate.contains("/")) {
                    // Fallback for MM/DD/YYYY or DD/MM/YYYY if ever used
                    null 
                } else {
                    null
                }
                
                date != null && (date.isEqual(startDate) || date.isAfter(startDate)) && date.isBefore(endDate)
            } catch (e: Exception) {
                false
            }
        }.sumOf { it.totalCount }
    }
    
    suspend fun calculateCurrentStreak(): Int {
        val history = historyFlow.first()
        val goals = goalsFlow.first()
        val todayCount = goals.distinctDailyTotal()
        val sortedHistory = history.sortedByDescending { it.date }
        val today = LocalDate.now()
        var currentCheckDate = today.minusDays(1)
        var consecutive = 0
        while (true) {
            val dateStr = currentCheckDate.toString()
            val entry = sortedHistory.find { it.date == dateStr }
            if (entry != null && entry.totalCount > 0) {
                consecutive++
                currentCheckDate = currentCheckDate.minusDays(1)
            } else {
                break
            }
        }
        return if (todayCount > 0) consecutive + 1 else consecutive
    }

    suspend fun checkAndUnlockAchievements() {
        val goals = goalsFlow.first()
        val totalCount = goals.distinctTotalCount()
        val streak = calculateCurrentStreak()
        val todayTotal = goals.distinctDailyTotal()
        val currentCounts = earnedAchievementsCountsFlow.first().toMutableMap()
        var newUnlock = false
        AchievementsLibrary.list.forEach { achievement ->
            if (!currentCounts.containsKey(achievement.id)) {
                val unlocked = when (achievement.type) {
                    AchievementType.TOTAL_COUNT -> totalCount >= achievement.threshold
                    AchievementType.STREAK -> streak >= achievement.threshold
                    AchievementType.DAILY_COUNT -> todayTotal >= achievement.threshold
                }
                if (unlocked) {
                    currentCounts[achievement.id] = 1
                    newUnlock = true
                }
            }
        }
        if (newUnlock) {
            context.tasbeehDataStore.edit { preferences ->
                preferences[EARNED_ACHIEVEMENTS_COUNTS_KEY] = gson.toJson(currentCounts)
            }
        }
    }

    suspend fun checkAndResetDailyCounts() {
        context.tasbeehDataStore.edit { preferences ->
            val lastResetDateStr = preferences[LAST_RESET_DATE_KEY]
            val todayStr = LocalDate.now().toString()
            if (lastResetDateStr != todayStr) {
                 val currentGoals = parseGoals(preferences[GOALS_KEY])
                 
                 // Save history using dailyCount
                 val dailyTotal = currentGoals.distinctDailyTotal()
                 if (dailyTotal > 0) {
                     val detailsMap = currentGoals.dailyDetailsByName()
                     val historyJson = preferences[HISTORY_KEY]
                     val currentHistory: MutableList<TasbeehHistory> = if (historyJson != null) {
                         val type = object : TypeToken<MutableList<TasbeehHistory>>() {}.type
                         gson.fromJson(historyJson, type)
                     } else {
                         mutableListOf()
                     }
                     val dateForHistory = lastResetDateStr ?: LocalDate.now().minusDays(1).toString()
                     val existingEntryIndex = currentHistory.indexOfFirst { it.date == dateForHistory }
                     if (existingEntryIndex != -1) {
                          currentHistory[existingEntryIndex] = TasbeehHistory(dateForHistory, dailyTotal, detailsMap)
                     } else {
                          currentHistory.add(TasbeehHistory(dateForHistory, dailyTotal, detailsMap))
                     }
                     preferences[HISTORY_KEY] = gson.toJson(currentHistory)
                 }
                 
                 // Reset counts based on duration
                 val today = LocalDate.now()
                 val resetGoals = currentGoals.map { goal ->
                     val lastReset = Instant.ofEpochMilli(goal.lastResetDate).atZone(ZoneId.systemDefault()).toLocalDate()
                     
                     val shouldResetDuration = when (goal.duration) {
                         GoalDuration.DAILY -> true
                         GoalDuration.WEEKLY -> ChronoUnit.WEEKS.between(lastReset, today) >= 1
                         GoalDuration.MONTHLY -> ChronoUnit.MONTHS.between(lastReset, today) >= 1
                         GoalDuration.YEARLY -> ChronoUnit.YEARS.between(lastReset, today) >= 1
                     }
                     
                     goal.copy(
                         dailyCount = 0,
                         currentCount = if (shouldResetDuration) 0 else goal.currentCount,
                         lastResetDate = if (shouldResetDuration) System.currentTimeMillis() else goal.lastResetDate
                     )
                 }

                 preferences[GOALS_KEY] = gson.toJson(resetGoals)
                 preferences[LAST_RESET_DATE_KEY] = todayStr
            }
        }
        checkAndUnlockAchievements()
    }

    suspend fun runDataMigrationIfNeeded() {
        context.tasbeehDataStore.edit { preferences ->
            val syncCompleted = preferences[SYNC_V1_COMPLETED_KEY] ?: false
            if (syncCompleted) return@edit
            
            val goalsJson = preferences[GOALS_KEY]
            val historyJson = preferences[HISTORY_KEY]
            
            if (goalsJson != null && historyJson != null) {
                val typeGoal = object : TypeToken<List<TasbeehGoal>>() {}.type
                val currentGoals: List<TasbeehGoal> = gson.fromJson(goalsJson, typeGoal)
                
                val typeHistory = object : TypeToken<List<TasbeehHistory>>() {}.type
                val history: List<TasbeehHistory> = gson.fromJson(historyJson, typeHistory)
                
                // Recalculate accurate historical totals
                val historicalCounts = mutableMapOf<String, Int>()
                for (entry in history) {
                    for ((name, count) in entry.details) {
                        historicalCounts[name] = (historicalCounts[name] ?: 0) + count
                    }
                }
                
                // Fix: Reset totalCount to (History + CurrentCount)
                val fixedGoals = currentGoals.map { goal ->
                    val totalFromHistory = historicalCounts[goal.name] ?: 0
                    goal.copy(totalCount = totalFromHistory + goal.currentCount)
                }
                
                preferences[GOALS_KEY] = gson.toJson(fixedGoals)
                preferences[SYNC_V1_COMPLETED_KEY] = true
            }
        }
    }

    suspend fun saveGoals(goals: List<TasbeehGoal>) {
        val json = gson.toJson(goals)
        context.tasbeehDataStore.edit { preferences ->
            preferences[GOALS_KEY] = json
            if (!preferences.contains(LAST_RESET_DATE_KEY)) {
                preferences[LAST_RESET_DATE_KEY] = LocalDate.now().toString()
            }
        }
        checkAndUnlockAchievements()
    }

    suspend fun resetTodayCounts() {
        context.tasbeehDataStore.edit { preferences ->
            val currentGoals = parseGoals(preferences[GOALS_KEY])
            val resetGoals = currentGoals.map { goal ->
                if (goal.duration == GoalDuration.DAILY) {
                    goal.copy(currentCount = 0, dailyCount = 0)
                } else {
                    goal.copy(
                        currentCount = (goal.currentCount - goal.dailyCount).coerceAtLeast(0),
                        dailyCount = 0
                    )
                }
            }
            preferences[GOALS_KEY] = gson.toJson(resetGoals)
        }
    }

    suspend fun getFullBackupData(): BackupData {
        val goals = goalsFlow.first()
        val history = historyFlow.first()
        val tasbeehPrefs = context.tasbeehDataStore.data.first()
        val settingsPrefs = context.settingsDataStore.data.first()

        val userProfile = UserProfileBackup(
            name = tasbeehPrefs[USER_NAME_KEY] ?: "",
            isMale = tasbeehPrefs[USER_IS_MALE_KEY] ?: true
        )

        val customDetails = customDetailsFlow.first()

        val settings = SettingsBackup(
            theme = settingsPrefs[SETTINGS_THEME_KEY] ?: 0,
            gradient = settingsPrefs[SETTINGS_GRADIENT_KEY] ?: "Sunset",
            thickness = settingsPrefs[SETTINGS_THICKNESS_KEY] ?: 20f,
            isSoundEnabled = settingsPrefs[SETTINGS_SOUND_KEY] ?: true,
            isVibrateTapEnabled = settingsPrefs[SETTINGS_VIBRATE_TAP_KEY] ?: false,
            isVibrate100Enabled = settingsPrefs[SETTINGS_VIBRATE_100_KEY] ?: true,
            language = settingsPrefs[SETTINGS_LANGUAGE_KEY] ?: "en",
            showCounterCircle = settingsPrefs[SETTINGS_SHOW_COUNTER_CIRCLE_KEY] ?: true,
            toastReminderEnabled = tasbeehPrefs[TOAST_REMINDER_ENABLED_KEY] ?: false,
            toastReminderText = tasbeehPrefs[TOAST_REMINDER_TEXT_KEY] ?: "Time for Dhikr",
            toastReminderInterval = tasbeehPrefs[TOAST_REMINDER_INTERVAL_KEY] ?: 15
        )

        val earnedAchievements = earnedAchievementsCountsFlow.first()
        val lastResetDate = tasbeehPrefs[LAST_RESET_DATE_KEY]
        val leaderboardUsername = tasbeehPrefs[LEADERBOARD_USERNAME_KEY] ?: ""
        val isAnonymous = tasbeehPrefs[IS_ANONYMOUS_KEY] ?: false
        val isLeaderboardEnabled = tasbeehPrefs[IS_LEADERBOARD_ENABLED_KEY] ?: false

        return BackupData(
            goals = goals,
            history = history,
            earnedAchievements = earnedAchievements,
            lastResetDate = lastResetDate,
            userProfile = userProfile,
            customDetails = customDetails,
            settings = settings,
            leaderboardUsername = leaderboardUsername,
            isAnonymous = isAnonymous,
            isLeaderboardEnabled = isLeaderboardEnabled
        )
    }

    suspend fun applyFullBackupData(backupData: BackupData) {
        val fixedGoals = backupData.goals.map { goal ->
            @Suppress("SENSELESS_COMPARISON")
            val duration = if (goal.duration == null) GoalDuration.DAILY else goal.duration
            val lastReset = if (goal.lastResetDate == 0L) System.currentTimeMillis() else goal.lastResetDate
            // Explicitly pass counts to ensure they are copied correctly during restoration
            goal.copy(
                duration = duration, 
                lastResetDate = lastReset,
                currentCount = goal.currentCount,
                dailyCount = goal.dailyCount,
                totalCount = goal.totalCount
            )
        }

        context.tasbeehDataStore.edit { prefs ->
            prefs[GOALS_KEY] = gson.toJson(fixedGoals)
            prefs[HISTORY_KEY] = gson.toJson(backupData.history)
            
            // Mark sync as completed to prevent recalculation of totals
            prefs[SYNC_V1_COMPLETED_KEY] = true
            
            backupData.earnedAchievements?.let { prefs[EARNED_ACHIEVEMENTS_COUNTS_KEY] = gson.toJson(it) }
            
            // CRITICAL: Ensure we restore the lastResetDate to prevent immediate daily reset
            if (!backupData.lastResetDate.isNullOrEmpty()) {
                prefs[LAST_RESET_DATE_KEY] = backupData.lastResetDate
            } else {
                // If missing, set to today to avoid resetting counts from today
                prefs[LAST_RESET_DATE_KEY] = LocalDate.now().toString()
            }

            backupData.userProfile?.let { profile ->
                prefs[USER_NAME_KEY] = profile.name
                prefs[USER_IS_MALE_KEY] = profile.isMale
            }
            backupData.customDetails?.let { details ->
                prefs[CUSTOM_DETAILS_KEY] = gson.toJson(details)
            }
            backupData.settings?.let { settings ->
                prefs[TOAST_REMINDER_ENABLED_KEY] = settings.toastReminderEnabled
                prefs[TOAST_REMINDER_TEXT_KEY] = settings.toastReminderText
                prefs[TOAST_REMINDER_INTERVAL_KEY] = settings.toastReminderInterval
            }
            backupData.leaderboardUsername?.let { prefs[LEADERBOARD_USERNAME_KEY] = it }
            backupData.isAnonymous?.let { prefs[IS_ANONYMOUS_KEY] = it }
            backupData.isLeaderboardEnabled?.let { prefs[IS_LEADERBOARD_ENABLED_KEY] = it }
        }

        backupData.settings?.let { settings ->
            context.settingsDataStore.edit { prefs ->
                prefs[SETTINGS_THEME_KEY] = settings.theme
                prefs[SETTINGS_GRADIENT_KEY] = settings.gradient
                prefs[SETTINGS_THICKNESS_KEY] = settings.thickness
                prefs[SETTINGS_SOUND_KEY] = settings.isSoundEnabled
                prefs[SETTINGS_VIBRATE_TAP_KEY] = settings.isVibrateTapEnabled
                prefs[SETTINGS_VIBRATE_100_KEY] = settings.isVibrate100Enabled
                prefs[SETTINGS_LANGUAGE_KEY] = settings.language
                prefs[SETTINGS_SHOW_COUNTER_CIRCLE_KEY] = settings.showCounterCircle
            }
        }
        checkAndUnlockAchievements()
    }
    
    suspend fun saveBackupToUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val goals = goalsFlow.first()
            val history = historyFlow.first()
            val tasbeehPrefs = context.tasbeehDataStore.data.first()
            val settingsPrefs = context.settingsDataStore.data.first()
            
            val userProfile = UserProfileBackup(
                name = tasbeehPrefs[USER_NAME_KEY] ?: "",
                isMale = tasbeehPrefs[USER_IS_MALE_KEY] ?: true
            )
            
            val customDetails = customDetailsFlow.first()
            
            val settings = SettingsBackup(
                theme = settingsPrefs[SETTINGS_THEME_KEY] ?: 0,
                gradient = settingsPrefs[SETTINGS_GRADIENT_KEY] ?: "Sunset",
                thickness = settingsPrefs[SETTINGS_THICKNESS_KEY] ?: 20f,
                isSoundEnabled = settingsPrefs[SETTINGS_SOUND_KEY] ?: true,
                isVibrateTapEnabled = settingsPrefs[SETTINGS_VIBRATE_TAP_KEY] ?: false,
                isVibrate100Enabled = settingsPrefs[SETTINGS_VIBRATE_100_KEY] ?: true,
                language = settingsPrefs[SETTINGS_LANGUAGE_KEY] ?: "en",
                showCounterCircle = settingsPrefs[SETTINGS_SHOW_COUNTER_CIRCLE_KEY] ?: true,
                toastReminderEnabled = tasbeehPrefs[TOAST_REMINDER_ENABLED_KEY] ?: false,
                toastReminderText = tasbeehPrefs[TOAST_REMINDER_TEXT_KEY] ?: "Time for Dhikr",
                toastReminderInterval = tasbeehPrefs[TOAST_REMINDER_INTERVAL_KEY] ?: 15
            )
            
            val earnedAchievements = earnedAchievementsCountsFlow.first()
            val lastResetDate = tasbeehPrefs[LAST_RESET_DATE_KEY]
            
            val backupData = BackupData(
                goals = goals, 
                history = history, 
                earnedAchievements = earnedAchievements,
                lastResetDate = lastResetDate,
                userProfile = userProfile, 
                customDetails = customDetails, 
                settings = settings
            )
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    gson.toJson(backupData, writer)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun restoreFromBackup(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext false
            val backupData = InputStreamReader(inputStream).use { reader ->
                gson.fromJson(reader, BackupData::class.java)
            }
            
            if (backupData != null && backupData.goals.isNotEmpty()) {
                applyFullBackupData(backupData)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
