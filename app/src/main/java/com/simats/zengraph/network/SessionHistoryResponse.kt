package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionHistoryItem(
    @SerializedName("id")            val id: Int,
    @SerializedName("session_name")  val session_name: String?,
    @SerializedName("goal")          val goal: String?,
    @SerializedName("mood_before")   val mood_before: String?,
    @SerializedName("mood_after")    val mood_after: String?,
    @SerializedName("duration")      val duration: Int?,
    @SerializedName("status")        val status: String?,
    @SerializedName("started_at")    val started_at: String?,
    @SerializedName("completed_at")  val completed_at: String?
)

data class SessionHistoryResponse(
    @SerializedName("status")   val status: String,
    @SerializedName("sessions") val sessions: List<SessionHistoryItem>
)
