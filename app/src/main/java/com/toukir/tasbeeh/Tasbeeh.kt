package com.toukir.tasbeeh

import com.google.gson.annotations.SerializedName

enum class GoalDuration {
    @SerializedName("DAILY") DAILY,
    @SerializedName("WEEKLY") WEEKLY,
    @SerializedName("MONTHLY") MONTHLY,
    @SerializedName("YEARLY") YEARLY
}

data class TasbeehGoal(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("targetCount") val targetCount: Int,
    @SerializedName("currentCount") var currentCount: Int,
    @SerializedName("dailyCount") var dailyCount: Int = 0,
    @SerializedName("totalCount") var totalCount: Int = 0,
    @SerializedName("isGoal") val isGoal: Boolean = true,
    @SerializedName("duration") val duration: GoalDuration = GoalDuration.DAILY,
    @SerializedName("lastResetDate") val lastResetDate: Long = System.currentTimeMillis()
)
