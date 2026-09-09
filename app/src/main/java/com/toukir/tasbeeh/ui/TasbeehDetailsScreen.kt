package com.toukir.tasbeeh.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.TasbeehGoal
import com.toukir.tasbeeh.data.AdhkarInfo
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import com.toukir.tasbeeh.utils.formatNumber
import java.util.Locale

@Composable
fun TasbeehDetailsScreen(
    goal: TasbeehGoal,
    history: List<TasbeehHistory>,
    customDetails: Map<String, AdhkarInfo> = emptyMap(),
    onBack: () -> Unit,
    onCountClick: () -> Unit,
    onEditDetails: (AdhkarInfo) -> Unit,
    language: String = "en"
) {
    val usageHistory = remember(history, goal.name) {
        history.mapNotNull { entry ->
            val count = entry.details[goal.name]
            if (count != null && count > 0) entry.date to count else null
        }.sortedByDescending { it.first }
    }

    val context = LocalContext.current
    val adhkarInfo = remember(goal.name, customDetails, language, context) {
        customDetails[goal.name] ?: AdhkarLibrary.getLocalizedInfo(context, goal.name) ?: AdhkarLibrary.getInfo(goal.name)
    }
    val displayGoalName = remember(goal.name, language, context) {
        AdhkarLibrary.getLocalizedName(context, goal.name)
    }

    Scaffold(
        topBar = {
            TasbeehDetailsTopBar(
                displayGoalName = displayGoalName,
                onBack = onBack,
                onEditClick = { onEditDetails(adhkarInfo ?: AdhkarInfo(goal.name, "", "", "")) }
            )
        },
        bottomBar = { TasbeehDetailsBottomBar(onCountClick = onCountClick) }
    ) { padding ->
        TasbeehDetailsList(
            padding = padding,
            adhkarInfo = adhkarInfo,
            totalCount = goal.totalCount,
            usageHistory = usageHistory,
            language = language
        )
    }
}

@Composable
private fun TasbeehDetailsList(
    padding: PaddingValues,
    adhkarInfo: AdhkarInfo?,
    totalCount: Int,
    usageHistory: List<Pair<String, Int>>,
    language: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (adhkarInfo != null && (adhkarInfo.arabic.isNotEmpty() || adhkarInfo.translation.isNotEmpty() || adhkarInfo.virtue.isNotEmpty())) {
            item { TasbeehDetailsHeaderCard(adhkarInfo) }
        }

        item {
            TasbeehDetailsStatsRow(
                totalCount = totalCount,
                daysUsed = usageHistory.size,
                language = language
            )
        }

        item {
            Column {
                Text(
                    text = stringResource(R.string.details_history),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        if (usageHistory.isEmpty()) {
            item { TasbeehDetailsEmptyHistory() }
        } else {
            items(usageHistory, key = { it.first }) { (date, count) ->
                HistoryLogItem(date = date, count = count, language = language)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun TasbeehDetailsStatsRow(
    totalCount: Int,
    daysUsed: Int,
    language: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailsStatCard(
            title = stringResource(R.string.details_total_count),
            value = formatNumber(totalCount, language),
            iconRes = StudioIcons.Analytics,
            modifier = Modifier.weight(1f)
        )
        DetailsStatCard(
            title = stringResource(R.string.details_days_used),
            value = formatNumber(daysUsed, language),
            iconRes = StudioIcons.CalendarMonth,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TasbeehDetailsEmptyHistory() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StudioIcon(
            iconRes = StudioIcons.History,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_history),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}
