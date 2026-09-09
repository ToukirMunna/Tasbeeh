package com.toukir.tasbeeh.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.ui.common.StudioIcon
import com.toukir.tasbeeh.ui.theme.StudioIcons
import java.time.YearMonth

@Composable
fun YearExpandableGroup(
    year: Int,
    monthsData: Map<YearMonth, List<TasbeehHistory>>,
    language: String = "en"
) {
    var expanded by remember { mutableStateOf(false) }
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
