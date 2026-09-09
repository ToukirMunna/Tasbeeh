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
    val chartData = remember(history) { computeWeeklyChartData(history) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val labelColor = MaterialTheme.colorScheme.onSurface.toArgb()
    
    val animatedProgress = chartData.dataPoints.map { count ->
        animateFloatAsState(
            targetValue = count.toFloat(),
            animationSpec = tween(durationMillis = 1000, delayMillis = 100),
            label = "BarHeight"
        )
    }

    WeeklyBarChartCanvas(
        chartData = chartData,
        animatedProgress = animatedProgress,
        primaryColor = primaryColor,
        surfaceVariant = surfaceVariant,
        labelColor = labelColor,
        kalpurushTypeface = kalpurushTypeface,
        language = language
    )
}

@Composable
private fun WeeklyBarChartCanvas(
    chartData: WeeklyChartData,
    animatedProgress: List<androidx.compose.runtime.State<Float>>,
    primaryColor: androidx.compose.ui.graphics.Color,
    surfaceVariant: androidx.compose.ui.graphics.Color,
    labelColor: Int,
    kalpurushTypeface: android.graphics.Typeface?,
    language: String
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val bottomPadding = 32.dp.toPx()
        val topPadding = 24.dp.toPx()
        val chartHeight = height - bottomPadding - topPadding
        val barWidth = (width / chartData.dataPoints.size) * 0.4f
        val spacing = (width / chartData.dataPoints.size)

        chartData.dataPoints.forEachIndexed { index, count ->
            val x = index * spacing + (spacing - barWidth) / 2
            val animatedCount = animatedProgress[index].value
            val barHeight = (animatedCount / chartData.maxY) * chartHeight
            val y = chartHeight - barHeight + topPadding

            drawBarTrackAndFill(
                x = x,
                y = y,
                topPadding = topPadding,
                barWidth = barWidth,
                chartHeight = chartHeight,
                barHeight = barHeight,
                trackColor = surfaceVariant.copy(alpha = 0.5f),
                activeColor = primaryColor
            )

            val date = chartData.last7Days[index]
            val locale = if (language == "bn") Locale.forLanguageTag("bn-BD") else Locale.getDefault()
            val dayLabel = date.format(DateTimeFormatter.ofPattern("EEE", locale))
            val formattedCount = if (count > 0) formatNumber(count, language) else null

            drawBarTexts(
                formattedCount = formattedCount,
                dayLabel = dayLabel,
                x = x,
                y = y,
                barWidth = barWidth,
                chartHeight = chartHeight,
                topPadding = topPadding,
                labelColor = labelColor,
                typeface = kalpurushTypeface
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBarTrackAndFill(
    x: Float,
    y: Float,
    topPadding: Float,
    barWidth: Float,
    chartHeight: Float,
    barHeight: Float,
    trackColor: androidx.compose.ui.graphics.Color,
    activeColor: androidx.compose.ui.graphics.Color
) {
    drawRoundRect(
        color = trackColor,
        topLeft = Offset(x, topPadding),
        size = Size(barWidth, chartHeight),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
    )
    if (barHeight > 0) {
        drawRoundRect(
            color = activeColor,
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBarTexts(
    formattedCount: String?,
    dayLabel: String,
    x: Float,
    y: Float,
    barWidth: Float,
    chartHeight: Float,
    topPadding: Float,
    labelColor: Int,
    typeface: android.graphics.Typeface?
) {
    drawContext.canvas.nativeCanvas.apply {
        if (formattedCount != null) {
            val countPaint = android.graphics.Paint().apply {
                color = labelColor
                textSize = 10.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
                this.typeface = typeface
            }
            drawText(formattedCount, x + barWidth / 2, y - 8.dp.toPx(), countPaint)
        }
        val labelPaint = android.graphics.Paint().apply {
            color = labelColor
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            this.typeface = typeface
        }
        drawText(dayLabel, x + barWidth / 2, chartHeight + topPadding + 24.dp.toPx(), labelPaint)
    }
}

private data class WeeklyChartData(
    val last7Days: List<LocalDate>,
    val dataPoints: List<Int>,
    val maxY: Float
)

private fun computeWeeklyChartData(history: List<TasbeehHistory>): WeeklyChartData {
    val today = LocalDate.now()
    val last7Days = (0..6).map { i -> today.minusDays(6L - i) }
    val dataPoints = last7Days.map { date -> history.find { it.date == date.toString() }?.totalCount ?: 0 }
    val maxCount = dataPoints.maxOrNull() ?: 100
    val maxY = if (maxCount == 0) 10f else maxCount.toFloat() * 1.2f
    return WeeklyChartData(last7Days, dataPoints, maxY)
}
