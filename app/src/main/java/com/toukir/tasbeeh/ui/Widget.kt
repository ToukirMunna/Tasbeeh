package com.toukir.tasbeeh.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.widget.RemoteViews
import com.toukir.tasbeeh.R
import com.toukir.tasbeeh.data.TasbeehRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.max

class TasbeehWidget : AppWidgetProvider() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_REFRESH = "com.toukir.tasbeeh.ACTION_WIDGET_REFRESH"
    }

    private fun List<com.toukir.tasbeeh.TasbeehGoal>.distinctDailyTotal(): Int {
        return groupBy { it.name }.values.sumOf { goals -> goals.maxOf { it.dailyCount } }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE || 
            intent.action == ACTION_REFRESH) {
            
            val pendingResult = goAsync()
            scope.launch {
                try {
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, TasbeehWidget::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                    
                    updateAllWidgets(context, appWidgetManager, appWidgetIds)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Handled in onReceive to support goAsync
    }

    private suspend fun updateAllWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val repository = TasbeehRepository(context)
        // Ensure daily reset logic runs so we have accurate today counts
        repository.checkAndResetDailyCounts()
        
        val goals = repository.goalsFlow.first()
        val history = repository.historyFlow.first()
        
        // Calculate today's count using dailyCount for accurate daily progress
        val todayCount = goals.distinctDailyTotal()
        
        // Calculate Streak
        val sortedHistory = history.sortedByDescending { it.date }
        var streak = 0
        
        val today = LocalDate.now()
        var currentCheckDate = today.minusDays(1)
        var consecutive = 0
        
        // Check yesterday, day before, etc.
        while (true) {
            val dateStr = currentCheckDate.toString()
            val entry = sortedHistory.find { it.date == dateStr }
            if (entry != null && entry.totalCount > 0) {
                consecutive++
                currentCheckDate = currentCheckDate.minusDays(1)
            } else {
                break
            }
        }
        
        streak = if (todayCount > 0) consecutive + 1 else consecutive

        // Weekly Data for Chart (Last 7 days including today)
        val weeklyCounts = mutableListOf<Int>()
        val weeklyLabels = mutableListOf<String>()
        
        for (i in 6 downTo 0) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.toString()
            val count = if (date == today) {
                todayCount
            } else {
                sortedHistory.find { it.date == dateStr }?.totalCount ?: 0
            }
            weeklyCounts.add(count)
            weeklyLabels.add(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1))
        }

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, todayCount, streak, weeklyCounts, weeklyLabels)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        todayCount: Int,
        streak: Int,
        weeklyCounts: List<Int>,
        weeklyLabels: List<String>
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        
        // Update Text
        views.setTextViewText(R.id.widget_streak_text, "Streak: $streak 🔥")
        views.setTextViewText(R.id.widget_today_text, "Today: $todayCount")
        
        // Draw Chart
        val bitmap = drawChart(context, weeklyCounts, weeklyLabels)
        views.setImageViewBitmap(R.id.widget_chart_image, bitmap)

        // Open App on click
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = android.app.PendingIntent.getActivity(
            context, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun drawChart(context: Context, counts: List<Int>, labels: List<String>): Bitmap {
        val width = 400
        val height = 200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val maxCount = max(counts.maxOrNull() ?: 1, 10) // Min max scale 10
        
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 24f
            color = android.graphics.Color.WHITE // Assuming dark theme or high contrast
            textAlign = Paint.Align.CENTER
        }
        
        val barPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#FFD700") // Gold
            style = Paint.Style.FILL
        }
        
        val barWidth = 30f
        val spacing = (width - (barWidth * 7)) / 8f
        val bottomMargin = 40f
        val availableHeight = height - bottomMargin - 10f

        for (i in counts.indices) {
            val count = counts[i]
            val x = spacing + (i * (barWidth + spacing)) + (barWidth / 2)
            
            // Draw Label
            // paint.color = android.graphics.Color.parseColor("#CCCCCC") // keep white for visibility
            canvas.drawText(labels[i], x, height - 10f, paint)
            
            // Draw Bar
            val barHeight = (count.toFloat() / maxCount) * availableHeight
            // Min height for visibility
            val drawHeight = if (count > 0) max(barHeight, 5f) else 2f
            
            val barRect = RectF(
                x - barWidth/2, 
                (height - bottomMargin) - drawHeight, 
                x + barWidth/2, 
                height - bottomMargin
            )
            
            // Highlight today (last item)
            if (i == counts.lastIndex) {
                 barPaint.alpha = 255
            } else {
                 barPaint.alpha = 150 // Dimmer for past days
            }
            
            canvas.drawRoundRect(barRect, 8f, 8f, barPaint)
        }

        return bitmap
    }
}
