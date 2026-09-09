package com.toukir.tasbeeh.domain

import com.toukir.tasbeeh.data.TasbeehHistory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PeriodAggregationLogicTest {

    private fun calculateTotalForPeriod(
        history: List<TasbeehHistory>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Int {
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

    @Test
    fun periodTotal_aggregatesWithinHalfOpenInterval() {
        val start = LocalDate.of(2026, 9, 1)
        val end = LocalDate.of(2026, 9, 8)
        val history = listOf(
            TasbeehHistory(date = "2026-09-01", totalCount = 100),
            TasbeehHistory(date = "2026-09-04", totalCount = 200),
            TasbeehHistory(date = "2026-09-07", totalCount = 300),
            TasbeehHistory(date = "2026-09-08", totalCount = 500), // Excluded: isBefore(end)
            TasbeehHistory(date = "2026-08-31", totalCount = 150)  // Excluded: before start
        )

        val total = calculateTotalForPeriod(history, start, end)
        assertEquals(600, total) // 100 + 200 + 300
    }

    @Test
    fun periodTotal_ignoresMalformedDatesGracefully() {
        val start = LocalDate.of(2026, 9, 1)
        val end = LocalDate.of(2026, 9, 8)
        val history = listOf(
            TasbeehHistory(date = "2026-09-02", totalCount = 100),
            TasbeehHistory(date = "invalid-date", totalCount = 999),
            TasbeehHistory(date = "", totalCount = 888)
        )

        val total = calculateTotalForPeriod(history, start, end)
        assertEquals(100, total)
    }
}
