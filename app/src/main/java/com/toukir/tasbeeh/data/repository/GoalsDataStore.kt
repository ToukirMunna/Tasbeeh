package com.toukir.tasbeeh.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.tasbeehDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GoalsDataStore(private val context: Context, private val gson: Gson) {
    val GOALS_KEY = stringPreferencesKey("tasbeeh_goals_v5")
    val LAST_RESET_DATE_KEY = stringPreferencesKey("last_reset_date")

    fun getDefaultGoals(): List<TasbeehGoal> {
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

    fun parseGoals(json: String?): List<TasbeehGoal> {
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
            val dailyCount = if (duration == GoalDuration.DAILY && goal.dailyCount == 0) {
                goal.currentCount
            } else {
                goal.dailyCount
            }
            goal.copy(duration = duration, lastResetDate = lastReset, dailyCount = dailyCount)
        }
    }

    fun distinctDailyTotal(goals: List<TasbeehGoal>): Int {
        return goals.groupBy { it.name }.values.sumOf { gList -> gList.maxOf { it.dailyCount } }
    }

    fun distinctTotalCount(goals: List<TasbeehGoal>): Int {
        return goals.groupBy { it.name }.values.sumOf { gList -> gList.maxOf { it.totalCount } }
    }

    fun dailyDetailsByName(goals: List<TasbeehGoal>): Map<String, Int> {
        return goals.groupBy { it.name }
            .mapValues { (_, gList) -> gList.maxOf { it.dailyCount } }
            .filterValues { it > 0 }
    }

    val goalsFlow: Flow<List<TasbeehGoal>> = context.tasbeehDataStore.data
        .map { preferences -> parseGoals(preferences[GOALS_KEY]) }

    suspend fun saveGoals(goals: List<TasbeehGoal>) {
        val json = gson.toJson(goals)
        context.tasbeehDataStore.edit { preferences ->
            preferences[GOALS_KEY] = json
            if (!preferences.contains(LAST_RESET_DATE_KEY)) {
                preferences[LAST_RESET_DATE_KEY] = LocalDate.now().toString()
            }
        }
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
}
