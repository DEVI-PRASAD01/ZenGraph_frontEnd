package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class LibrarySession(
    @SerializedName("id")          val id:          Int    = 0,
    @SerializedName("title")       val title:       String = "",
    @SerializedName("category")    val category:    String = "",
    @SerializedName("duration")    val duration:    Int    = 10,
    @SerializedName("level")       val level:       String = "Beginner",
    @SerializedName("technique")   val technique:   String = "",
    @SerializedName("description") val description: String = ""
)

data class StartLibrarySessionRequest(
    @SerializedName("user_id")          val userId:          Int,
    @SerializedName("goal")             val goal:            String,
    @SerializedName("mood_before")      val moodBefore:      String,
    @SerializedName("experience_level") val experienceLevel: String,
    @SerializedName("session_name")     val sessionName:     String,
    @SerializedName("duration")         val duration:        Int,
    @SerializedName("techniques")       val techniques:      String,
    @SerializedName("match_score")      val matchScore:      Int
)

data class StartLibrarySessionResponse(
    @SerializedName("session_id")   val sessionId:   Int     = -1,
    @SerializedName("session_name") val sessionName: String? = null,
    @SerializedName("status")       val status:      String? = null,
    @SerializedName("message")      val message:     String? = null
)

data class LibraryGeneratePlanRequest(
    @SerializedName("user_id")  val userId:   Int,
    @SerializedName("title")    val title:    String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("category") val category: String
)

data class LibraryGeneratePlanResponse(
    @SerializedName("plan_id")  val planId:   Int    = -1,
    @SerializedName("title")    val title:    String = "",
    @SerializedName("duration") val duration: Int    = 10
)