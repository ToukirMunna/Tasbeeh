package com.toukir.tasbeeh.domain

import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.TasbeehHistory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupMergeLogicTest {

    private fun mergeGoals(local: List<TasbeehGoal>, remote: List<TasbeehGoal>): List<TasbeehGoal> {
        val currentByName = local.associateBy { it.name }
        val backupByName = remote.associateBy { it.name }
        val allGoalNames = currentByName.keys + backupByName.keys

        return allGoalNames.map { name ->
            val localGoal = currentByName[name]
            val remoteGoal = backupByName[name]
            when {
                localGoal != null && remoteGoal != null -> {
                    localGoal.copy(
                        targetCount = remoteGoal.targetCount.coerceAtLeast(localGoal.targetCount),
                        currentCount = maxOf(localGoal.currentCount, remoteGoal.currentCount),
                        dailyCount = maxOf(localGoal.dailyCount, remoteGoal.dailyCount),
                        totalCount = maxOf(localGoal.totalCount, remoteGoal.totalCount),
                        duration = remoteGoal.duration ?: localGoal.duration,
                        isGoal = localGoal.isGoal || remoteGoal.isGoal
                    )
                }
                remoteGoal != null -> remoteGoal
                else -> localGoal!!
            }
        }
    }

    private fun mergeHistory(local: List<TasbeehHistory>, remote: List<TasbeehHistory>): List<TasbeehHistory> {
        val historyByDate = local.associateBy { it.date }.toMutableMap()
        remote.forEach { remoteEntry ->
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
        return historyByDate.values.sortedBy { it.date }
    }

    @Test
    fun mergeGoals_preservesHigherCountsAndUnionOfItems() {
        val local = listOf(
            TasbeehGoal(id = 1, name = "SubhanAllah", targetCount = 100, currentCount = 50, dailyCount = 10, totalCount = 500),
            TasbeehGoal(id = 2, name = "LocalOnly", targetCount = 33, currentCount = 5, dailyCount = 5, totalCount = 20)
        )
        val remote = listOf(
            TasbeehGoal(id = 1, name = "SubhanAllah", targetCount = 200, currentCount = 75, dailyCount = 5, totalCount = 400),
            TasbeehGoal(id = 3, name = "RemoteOnly", targetCount = 100, currentCount = 100, dailyCount = 100, totalCount = 1000)
        )

        val merged = mergeGoals(local, remote).associateBy { it.name }

        // SubhanAllah should take max of counts
        val subhanAllah = merged["SubhanAllah"]!!
        assertEquals(200, subhanAllah.targetCount)
        assertEquals(75, subhanAllah.currentCount)
        assertEquals(10, subhanAllah.dailyCount) // Local was 10, remote was 5 -> max is 10
        assertEquals(500, subhanAllah.totalCount) // Local was 500, remote was 400 -> max is 500

        // Both local-only and remote-only items should be preserved
        assertTrue(merged.containsKey("LocalOnly"))
        assertTrue(merged.containsKey("RemoteOnly"))
    }

    @Test
    fun mergeHistory_mergesDetailsAndTakesMaximumTotals() {
        val local = listOf(
            TasbeehHistory(date = "2026-09-08", totalCount = 50, details = mapOf("DhikrA" to 50))
        )
        val remote = listOf(
            TasbeehHistory(date = "2026-09-08", totalCount = 60, details = mapOf("DhikrA" to 30, "DhikrB" to 30)),
            TasbeehHistory(date = "2026-09-07", totalCount = 100, details = mapOf("DhikrA" to 100))
        )

        val merged = mergeHistory(local, remote).associateBy { it.date }

        val sept8 = merged["2026-09-08"]!!
        assertEquals(50, sept8.details["DhikrA"])
        assertEquals(30, sept8.details["DhikrB"])
        assertEquals(80, sept8.totalCount) // sum of details is 50 + 30 = 80, which is > maxOf(50, 60)

        assertTrue(merged.containsKey("2026-09-07"))
    }

    private fun mergeAchievements(local: Map<String, Int>, remote: Map<String, Int>): Map<String, Int> {
        val allKeys = local.keys + remote.keys
        return allKeys.associateWith { k -> maxOf(local[k] ?: 0, remote[k] ?: 0) }
    }

    private fun reconcileLastResetDate(localDate: String?, backupDate: String?): String {
        return when {
            backupDate.isNullOrEmpty() -> localDate ?: ""
            localDate.isNullOrEmpty() || backupDate > localDate -> backupDate
            else -> localDate
        }
    }

    @Test
    fun mergeAchievements_takesMaximumOfCounts() {
        val local = mapOf("streak_7" to 1, "count_1000" to 2)
        val remote = mapOf("streak_7" to 1, "count_1000" to 1, "count_5000" to 1)

        val merged = mergeAchievements(local, remote)
        assertEquals(1, merged["streak_7"])
        assertEquals(2, merged["count_1000"]) // Local was 2, remote was 1 -> max is 2
        assertEquals(1, merged["count_5000"])
    }

    @Test
    fun reconcileLastResetDate_preservesLatestDate() {
        // When local is newer than backup (e.g. user restored an old backup), keep local
        val keptLocal = reconcileLastResetDate("2026-09-09", "2026-09-05")
        assertEquals("2026-09-09", keptLocal)

        // When backup is newer than local, adopt backup
        val adoptedRemote = reconcileLastResetDate("2026-09-01", "2026-09-08")
        assertEquals("2026-09-08", adoptedRemote)
    }
}
