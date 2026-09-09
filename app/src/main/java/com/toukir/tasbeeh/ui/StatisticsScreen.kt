package com.toukir.tasbeeh.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

enum class StatPeriod { Week, Month, Total }

@Composable
fun StatisticsScreen(
    history: List<TasbeehHistory>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    var selectedPeriod by remember { mutableStateOf<StatPeriod?>(null) }
    val summary = remember(history) { computeStatsSummary(history) }

    if (selectedPeriod != null) {
        BackHandler { selectedPeriod = null }
        DetailStatsScreen(
            period = selectedPeriod!!,
            history = history,
            onBack = { selectedPeriod = null },
            language = language
        )
    } else {
        Scaffold(
            modifier = modifier,
            topBar = { StatsTopBar(onBack = onBack) }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsDashboardCards(
                    weekCount = summary.weekCount,
                    monthCount = summary.monthCount,
                    totalCount = summary.totalCount,
                    language = language,
                    onPeriodSelect = { selectedPeriod = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                StatsAveragesSection(
                    dailyAverage = summary.dailyAverage,
                    weeklyAverage = summary.weeklyAverage,
                    monthlyAverage = summary.monthlyAverage,
                    language = language
                )
            }
        }
    }
}

private data class StatsSummary(
    val weekCount: Int,
    val monthCount: Int,
    val totalCount: Int,
    val dailyAverage: Int,
    val weeklyAverage: Int,
    val monthlyAverage: Int
)

private fun computeStatsSummary(history: List<TasbeehHistory>): StatsSummary {
    val weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val weekCount = history.filter {
        val date = LocalDate.parse(it.date)
        !date.isBefore(weekStart) && !date.isAfter(LocalDate.now())
    }.sumOf { it.totalCount }

    val monthStart = LocalDate.now().withDayOfMonth(1)
    val monthCount = history.filter {
        val date = LocalDate.parse(it.date)
        !date.isBefore(monthStart) && !date.isAfter(LocalDate.now())
    }.sumOf { it.totalCount }

    val totalCount = history.sumOf { it.totalCount }
    val firstDate = history.asSequence().map { LocalDate.parse(it.date) }.minOrNull() ?: LocalDate.now()
    val daysSinceStart = ChronoUnit.DAYS.between(firstDate, LocalDate.now()) + 1

    val dailyAverage = if (daysSinceStart > 0) (totalCount / daysSinceStart).toInt() else 0
    val weeklyAverage = dailyAverage * 7
    val monthlyAverage = dailyAverage * 30

    return StatsSummary(weekCount, monthCount, totalCount, dailyAverage, weeklyAverage, monthlyAverage)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.stats_title), style = MaterialTheme.typography.titleLarge) },
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

@Composable
private fun StatsDashboardCards(
    weekCount: Int,
    monthCount: Int,
    totalCount: Int,
    language: String,
    onPeriodSelect: (StatPeriod) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
            title = stringResource(R.string.stats_week),
            value = formatNumber(weekCount, language),
            color = Color(0xFFFFF9C4),
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            onClick = { onPeriodSelect(StatPeriod.Week) }
        )
        StatCard(
            title = stringResource(R.string.stats_month),
            value = formatNumber(monthCount, language),
            color = Color(0xFFFFCCBC),
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            onClick = { onPeriodSelect(StatPeriod.Month) }
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
            title = stringResource(R.string.stats_total),
            value = formatNumber(totalCount, language),
            color = Color(0xFFE1BEE7),
            textColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            onClick = { onPeriodSelect(StatPeriod.Total) }
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatsAveragesSection(
    dailyAverage: Int,
    weeklyAverage: Int,
    monthlyAverage: Int,
    language: String
) {
    Text(stringResource(R.string.stats_average), style = MaterialTheme.typography.titleMedium)
    AverageItem(
        title = stringResource(R.string.avg_daily),
        subtitle = stringResource(R.string.label_tasbeeh_count),
        value = formatNumber(dailyAverage, language)
    )
    AverageItem(
        title = stringResource(R.string.avg_weekly),
        subtitle = stringResource(R.string.label_tasbeeh_count),
        value = formatNumber(weeklyAverage, language)
    )
    AverageItem(
        title = stringResource(R.string.avg_monthly),
        subtitle = stringResource(R.string.label_tasbeeh_count),
        value = formatNumber(monthlyAverage, language)
    )
}
