package com.toukir.tasbeeh.domain

import com.toukir.tasbeeh.GoalDuration
import com.toukir.tasbeeh.TasbeehGoal
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalsCalculationTest {

    private fun distinctDailyTotal(goals: List<TasbeehGoal>): Int {
        return goals.groupBy { it.name }.values.sumOf { gList -> gList.maxOf { it.dailyCount } }
    }

    private fun distinctTotalCount(goals: List<TasbeehGoal>): Int {
        return goals.groupBy { it.name }.values.sumOf { gList -> gList.maxOf { it.totalCount } }
    }

    private fun dailyDetailsByName(goals: List<TasbeehGoal>): Map<String, Int> {
        return goals.groupBy { it.name }
            .mapValues { (_, gList) -> gList.maxOf { it.dailyCount } }
            .filterValues { it > 0 }
    }

    @Test
    fun distinctDailyTotal_aggregatesDistinctDhikrsCorrectly() {
        val goals = listOf(
            TasbeehGoal(id = 1, name = "SubhanAllah", targetCount = 100, currentCount = 33, dailyCount = 33, totalCount = 100),
            TasbeehGoal(id = 2, name = "Alhamdulillah", targetCount = 100, currentCount = 33, dailyCount = 33, totalCount = 100),
            TasbeehGoal(id = 3, name = "Allahu Akbar", targetCount = 100, currentCount = 34, dailyCount = 34, totalCount = 100)
        )

        val total = distinctDailyTotal(goals)
        assertEquals(100, total)
    }

    @Test
    fun distinctDailyTotal_handlesDuplicateDhikrNamesByTakingMax() {
        val goals = listOf(
            TasbeehGoal(id = 1, name = "SubhanAllah", targetCount = 100, currentCount = 33, dailyCount = 33, totalCount = 100),
            TasbeehGoal(id = 2, name = "SubhanAllah", targetCount = 500, currentCount = 50, dailyCount = 50, totalCount = 500)
        )

        val total = distinctDailyTotal(goals)
        assertEquals(50, total)
    }

    @Test
    fun distinctTotalCount_sumsMaxAcrossDistinctDhikrs() {
        val goals = listOf(
            TasbeehGoal(id = 1, name = "SubhanAllah", targetCount = 100, currentCount = 33, dailyCount = 33, totalCount = 150),
            TasbeehGoal(id = 2, name = "SubhanAllah", targetCount = 100, currentCount = 33, dailyCount = 33, totalCount = 200),
            TasbeehGoal(id = 3, name = "Astaghfirullah", targetCount = 100, currentCount = 100, dailyCount = 100, totalCount = 300)
        )

        val total = distinctTotalCount(goals)
        assertEquals(500, total)
    }

    @Test
    fun dailyDetailsByName_filtersOutZeroCounts() {
        val goals = listOf(
            TasbeehGoal(id = 1, name = "SubhanAllah", targetCount = 100, currentCount = 33, dailyCount = 33, totalCount = 150),
            TasbeehGoal(id = 2, name = "Alhamdulillah", targetCount = 100, currentCount = 0, dailyCount = 0, totalCount = 0)
        )

        val details = dailyDetailsByName(goals)
        assertEquals(1, details.size)
        assertEquals(33, details["SubhanAllah"])
        assertEquals(null, details["Alhamdulillah"])
    }
}
