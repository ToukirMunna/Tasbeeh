package com.toukir.tasbeeh.domain

import com.toukir.tasbeeh.data.TasbeehHistory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculationLogicTest {

    private fun calculateStreak(
        history: List<TasbeehHistory>,
        todayCount: Int,
        referenceDate: LocalDate = LocalDate.now()
    ): Int {
        val sortedHistory = history.sortedByDescending { it.date }
        var currentCheckDate = referenceDate.minusDays(1)
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

    @Test
    fun streak_zeroWhenNoHistoryAndZeroToday() {
        val streak = calculateStreak(emptyList(), todayCount = 0)
        assertEquals(0, streak)
    }

    @Test
    fun streak_oneWhenOnlyTodayActive() {
        val streak = calculateStreak(emptyList(), todayCount = 33)
        assertEquals(1, streak)
    }

    @Test
    fun streak_incrementsCorrectlyForConsecutiveDays() {
        val today = LocalDate.of(2026, 9, 9)
        val history = listOf(
            TasbeehHistory(date = today.minusDays(1).toString(), totalCount = 100),
            TasbeehHistory(date = today.minusDays(2).toString(), totalCount = 50),
            TasbeehHistory(date = today.minusDays(3).toString(), totalCount = 70)
        )

        val streakWithoutToday = calculateStreak(history, todayCount = 0, referenceDate = today)
        assertEquals(3, streakWithoutToday)

        val streakWithToday = calculateStreak(history, todayCount = 15, referenceDate = today)
        assertEquals(4, streakWithToday)
    }

    @Test
    fun streak_breaksWhenADayIsMissed() {
        val today = LocalDate.of(2026, 9, 9)
        val history = listOf(
            TasbeehHistory(date = today.minusDays(1).toString(), totalCount = 100),
            // minusDays(2) is missing!
            TasbeehHistory(date = today.minusDays(3).toString(), totalCount = 70)
        )

        val streak = calculateStreak(history, todayCount = 10, referenceDate = today)
        assertEquals(2, streak) // today + yesterday = 2
    }

    @Test
    fun streak_stopsAtZeroCountDay() {
        val today = LocalDate.of(2026, 9, 9)
        val history = listOf(
            TasbeehHistory(date = today.minusDays(1).toString(), totalCount = 100),
            TasbeehHistory(date = today.minusDays(2).toString(), totalCount = 0),
            TasbeehHistory(date = today.minusDays(3).toString(), totalCount = 50)
        )

        val streak = calculateStreak(history, todayCount = 0, referenceDate = today)
        assertEquals(1, streak)
    }
}
