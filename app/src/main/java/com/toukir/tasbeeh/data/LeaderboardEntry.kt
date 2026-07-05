package com.toukir.tasbeeh.data

import com.google.gson.annotations.SerializedName

data class LeaderboardEntry(
    @SerializedName("userId") val userId: String = "",
    @SerializedName("username") val username: String = "",
    @SerializedName("dailyCount") val dailyCount: Int = 0,
    @SerializedName("weeklyCount") val weeklyCount: Int = 0,
    @SerializedName("monthlyCount") val monthlyCount: Int = 0,
    @SerializedName("lastUpdated") val lastUpdated: Long = 0,
    @SerializedName("isAnonymous") val isAnonymous: Boolean = false,
    @SerializedName("isMale") val isMale: Boolean = true
)
