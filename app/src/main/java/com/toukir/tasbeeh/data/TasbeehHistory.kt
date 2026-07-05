package com.toukir.tasbeeh.data

import com.google.gson.annotations.SerializedName

data class TasbeehHistory(
    @SerializedName("date") val date: String,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("details") val details: Map<String, Int> = emptyMap()
)
