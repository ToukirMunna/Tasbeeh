package com.toukir.tasbeeh.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.utils.formatNumber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil

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
                        StudioIcon(StudioIcons.ArrowBack, contentDescription = "Back")
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
fun DailyHistoryList(history: List<TasbeehHistory>, language: String = "en") {
    val pageSize = 20
    val sortedHistory = remember(history) {
        history.sortedByDescending { it.date }
    }
    
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = ceil(sortedHistory.size.toFloat() / pageSize).toInt().coerceAtLeast(1)
    
    val pagedHistory = remember(sortedHistory, currentPage) {
        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(sortedHistory.size)
        if (start < sortedHistory.size) {
            sortedHistory.subList(start, end)
        } else {
            emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pagedHistory) { dayHistory ->
                DailyHistoryItem(dayHistory, language)
            }
        }

        if (totalPages > 1) {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (currentPage > 0) currentPage-- },
                        enabled = currentPage > 0,
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        StudioIcon(StudioIcons.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.previous))
                    }

                    Text(
                        text = stringResource(R.string.page_format, currentPage + 1, totalPages),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = { if (currentPage < (totalPages - 1)) currentPage++ },
                        enabled = currentPage < (totalPages - 1),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(stringResource(R.string.next))
                        Spacer(modifier = Modifier.width(4.dp))
                        StudioIcon(StudioIcons.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DailyHistoryItem(history: TasbeehHistory, language: String = "en") {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
    val date = LocalDate.parse(history.date)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = formatNumber(history.totalCount, language),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            if (history.details.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                history.details.entries.forEach { (name, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val localizedName = AdhkarLibrary.getLocalizedName(context, name)
                        Text(
                            text = localizedName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatNumber(count, language),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Group months by year
        val years = groupedHistory.keys.asSequence()
            .map { it.year }
            .distinct()
            .sortedDescending()
            .toList()
        
        years.forEach { year ->
            val monthsInYear = groupedHistory.filterKeys { it.year == year }
            
            if (year == currentYear) {
                // Directly show months for current year
                items(monthsInYear.entries.toList()) { (yearMonth, historyItems) ->
                    MonthItem(yearMonth, historyItems, language)
                }
            } else {
                // Wrap previous years
                item {
                    YearExpandableGroup(year = year, monthsData = monthsInYear, language = language)
                }
            }
        }
    }
}

@Composable
fun YearExpandableGroup(
    year: Int,
    monthsData: Map<YearMonth, List<TasbeehHistory>>,
    language: String = "en"
) {
    var expanded by remember { mutableStateOf(value = false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rotation")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = year.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StudioIcon(
                    iconRes = StudioIcons.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotationState)
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    monthsData.entries.sortedByDescending { it.key }.forEach { (yearMonth, historyItems) ->
                        MonthItem(yearMonth, historyItems, language)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
