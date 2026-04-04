package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SendMessageRequest(
    @SerializedName("sender_id")   val sender_id:   Int,
    @SerializedName("receiver_id") val receiver_id: Int,
    @SerializedName("message")     val message:     String
)

data class MarkReadRequest(
    @SerializedName("user_id")   val user_id:   Int,
    @SerializedName("friend_id") val friend_id: Int
)

data class ChatMessage(
    @SerializedName("id")          val id:          Int,
    @SerializedName("sender_id")   val sender_id:   Int,
    @SerializedName("receiver_id") val receiver_id: Int,
    @SerializedName("message")     val message:     String,
    @SerializedName("is_read")     val is_read:     Boolean,
    @SerializedName("time")        val time:        String,
    @SerializedName("is_mine")     val is_mine:     Boolean
)

data class ChatMessagesResponse(
    @SerializedName("status")      val status:      String,
    @SerializedName("friend_name") val friend_name: String,
    @SerializedName("friend_id")   val friend_id:   Int,
    @SerializedName("messages")    val messages:    List<ChatMessage>
)

data class UnreadCountResponse(
    @SerializedName("status")       val status:        String,
    @SerializedName("unread_map")   val unread_map:    Map<String, Int>,
    @SerializedName("total_unread") val total_unread:  Int
)

data class ChatListItem(
    @SerializedName("friend_id")    val friend_id:    Int,
    @SerializedName("friend_name")  val friend_name:  String,
    @SerializedName("initials")     val initials:     String,
    @SerializedName("last_message") val last_message: String,
    @SerializedName("last_time")    val last_time:    String,
    @SerializedName("unread_count") val unread_count: Int,
    @SerializedName("color_index")  val color_index:  Int
)

data class ChatListResponse(
    @SerializedName("status")    val status:    String,
    @SerializedName("chat_list") val chat_list: List<ChatListItem>
)