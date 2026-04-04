package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class PreferencesRequest(
    @SerializedName("enable_notifications") val enableNotifications: Boolean,
    @SerializedName("data_sharing_consent") val dataSharingConsent: Boolean
)
