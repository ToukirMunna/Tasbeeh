package com.toukir.tasbeeh.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.data.tasbeehDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class HistoryDataStore(
    private val context: Context,
    private val gson: Gson,
    private val goalsDataStore: GoalsDataStore
) {
    val HISTORY_KEY = stringPreferencesKey("tasbeeh_history")
    val LAST_RESET_DATE_KEY = stringPreferencesKey("last_reset_date")
    val SYNC_V1_COMPLETED_KEY = booleanPreferencesKey("sync_v1_completed")

    val historyFlow: Flow<List<TasbeehHistory>> = context.tasbeehDataStore.data
        .map { preferences ->
            val json = preferences[HISTORY_KEY]
            if (json != null) {
                val type = object : TypeToken<List<TasbeehHistory>>() {}.type
                try {
                    gson.fromJson(json, type)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    suspend fun calculateTotalHistoryForPeriod(startDate: LocalDate, endDate: LocalDate): Int {
        val history = historyFlow.first()
        return history.filter {
            try {
                val cleanDate = it.date.trim()
                val date = if (cleanDate.contains("-")) {
                    LocalDate.parse(cleanDate)
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
        val goals = goalsDataStore.goalsFlow.first()
        val todayCount = goalsDataStore.distinctDailyTotal(goals)
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

    suspend fun checkAndResetDailyCounts() {
        context.tasbeehDataStore.edit { preferences ->
            val lastResetDateStr = preferences[LAST_RESET_DATE_KEY]
            val todayStr = LocalDate.now().toString()
            if (lastResetDateStr != todayStr) {
                val currentGoals = goalsDataStore.parseGoals(preferences[goalsDataStore.GOALS_KEY])
                val dailyTotal = goalsDataStore.distinctDailyTotal(currentGoals)
                if (dailyTotal > 0) {
                    val detailsMap = goalsDataStore.dailyDetailsByName(currentGoals)
                    val historyJson = preferences[HISTORY_KEY]
                    val currentHistory: MutableList<TasbeehHistory> = if (historyJson != null) {
                        val type = object : TypeToken<MutableList<TasbeehHistory>>() {}.type
                        try {
                            gson.fromJson(historyJson, type)
                        } catch (e: Exception) {
                            mutableListOf()
                        }
                    } else {
                        mutableListOf()
                    }
                    val dateForHistory = lastResetDateStr ?: LocalDate.now().minusDays(1).toString()
                    val existingEntryIndex = currentHistory.indexOfFirst { it.date == dateForHistory }
                    if (existingEntryIndex != -1) {
                        val existing = currentHistory[existingEntryIndex]
                        val mergedDetails = (existing.details.keys + detailsMap.keys).associateWith { key ->
                            maxOf(existing.details[key] ?: 0, detailsMap[key] ?: 0)
                        }
                        val mergedTotal = maxOf(existing.totalCount, dailyTotal, mergedDetails.values.sum())
                        currentHistory[existingEntryIndex] = TasbeehHistory(dateForHistory, mergedTotal, mergedDetails)
                    } else {
                        currentHistory.add(TasbeehHistory(dateForHistory, dailyTotal, detailsMap))
                    }
                    preferences[HISTORY_KEY] = gson.toJson(currentHistory)
                }

                val today = LocalDate.now()
                val resetGoals = currentGoals.map { goal ->
                    val lastReset = Instant.ofEpochMilli(goal.lastResetDate).atZone(ZoneId.systemDefault()).toLocalDate()
                    val shouldResetDuration = when (goal.duration) {
                        GoalDuration.DAILY -> true
                        GoalDuration.WEEKLY -> ChronoUnit.DAYS.between(lastReset, today) >= 7
                        GoalDuration.MONTHLY -> YearMonth.from(lastReset) != YearMonth.from(today)
                        GoalDuration.YEARLY -> today.year != lastReset.year
                    }
                    goal.copy(
                        dailyCount = 0,
                        currentCount = if (shouldResetDuration) 0 else goal.currentCount,
                        lastResetDate = if (shouldResetDuration) System.currentTimeMillis() else goal.lastResetDate
                    )
                }

                preferences[goalsDataStore.GOALS_KEY] = gson.toJson(resetGoals)
                preferences[LAST_RESET_DATE_KEY] = todayStr
            }
        }
    }

    suspend fun runDataMigrationIfNeeded() {
        context.tasbeehDataStore.edit { preferences ->
            val syncCompleted = preferences[SYNC_V1_COMPLETED_KEY] ?: false
            if (syncCompleted) return@edit

            val goalsJson = preferences[goalsDataStore.GOALS_KEY]
            val historyJson = preferences[HISTORY_KEY]

            if (goalsJson != null && historyJson != null) {
                val typeGoal = object : TypeToken<List<com.toukir.tasbeeh.TasbeehGoal>>() {}.type
                val currentGoals: List<com.toukir.tasbeeh.TasbeehGoal> = gson.fromJson(goalsJson, typeGoal)
                val typeHistory = object : TypeToken<List<TasbeehHistory>>() {}.type
                val history: List<TasbeehHistory> = gson.fromJson(historyJson, typeHistory)

                val historicalCounts = mutableMapOf<String, Int>()
                for (entry in history) {
                    for ((name, count) in entry.details) {
                        historicalCounts[name] = (historicalCounts[name] ?: 0) + count
                    }
                }

                val fixedGoals = currentGoals.map { goal ->
                    val totalFromHistory = historicalCounts[goal.name] ?: 0
                    goal.copy(totalCount = totalFromHistory + goal.currentCount)
                }

                preferences[goalsDataStore.GOALS_KEY] = gson.toJson(fixedGoals)
                preferences[SYNC_V1_COMPLETED_KEY] = true
            }
        }
    }
}
