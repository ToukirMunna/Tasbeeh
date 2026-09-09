package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun DetailStatsScreen(
    period: StatPeriod,
    history: List<TasbeehHistory>,
    onBack: () -> Unit,
    language: String = "en"
) {
    val allTimeText = stringResource(R.string.stats_all_time)
    val (title, relevantHistory) = remember(period, history, allTimeText) {
        when (period) {
            StatPeriod.Week -> {
                val today = LocalDate.now()
                val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                val formatter = DateTimeFormatter.ofPattern("d MMMM")
                "${weekStart.format(formatter)} - ${weekEnd.format(formatter)}" to history.filter {
                    val d = LocalDate.parse(it.date)
                    !d.isBefore(weekStart) && !d.isAfter(weekEnd)
                }
            }
            StatPeriod.Month -> {
                val today = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                today.format(formatter) to history.filter {
                    val d = LocalDate.parse(it.date)
                    d.year == today.year && d.month == today.month
                }
            }
            StatPeriod.Total -> allTimeText to history
        }
    }

    val tasbeehMap = remember(relevantHistory) {
        val map = mutableMapOf<String, Int>()
        relevantHistory.forEach { h ->
            h.details.forEach { (name, count) ->
                map[name] = (map[name] ?: 0) + count
            }
        }
        map.entries.sortedByDescending { it.value }
    }

    Scaffold(
        topBar = { DetailStatsTopBar(title = title, onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (period == StatPeriod.Week) {
                detailWeekDaysBreakdown(relevantHistory = relevantHistory, language = language)
            }
            detailTasbeehList(sortedTasbeehs = tasbeehMap, language = language)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailStatsTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                StudioIcon(StudioIcons.ArrowBack, contentDescription = stringResource(R.string.cd_back))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

private fun LazyListScope.detailWeekDaysBreakdown(
    relevantHistory: List<TasbeehHistory>,
    language: String
) {
    item {
        Text(stringResource(R.string.label_days), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        val weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val days = (0..6).map { weekStart.plusDays(it.toLong()) }
        val maxDayCount = days.maxOfOrNull { day ->
            relevantHistory.find { it.date == day.toString() }?.totalCount ?: 0
        } ?: 1
        val scale = if (maxDayCount == 0) 1 else maxDayCount

        days.forEach { day ->
            val count = relevantHistory.find { it.date == day.toString() }?.totalCount ?: 0
            val dayName = day.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
            val dayInitial = dayName.take(1)
            val dayColor = when (day.dayOfWeek) {
                DayOfWeek.MONDAY -> Color(0xFF4DB6AC)
                DayOfWeek.TUESDAY -> Color(0xFF64B5F6)
                DayOfWeek.WEDNESDAY -> Color(0xFFE57373)
                DayOfWeek.THURSDAY -> Color(0xFFFFD54F)
                DayOfWeek.FRIDAY -> Color(0xFFBA68C8)
                DayOfWeek.SATURDAY -> Color(0xFFF06292)
                DayOfWeek.SUNDAY -> Color(0xFF81C784)
            }
            DayStatItem(
                initial = dayInitial,
                name = dayName,
                count = formatNumber(count, language),
                progress = if (scale > 0) count.toFloat() / scale else 0f,
                color = dayColor
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun LazyListScope.detailTasbeehList(
    sortedTasbeehs: List<Map.Entry<String, Int>>,
    language: String
) {
    item {
        Text(stringResource(R.string.label_tasbeehs), style = MaterialTheme.typography.titleMedium)
    }
    if (sortedTasbeehs.isEmpty()) {
        item {
            Text(
                stringResource(R.string.no_data_period),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val maxTasbeehCount = sortedTasbeehs.maxOfOrNull { it.value } ?: 1
        items(sortedTasbeehs, key = { it.key }) { (name, count) ->
            val index = sortedTasbeehs.indexOfFirst { it.key == name }
            val color = when (index % 3) {
                0 -> Color(0xFF69F0AE)
                1 -> Color(0xFF40C4FF)
                else -> Color(0xFFFF5252)
            }
            val context = LocalContext.current
            val localizedName = AdhkarLibrary.getLocalizedName(context, name)
            TasbeehStatItem(
                rank = formatNumber(index + 1, language),
                name = localizedName,
                count = formatNumber(count, language),
                progress = if (maxTasbeehCount > 0) count.toFloat() / maxTasbeehCount else 0f,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
