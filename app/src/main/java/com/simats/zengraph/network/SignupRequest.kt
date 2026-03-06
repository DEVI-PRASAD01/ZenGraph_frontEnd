package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirm_password") val confirmPassword: String,
    @SerializedName("enable_notifications") val enableNotifications: Boolean
)
