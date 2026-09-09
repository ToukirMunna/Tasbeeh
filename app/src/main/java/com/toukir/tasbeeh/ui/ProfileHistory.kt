package com.toukir.tasbeeh.ui

import android.content.Context

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.AdhkarLibrary
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.utils.formatNumber
import com.toukir.tasbeeh.utils.getLocaleForLanguage
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SectionHeader(title: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun MonthItem(
    yearMonth: YearMonth,
    historyItems: List<TasbeehHistory>,
    language: String = "en"
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    val monthTotal = historyItems.sumOf { it.totalCount }
    val formatter = remember(language) { DateTimeFormatter.ofPattern("MMMM yyyy", getLocaleForLanguage(language)) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = if (expanded) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            MonthItemHeader(
                yearMonth = yearMonth,
                formatter = formatter,
                daysActive = historyItems.size,
                monthTotal = monthTotal,
                language = language,
                rotationState = rotationState
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                MonthItemExpandedDetails(
                    historyItems = historyItems,
                    language = language,
                    context = context
                )
            }
        }
    }
}

@Composable
private fun MonthItemHeader(
    yearMonth: YearMonth,
    formatter: DateTimeFormatter,
    daysActive: Int,
    monthTotal: Int,
    language: String,
    rotationState: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = yearMonth.format(formatter),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.days_active, formatNumber(daysActive, language)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = formatNumber(monthTotal, language),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StudioIcon(
                iconRes = StudioIcons.ExpandMore,
                contentDescription = null,
                modifier = Modifier.rotate(rotationState),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MonthItemExpandedDetails(
    historyItems: List<TasbeehHistory>,
    language: String,
    context: Context
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        val tasbeehCounts = mutableMapOf<String, Int>()
        historyItems.forEach { dayHistory ->
            dayHistory.details.forEach { (name, count) ->
                tasbeehCounts[name] = (tasbeehCounts[name] ?: 0) + count
            }
        }

        tasbeehCounts.entries.sortedByDescending { it.value }.forEach { (name, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val localizedName = AdhkarLibrary.getLocalizedName(context, name)
                    Text(localizedName, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = formatNumber(count, language),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
