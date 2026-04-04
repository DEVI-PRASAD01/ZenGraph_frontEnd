package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class FriendStreakItem(
    @SerializedName("user_id")         val user_id: Int,
    @SerializedName("name")            val name: String,
    @SerializedName("initials")        val initials: String,
    @SerializedName("streak")          val streak: Int,
    @SerializedName("meditated_today") val meditated_today: Boolean,
    @SerializedName("is_self")         val is_self: Boolean = false,
    @SerializedName("color_index")     val color_index: Int = 0
)

data class FriendStreaksResponse(
    @SerializedName("status")  val status: String,
    @SerializedName("friends") val friends: List<FriendStreakItem>
)

data class UserSearchItem(
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("name")    val name: String,
    @SerializedName("initials") val initials: String = ""
)

data class FindUserResponse(
    @SerializedName("status") val status: String,
    @SerializedName("users")  val users: List<UserSearchItem>
)

data class AddFriendRequest(
    @SerializedName("user_id")   val user_id: Int,
    @SerializedName("friend_id") val friend_id: Int
)

data class NudgeRequest(
    @SerializedName("user_id")   val user_id: Int,
    @SerializedName("friend_id") val friend_id: Int
)

// ── Challenge detail (nested inside CurrentChallengeResponse) ──
data class ChallengeDetail(
    @SerializedName("id")                 val id: Int,
    @SerializedName("title")              val title: String,
    @SerializedName("description")        val description: String? = null,
    @SerializedName("target_days")        val target_days: Int = 5,
    @SerializedName("week_start")         val week_start: String? = null,
    @SerializedName("week_end")           val week_end: String? = null,
    @SerializedName("total_participants") val total_participants: Int = 0
)

data class CurrentChallengeResponse(
    @SerializedName("status")    val status: String,
    @SerializedName("challenge") val challenge: ChallengeDetail? = null
)

data class JoinChallengeRequest(
    @SerializedName("challenge_id") val challenge_id: Int,
    @SerializedName("user_id")      val user_id: Int
)

data class LeaderboardItem(
    @SerializedName("user_id")        val user_id: Int,
    @SerializedName("name")           val name: String,
    @SerializedName("initials")       val initials: String,
    @SerializedName("rank")           val rank: Int,
    @SerializedName("days_completed") val days_completed: Int,
    @SerializedName("color_index")    val color_index: Int = 0
)

data class LeaderboardResponse(
    @SerializedName("status")      val status: String,
    @SerializedName("leaderboard") val leaderboard: List<LeaderboardItem>
)

data class MyChallengeProgressResponse(
    @SerializedName("status")           val status: String,
    @SerializedName("challenge_id")     val challenge_id: Int = 0,
    @SerializedName("days_completed")   val days_completed: Int = 0,
    @SerializedName("target_days")      val target_days: Int = 5,
    @SerializedName("is_joined")        val is_joined: Boolean = false,
    @SerializedName("progress_percent") val progress_percent: Int = 0
)