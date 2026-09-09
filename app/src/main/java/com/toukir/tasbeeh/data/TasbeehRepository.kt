package com.toukir.tasbeeh.data

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.repository.AchievementManager
import com.toukir.tasbeeh.data.repository.BackupRestoreManager
import com.toukir.tasbeeh.data.repository.GoalsDataStore
import com.toukir.tasbeeh.data.repository.HistoryDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

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
    val goalsDataStore = GoalsDataStore(context, gson)
    val historyDataStore = HistoryDataStore(context, gson, goalsDataStore)
    val achievementManager = AchievementManager(context, gson)
    val backupRestoreManager = BackupRestoreManager(
        context, gson, goalsDataStore, historyDataStore, achievementManager
    )

    val goalsFlow: Flow<List<TasbeehGoal>> = goalsDataStore.goalsFlow
    val historyFlow: Flow<List<TasbeehHistory>> = historyDataStore.historyFlow
    val earnedAchievementsCountsFlow: Flow<Map<String, Int>> = achievementManager.earnedAchievementsCountsFlow

    val userNameFlow: Flow<String> = context.tasbeehDataStore.data.map { it[backupRestoreManager.USER_NAME_KEY] ?: "" }
    val userIsMaleFlow: Flow<Boolean> = context.tasbeehDataStore.data.map { it[backupRestoreManager.USER_IS_MALE_KEY] ?: true }
    val customDetailsFlow: Flow<Map<String, AdhkarInfo>> = context.tasbeehDataStore.data.map { preferences ->
        val json = preferences[backupRestoreManager.CUSTOM_DETAILS_KEY]
        if (json != null) {
            val type = object : TypeToken<Map<String, AdhkarInfo>>() {}.type
            try { gson.fromJson(json, type) } catch (e: Exception) { emptyMap() }
        } else emptyMap()
    }

    val toastReminderEnabledFlow: Flow<Boolean> = context.tasbeehDataStore.data.map { it[backupRestoreManager.TOAST_REMINDER_ENABLED_KEY] ?: false }
    val toastReminderTextFlow: Flow<String> = context.tasbeehDataStore.data.map { it[backupRestoreManager.TOAST_REMINDER_TEXT_KEY] ?: "Time for Dhikr" }
    val toastReminderIntervalFlow: Flow<Int> = context.tasbeehDataStore.data.map { it[backupRestoreManager.TOAST_REMINDER_INTERVAL_KEY] ?: 15 }
    val leaderboardUsernameFlow: Flow<String> = context.tasbeehDataStore.data.map { it[backupRestoreManager.LEADERBOARD_USERNAME_KEY] ?: "" }
    val isAnonymousFlow: Flow<Boolean> = context.tasbeehDataStore.data.map { it[backupRestoreManager.IS_ANONYMOUS_KEY] ?: false }
    val isLeaderboardEnabledFlow: Flow<Boolean> = context.tasbeehDataStore.data.map { it[backupRestoreManager.IS_LEADERBOARD_ENABLED_KEY] ?: false }

    suspend fun saveToastReminderSettings(enabled: Boolean, text: String, interval: Int) {
        context.tasbeehDataStore.edit {
            it[backupRestoreManager.TOAST_REMINDER_ENABLED_KEY] = enabled
            it[backupRestoreManager.TOAST_REMINDER_TEXT_KEY] = text
            it[backupRestoreManager.TOAST_REMINDER_INTERVAL_KEY] = interval
        }
    }

    suspend fun saveAdhkarInfo(info: AdhkarInfo) {
        val current = customDetailsFlow.first().toMutableMap()
        current[info.name] = info
        context.tasbeehDataStore.edit { it[backupRestoreManager.CUSTOM_DETAILS_KEY] = gson.toJson(current) }
    }

    suspend fun saveUserProfile(name: String, isMale: Boolean) {
        context.tasbeehDataStore.edit {
            it[backupRestoreManager.USER_NAME_KEY] = name
            it[backupRestoreManager.USER_IS_MALE_KEY] = isMale
            if (it[backupRestoreManager.LEADERBOARD_USERNAME_KEY].isNullOrEmpty()) {
                it[backupRestoreManager.LEADERBOARD_USERNAME_KEY] = name
            }
        }
    }

    suspend fun saveLeaderboardSettings(username: String, isAnonymous: Boolean) {
        context.tasbeehDataStore.edit {
            it[backupRestoreManager.LEADERBOARD_USERNAME_KEY] = username
            it[backupRestoreManager.IS_ANONYMOUS_KEY] = isAnonymous
        }
    }

    suspend fun saveLeaderboardEnabled(enabled: Boolean) {
        context.tasbeehDataStore.edit { it[backupRestoreManager.IS_LEADERBOARD_ENABLED_KEY] = enabled }
    }

    suspend fun calculateTotalHistoryForPeriod(startDate: LocalDate, endDate: LocalDate): Int =
        historyDataStore.calculateTotalHistoryForPeriod(startDate, endDate)

    suspend fun calculateCurrentStreak(): Int = historyDataStore.calculateCurrentStreak()

    suspend fun checkAndUnlockAchievements() {
        val goals = goalsFlow.first()
        val totalCount = goalsDataStore.distinctTotalCount(goals)
        val streak = calculateCurrentStreak()
        val todayTotal = goalsDataStore.distinctDailyTotal(goals)
        achievementManager.checkAndUnlockAchievements(goals, totalCount, todayTotal, streak)
    }

    suspend fun checkAndResetDailyCounts() {
        historyDataStore.checkAndResetDailyCounts()
        checkAndUnlockAchievements()
    }

    suspend fun runDataMigrationIfNeeded() = historyDataStore.runDataMigrationIfNeeded()

    suspend fun saveGoals(goals: List<TasbeehGoal>) {
        goalsDataStore.saveGoals(goals)
        checkAndUnlockAchievements()
    }

    suspend fun resetTodayCounts() = goalsDataStore.resetTodayCounts()

    suspend fun getFullBackupData(): BackupData = backupRestoreManager.getFullBackupData(customDetailsFlow.first())

    suspend fun applyFullBackupData(backupData: BackupData) {
        backupRestoreManager.applyFullBackupData(backupData)
        checkAndUnlockAchievements()
    }

    suspend fun saveBackupToUri(uri: Uri): Boolean = backupRestoreManager.saveBackupToUri(uri, customDetailsFlow.first())

    suspend fun restoreFromBackup(uri: Uri): Boolean = backupRestoreManager.restoreFromBackup(uri)
}
