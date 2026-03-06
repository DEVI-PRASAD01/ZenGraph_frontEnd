package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("detail") val detail: String? = null
)
