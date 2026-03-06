package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class UploadPhotoResponse(
    @SerializedName("status")        val status: String,
    @SerializedName("profile_image") val profileImage: String
)
