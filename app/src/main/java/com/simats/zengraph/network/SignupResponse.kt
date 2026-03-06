package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)
