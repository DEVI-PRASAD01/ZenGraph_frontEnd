package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class GeneratePlanResponse(
    @SerializedName("plan_id") val planId: Int = -1,
    @SerializedName("title") val title: String = "",
    @SerializedName("duration") val duration: Int = 0,
    @SerializedName("match_percent") val matchPercent: Int = 0,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)
