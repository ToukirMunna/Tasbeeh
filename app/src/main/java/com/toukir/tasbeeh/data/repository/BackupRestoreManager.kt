package com.toukir.tasbeeh.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.data.AdhkarInfo
import com.toukir.tasbeeh.data.BackupData
import com.toukir.tasbeeh.data.SettingsBackup
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.data.UserProfileBackup
import com.toukir.tasbeeh.data.settingsDataStore
import com.toukir.tasbeeh.data.tasbeehDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDate

class BackupRestoreManager(
    private val context: Context,
    private val gson: Gson,
    private val goalsDataStore: GoalsDataStore,
    private val historyDataStore: HistoryDataStore,
    private val achievementManager: AchievementManager
) {
    val USER_NAME_KEY = stringPreferencesKey("user_name")
    val USER_IS_MALE_KEY = booleanPreferencesKey("user_is_male")
    val CUSTOM_DETAILS_KEY = stringPreferencesKey("custom_adhkar_details")
    val TOAST_REMINDER_ENABLED_KEY = booleanPreferencesKey("toast_reminder_enabled")
    val TOAST_REMINDER_TEXT_KEY = stringPreferencesKey("toast_reminder_text")
    val TOAST_REMINDER_INTERVAL_KEY = intPreferencesKey("toast_reminder_interval")
    val LEADERBOARD_USERNAME_KEY = stringPreferencesKey("leaderboard_username")
    val IS_ANONYMOUS_KEY = booleanPreferencesKey("is_anonymous")
    val IS_LEADERBOARD_ENABLED_KEY = booleanPreferencesKey("is_leaderboard_enabled")

    val SETTINGS_THEME_KEY = intPreferencesKey("app_theme")
    val SETTINGS_GRADIENT_KEY = stringPreferencesKey("app_gradient")
    val SETTINGS_THICKNESS_KEY = floatPreferencesKey("app_thickness")
    val SETTINGS_SOUND_KEY = booleanPreferencesKey("app_sound")
    val SETTINGS_VIBRATE_TAP_KEY = booleanPreferencesKey("app_vibrate_tap")
    val SETTINGS_VIBRATE_100_KEY = booleanPreferencesKey("app_vibrate_100")
    val SETTINGS_LANGUAGE_KEY = stringPreferencesKey("app_language")
    val SETTINGS_SHOW_COUNTER_CIRCLE_KEY = booleanPreferencesKey("app_show_counter_circle")

    suspend fun getFullBackupData(customDetails: Map<String, AdhkarInfo>): BackupData {
        val goals = goalsDataStore.goalsFlow.first()
        val history = historyDataStore.historyFlow.first()
        val tasbeehPrefs = context.tasbeehDataStore.data.first()
        val settingsPrefs = context.settingsDataStore.data.first()

        val userProfile = UserProfileBackup(
            name = tasbeehPrefs[USER_NAME_KEY] ?: "",
            isMale = tasbeehPrefs[USER_IS_MALE_KEY] ?: true
        )

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

        return BackupData(
            goals = goals,
            history = history,
            earnedAchievements = achievementManager.earnedAchievementsCountsFlow.first(),
            lastResetDate = tasbeehPrefs[goalsDataStore.LAST_RESET_DATE_KEY],
            userProfile = userProfile,
            customDetails = customDetails,
            settings = settings,
            leaderboardUsername = tasbeehPrefs[LEADERBOARD_USERNAME_KEY] ?: "",
            isAnonymous = tasbeehPrefs[IS_ANONYMOUS_KEY] ?: false,
            isLeaderboardEnabled = tasbeehPrefs[IS_LEADERBOARD_ENABLED_KEY] ?: false
        )
    }

    suspend fun applyFullBackupData(backupData: BackupData) {
        context.tasbeehDataStore.edit { prefs ->
            val currentGoals = goalsDataStore.parseGoals(prefs[goalsDataStore.GOALS_KEY])
            val currentByName = currentGoals.associateBy { it.name }
            val backupByName = backupData.goals.associateBy { it.name }
            val allGoalNames = currentByName.keys + backupByName.keys

            val mergedGoals = allGoalNames.map { name ->
                val local = currentByName[name]
                val remote = backupByName[name]
                when {
                    local != null && remote != null -> {
                        local.copy(
                            targetCount = remote.targetCount.coerceAtLeast(local.targetCount),
                            currentCount = maxOf(local.currentCount, remote.currentCount),
                            dailyCount = maxOf(local.dailyCount, remote.dailyCount),
                            totalCount = maxOf(local.totalCount, remote.totalCount),
                            duration = remote.duration ?: local.duration,
                            isGoal = local.isGoal || remote.isGoal
                        )
                    }
                    remote != null -> remote
                    else -> local!!
                }
            }
            prefs[goalsDataStore.GOALS_KEY] = gson.toJson(mergedGoals)

            val currentHistoryJson = prefs[historyDataStore.HISTORY_KEY]
            val currentHistory: List<TasbeehHistory> = if (currentHistoryJson != null) {
                try {
                    val type = object : TypeToken<List<TasbeehHistory>>() {}.type
                    gson.fromJson(currentHistoryJson, type)
                } catch (e: Exception) {
                    emptyList()
                }
            } else emptyList()

            val historyByDate = currentHistory.associateBy { it.date }.toMutableMap()
            backupData.history.forEach { remoteEntry ->
                val localEntry = historyByDate[remoteEntry.date]
                if (localEntry != null) {
                    val mergedDetails = (localEntry.details.keys + remoteEntry.details.keys).associateWith { dhikr ->
                        maxOf(localEntry.details[dhikr] ?: 0, remoteEntry.details[dhikr] ?: 0)
                    }
                    val mergedTotal = maxOf(localEntry.totalCount, remoteEntry.totalCount, mergedDetails.values.sum())
                    historyByDate[remoteEntry.date] = TasbeehHistory(remoteEntry.date, mergedTotal, mergedDetails)
                } else {
                    historyByDate[remoteEntry.date] = remoteEntry
                }
            }
            prefs[historyDataStore.HISTORY_KEY] = gson.toJson(historyByDate.values.sortedBy { it.date })

            prefs[historyDataStore.SYNC_V1_COMPLETED_KEY] = true

            backupData.earnedAchievements?.let { backupAchievements ->
                val localJson = prefs[achievementManager.EARNED_ACHIEVEMENTS_COUNTS_KEY]
                val localAchievements: Map<String, Int> = if (localJson != null) {
                    try {
                        val type = object : TypeToken<Map<String, Int>>() {}.type
                        gson.fromJson(localJson, type)
                    } catch (e: Exception) {
                        emptyMap()
                    }
                } else emptyMap()
                val allAchievementKeys = localAchievements.keys + backupAchievements.keys
                val mergedAchievements = allAchievementKeys.associateWith { key ->
                    maxOf(localAchievements[key] ?: 0, backupAchievements[key] ?: 0)
                }
                prefs[achievementManager.EARNED_ACHIEVEMENTS_COUNTS_KEY] = gson.toJson(mergedAchievements)
            }

            if (!backupData.lastResetDate.isNullOrEmpty()) {
                val currentLastResetDate = prefs[goalsDataStore.LAST_RESET_DATE_KEY]
                if (currentLastResetDate.isNullOrEmpty() || backupData.lastResetDate > currentLastResetDate) {
                    prefs[goalsDataStore.LAST_RESET_DATE_KEY] = backupData.lastResetDate
                }
            } else if (!prefs.contains(goalsDataStore.LAST_RESET_DATE_KEY)) {
                prefs[goalsDataStore.LAST_RESET_DATE_KEY] = LocalDate.now().toString()
            }

            backupData.userProfile?.let { profile ->
                if (profile.name.isNotEmpty()) prefs[USER_NAME_KEY] = profile.name
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
    }

    suspend fun saveBackupToUri(uri: Uri, customDetails: Map<String, AdhkarInfo>): Boolean = withContext(Dispatchers.IO) {
        try {
            val backupData = getFullBackupData(customDetails)
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
