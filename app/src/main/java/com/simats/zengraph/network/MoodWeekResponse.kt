package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class MoodDayItem(
    @SerializedName("day")          val day: String,
    @SerializedName("date")         val date: String,
    @SerializedName("mood")         val mood: String?,
    @SerializedName("emoji")        val emoji: String,
    @SerializedName("bg_color")     val bg_color: String,
    @SerializedName("border_color") val border_color: String,
    @SerializedName("label")        val label: String,
    @SerializedName("logged")       val logged: Boolean,
    @SerializedName("is_future")    val is_future: Boolean
)

data class MoodWeekResponse(
    @SerializedName("status")       val status: String,
    @SerializedName("mood_history") val mood_history: List<String?>? = null,
    @SerializedName("week")         val week: List<MoodDayItem>? = null,
    @SerializedName("last_logged") val last_logged: MoodDayItem?
)