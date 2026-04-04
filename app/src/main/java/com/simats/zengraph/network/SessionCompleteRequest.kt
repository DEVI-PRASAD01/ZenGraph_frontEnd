package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionCompleteRequest(
    @SerializedName("mood_after") val moodAfter: String,
    @SerializedName("actual_duration") val actualDuration: Int = 0,
    @SerializedName("notes") val notes: String = ""
)
