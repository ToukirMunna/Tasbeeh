package com.toukir.tasbeeh.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.utils.formatNumber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

enum class StatPeriod { Week, Month, Total }

@Composable
fun StatisticsScreen(
    history: List<TasbeehHistory>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    language: String = "en"
) {
    var selectedPeriod by remember { mutableStateOf<StatPeriod?>(null) }

    // Dashboard Calculations
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
    
    // Averages
    val firstDate = history.asSequence().map { LocalDate.parse(it.date) }.minOrNull() ?: LocalDate.now()
    val daysSinceStart = ChronoUnit.DAYS.between(firstDate, LocalDate.now()) + 1
    
    val dailyAverage = if (daysSinceStart > 0) (totalCount / daysSinceStart).toInt() else 0
    val weeklyAverage = dailyAverage * 7
    val monthlyAverage = dailyAverage * 30

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
            topBar = {
                @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text(stringResource(R.string.stats_title), style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Grid (2x2)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = stringResource(R.string.stats_week), 
                        value = formatNumber(weekCount, language), 
                        color = Color(0xFFFFF9C4), // Light Yellow
                        textColor = Color.Black,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPeriod = StatPeriod.Week }
                    )
                    StatCard(
                        title = stringResource(R.string.stats_month), 
                        value = formatNumber(monthCount, language), 
                        color = Color(0xFFFFCCBC), // Light Red/Orange
                        textColor = Color.Black,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPeriod = StatPeriod.Month }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = stringResource(R.string.stats_total), 
                        value = formatNumber(totalCount, language), 
                        color = Color(0xFFE1BEE7), // Light Purple
                        textColor = Color.Black,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPeriod = StatPeriod.Total }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(stringResource(R.string.stats_average), style = MaterialTheme.typography.titleMedium)
                
                // Average List
                AverageItem(title = stringResource(R.string.avg_daily), subtitle = stringResource(R.string.label_tasbeeh_count), value = formatNumber(dailyAverage, language))
                AverageItem(title = stringResource(R.string.avg_weekly), subtitle = stringResource(R.string.label_tasbeeh_count), value = formatNumber(weeklyAverage, language))
                AverageItem(title = stringResource(R.string.avg_monthly), subtitle = stringResource(R.string.label_tasbeeh_count), value = formatNumber(monthlyAverage, language))
            }
        }
    }
}

@Composable
fun DetailStatsScreen(
    period: StatPeriod,
    history: List<TasbeehHistory>,
    onBack: () -> Unit,
    language: String = "en"
) {
    // 1. Prepare Data
    val (title, relevantHistory) = when (period) {
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
        StatPeriod.Total -> {
            "All Time" to history
        }
    }

    // 2. Aggregate Tasbeehs
    val tasbeehMap = mutableMapOf<String, Int>()
    relevantHistory.forEach { h ->
        h.details.forEach { (name, count) ->
            tasbeehMap[name] = (tasbeehMap[name] ?: 0) + count
        }
    }
    val sortedTasbeehs = tasbeehMap.entries.sortedByDescending { it.value }
    val maxTasbeehCount = sortedTasbeehs.maxOfOrNull { it.value } ?: 1
    val context = LocalContext.current

    Scaffold(
        topBar = {
            @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Week View: Show Days Breakdown
            if (period == StatPeriod.Week) {
                item {
                    Text(stringResource(R.string.label_days), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val days = (0..6).map { weekStart.plusDays(it.toLong()) }
                    
                    val maxDayCount = days.maxOfOrNull { day ->
                        relevantHistory.find { it.date == day.toString() }?.totalCount ?: 0
                    } ?: 1
                    
                    // Prevent zero division
                    val scale = if (maxDayCount == 0) 1 else maxDayCount

                    days.forEach { day ->
                        val count = relevantHistory.find { it.date == day.toString() }?.totalCount ?: 0
                        val dayName = day.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
                        val dayInitial = dayName.take(1)
                        
                        // Custom colors for days like in screenshot
                        val dayColor = when(day.dayOfWeek) {
                            DayOfWeek.MONDAY -> Color(0xFF4DB6AC) // Teal
                            DayOfWeek.TUESDAY -> Color(0xFF64B5F6) // Blue
                            DayOfWeek.WEDNESDAY -> Color(0xFFE57373) // Red
                            DayOfWeek.THURSDAY -> Color(0xFFFFD54F) // Yellow
                            DayOfWeek.FRIDAY -> Color(0xFFBA68C8) // Purple
                            DayOfWeek.SATURDAY -> Color(0xFFF06292) // Pink
                            DayOfWeek.SUNDAY -> Color(0xFF81C784) // Green
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

            // Tasbeeh List
            item {
                Text(stringResource(R.string.label_tasbeehs), style = MaterialTheme.typography.titleMedium)
            }
            
            if (sortedTasbeehs.isEmpty()) {
                item {
                     Text(stringResource(R.string.no_data_period), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            } else {
                items(sortedTasbeehs.toList()) { (name, count) ->
                    val index = sortedTasbeehs.indexOfFirst { it.key == name }
                    // Assign colors based on index or name hash for consistency
                    val color = when (index % 3) {
                        0 -> Color(0xFF69F0AE) // Greenish
                        1 -> Color(0xFF40C4FF) // Blueish
                        else -> Color(0xFFFF5252) // Reddish
                    }
                    
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
    }
}
