package com.toukir.tasbeeh.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.TasbeehHistory
import com.toukir.tasbeeh.utils.formatNumber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeeklyBarChart(history: List<TasbeehHistory>, language: String = "en") {
    val context = LocalContext.current
    val kalpurushTypeface = remember { ResourcesCompat.getFont(context, R.font.kalpurush) }

    val today = LocalDate.now()
    val last7Days = (0..6).map { i -> today.minusDays(6L - i) }
    val dataPoints = last7Days.map { date -> history.find { it.date == date.toString() }?.totalCount ?: 0 }
    val maxCount = dataPoints.maxOrNull() ?: 100
    val maxY = if (maxCount == 0) 10f else maxCount.toFloat() * 1.2f

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val labelColor = onSurface.toArgb()
    
    val animatedProgress = dataPoints.map { count ->
        animateFloatAsState(
            targetValue = count.toFloat(),
            animationSpec = tween(durationMillis = 1000, delayMillis = 100),
            label = "BarHeight"
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val bottomPadding = 32.dp.toPx()
        val topPadding = 24.dp.toPx() // Space for numbers above bars
        val chartHeight = height - bottomPadding - topPadding
        val barWidth = (width / dataPoints.size) * 0.4f
        val spacing = (width / dataPoints.size)

        dataPoints.forEachIndexed { index, count ->
            val x = index * spacing + (spacing - barWidth) / 2
            val animatedCount = animatedProgress[index].value
            val barHeight = (animatedCount / maxY) * chartHeight
            val y = chartHeight - barHeight + topPadding

            // Draw Background Bar (Track)
            drawRoundRect(
                color = surfaceVariant.copy(alpha = 0.5f),
                topLeft = Offset(x, topPadding),
                size = Size(barWidth, chartHeight),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // Draw Active Bar
            if (barHeight > 0) {
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }

            // Draw the count number above the bar
            if (count > 0) {
                val formattedCount = formatNumber(count, language)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        formattedCount,
                        x + barWidth / 2,
                        y - 8.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = labelColor
                            textSize = 10.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                            typeface = kalpurushTypeface
                        }
                    )
                }
            }

            // Draw Date Label below axis
            val date = last7Days[index]
            val locale = if (language == "bn") Locale.forLanguageTag("bn-BD") else Locale.getDefault()
            val dayLabel = date.format(DateTimeFormatter.ofPattern("EEE", locale))
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    dayLabel,
                    x + barWidth / 2,
                    chartHeight + topPadding + 24.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = labelColor
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = kalpurushTypeface
                    }
                )
            }
        }
    }
}
