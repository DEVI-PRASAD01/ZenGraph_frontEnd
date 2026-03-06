package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class PreferencesRequest(
    @SerializedName("notifications_enabled") val notificationsEnabled: Boolean,
    @SerializedName("data_sharing_consent") val dataSharingConsent: Boolean
)
