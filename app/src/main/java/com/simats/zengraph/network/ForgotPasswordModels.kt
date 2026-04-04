package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("new_password") val newPassword: String
)

data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("name") val name: String? = null
)
