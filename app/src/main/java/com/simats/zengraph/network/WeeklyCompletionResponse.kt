package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class WeeklyCompletionResponse(
    @SerializedName("0") val mon: Int,
    @SerializedName("1") val tue: Int,
    @SerializedName("2") val wed: Int,
    @SerializedName("3") val thu: Int,
    @SerializedName("4") val fri: Int,
    @SerializedName("5") val sat: Int,
    @SerializedName("6") val sun: Int
)
