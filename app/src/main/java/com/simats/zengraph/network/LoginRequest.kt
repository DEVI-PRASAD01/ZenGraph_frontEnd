package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")        val email: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("password")     val password: String
)
