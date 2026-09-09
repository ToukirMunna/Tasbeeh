package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<TasbeehHistory>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: Int = 0,
    language: String = "en"
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf(stringResource(R.string.tab_daily), stringResource(R.string.tab_monthly))

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        StudioIcon(StudioIcons.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (history.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_history), style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    when (selectedTab) {
                        0 -> DailyHistoryList(history, language)
                        1 -> MonthlyHistoryList(history, language)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyHistoryList(history: List<TasbeehHistory>, language: String = "en") {
    val currentYear = LocalDate.now().year
    val groupedHistory = remember(history) {
        history.groupBy {
            val date = LocalDate.parse(it.date)
            YearMonth.from(date)
        }.toSortedMap(compareByDescending { it })
    }

    val yearsData = remember(groupedHistory) {
        val years = groupedHistory.keys.asSequence()
            .map { it.year }
            .distinct()
            .sortedDescending()
            .toList()
        years.map { year ->
            year to groupedHistory.filterKeys { it.year == year }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        yearsData.forEach { (year, monthsInYear) ->
            if (year == currentYear) {
                items(monthsInYear.entries.toList(), key = { "${it.key.year}_${it.key.monthValue}" }) { (yearMonth, historyItems) ->
                    MonthItem(yearMonth, historyItems, language)
                }
            } else {
                item(key = "year_$year") {
                    YearExpandableGroup(year = year, monthsData = monthsInYear, language = language)
                }
            }
        }
    }
}
