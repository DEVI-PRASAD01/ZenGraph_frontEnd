package com.simats.zengraph.repository

import com.simats.zengraph.network.ApiService
import com.simats.zengraph.network.PreferencesRequest
import com.simats.zengraph.network.UploadPhotoResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class SettingsRepository(private val apiService: ApiService) {

    suspend fun getProfile(userId: Int) = apiService.getProfile(userId)

    /**
     * Uploads a profile photo.
     * Returns the full Retrofit Response so the ViewModel can read profile_image from the body.
     */
    suspend fun uploadPhoto(userId: Int, photo: MultipartBody.Part): Response<UploadPhotoResponse> {
        val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        return apiService.uploadPhoto(userIdBody, photo)
    }

    suspend fun updatePreferences(
        userId: Int,
        notificationsEnabled: Boolean,
        dataSharingConsent: Boolean
    ) = apiService.updatePreferences(userId, PreferencesRequest(notificationsEnabled, dataSharingConsent))
}
