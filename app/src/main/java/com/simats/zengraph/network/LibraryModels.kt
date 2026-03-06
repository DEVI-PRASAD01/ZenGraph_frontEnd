package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class LibrarySession(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("duration") val duration: Int = 10
)

data class StartLibrarySessionRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int
)

data class StartLibrarySessionResponse(
    @SerializedName("session_id") val sessionId: Int = -1,
    @SerializedName("session_name") val sessionName: String? = null,
    @SerializedName("planned_duration") val plannedDuration: Int = 0,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)

data class LibraryGeneratePlanRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("category") val category: String
)

data class LibraryGeneratePlanResponse(
    @SerializedName("plan_id") val planId: Int = -1,
    @SerializedName("title") val title: String = "",
    @SerializedName("duration") val duration: Int = 10
)
