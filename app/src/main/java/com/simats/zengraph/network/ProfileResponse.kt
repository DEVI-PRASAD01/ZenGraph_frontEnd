package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("name") val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("enable_notifications") val enableNotifications: Boolean = true,
    @SerializedName("data_sharing_consent") val dataSharingConsent: Boolean = false,
    @SerializedName("profile_image") val profileImage: String? = null
)
