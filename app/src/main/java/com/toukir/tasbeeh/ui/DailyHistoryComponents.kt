package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil

@Composable
fun DailyHistoryList(history: List<TasbeehHistory>, language: String = "en") {
    val pageSize = 20
    val sortedHistory = remember(history) { history.sortedByDescending { it.date } }
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = ceil(sortedHistory.size.toFloat() / pageSize).toInt().coerceAtLeast(1)

    val pagedHistory = remember(sortedHistory, currentPage) {
        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(sortedHistory.size)
        if (start < sortedHistory.size) sortedHistory.subList(start, end) else emptyList()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pagedHistory, key = { it.date }) { dayHistory ->
                DailyHistoryItem(dayHistory, language)
            }
        }

        if (totalPages > 1) {
            DailyHistoryPaginationBar(
                currentPage = currentPage,
                totalPages = totalPages,
                onPrevious = { if (currentPage > 0) currentPage-- },
                onNext = { if (currentPage < (totalPages - 1)) currentPage++ }
            )
        }
    }
}

@Composable
private fun DailyHistoryPaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(tonalElevation = 4.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPrevious,
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
                onClick = onNext,
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

@Composable
fun DailyHistoryItem(history: TasbeehHistory, language: String = "en") {
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
                DailyHistoryDetailsList(history.details, language)
            }
        }
    }
}

@Composable
private fun DailyHistoryDetailsList(details: Map<String, Int>, language: String) {
    val context = LocalContext.current
    details.entries.forEach { (name, count) ->
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
