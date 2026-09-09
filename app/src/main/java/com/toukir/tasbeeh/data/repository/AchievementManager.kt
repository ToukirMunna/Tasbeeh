package com.toukir.tasbeeh.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.data.AchievementType
import com.toukir.tasbeeh.data.AchievementsLibrary
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.tasbeehDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AchievementManager(private val context: Context, private val gson: Gson) {
    val EARNED_ACHIEVEMENTS_COUNTS_KEY = stringPreferencesKey("earned_achievements_counts")

    val earnedAchievementsCountsFlow: Flow<Map<String, Int>> = context.tasbeehDataStore.data
        .map { preferences ->
            val json = preferences[EARNED_ACHIEVEMENTS_COUNTS_KEY]
            if (json != null) {
                val type = object : TypeToken<Map<String, Int>>() {}.type
                try {
                    gson.fromJson(json, type)
                } catch (e: Exception) {
                    emptyMap()
                }
            } else {
                emptyMap()
            }
        }

    suspend fun checkAndUnlockAchievements(
        goals: List<TasbeehGoal>,
        totalCount: Int,
        dailyTotal: Int,
        streak: Int
    ) {
        val currentCounts = earnedAchievementsCountsFlow.first().toMutableMap()
        var newUnlock = false
        AchievementsLibrary.list.forEach { achievement ->
            if (!currentCounts.containsKey(achievement.id)) {
                val unlocked = when (achievement.type) {
                    AchievementType.TOTAL_COUNT -> totalCount >= achievement.threshold
                    AchievementType.STREAK -> streak >= achievement.threshold
                    AchievementType.DAILY_COUNT -> dailyTotal >= achievement.threshold
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
}
