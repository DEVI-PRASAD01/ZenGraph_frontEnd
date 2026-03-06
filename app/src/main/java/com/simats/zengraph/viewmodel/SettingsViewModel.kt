package com.simats.zengraph.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.zengraph.network.ProfileResponse
import com.simats.zengraph.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val data: ProfileResponse) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class SettingsActionState {
    object Idle : SettingsActionState()
    object Loading : SettingsActionState()
    data class Success(val message: String) : SettingsActionState()
    data class Error(val message: String) : SettingsActionState()
}

/**
 * Dedicated state for photo upload result.
 * Carries the fresh profile_image URL returned by the backend on success.
 */
sealed class PhotoUploadState {
    object Idle    : PhotoUploadState()
    object Loading : PhotoUploadState()
    data class Success(val profileImageUrl: String) : PhotoUploadState()
    data class Error(val message: String)           : PhotoUploadState()
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    // ── Profile ───────────────────────────────────────────────────────────────
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    // ── General action (preferences, etc.) ────────────────────────────────────
    private val _actionState = MutableStateFlow<SettingsActionState>(SettingsActionState.Idle)
    val actionState: StateFlow<SettingsActionState> = _actionState.asStateFlow()

    // ── Photo upload ──────────────────────────────────────────────────────────
    private val _photoUploadState = MutableStateFlow<PhotoUploadState>(PhotoUploadState.Idle)
    val photoUploadState: StateFlow<PhotoUploadState> = _photoUploadState.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────

    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = repository.getProfile(userId)
                _profileState.value = ProfileState.Success(response)
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    /**
     * Upload photo — on success:
     *   1. Emits PhotoUploadState.Success with the fresh URL from the server response
     *   2. Refreshes profileState by calling loadProfile so the URL is persisted locally
     */
    fun uploadPhoto(userId: Int, photo: MultipartBody.Part) {
        viewModelScope.launch {
            _photoUploadState.value = PhotoUploadState.Loading
            try {
                val response = repository.uploadPhoto(userId, photo)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status == "success") {
                        _photoUploadState.value = PhotoUploadState.Success(body.profileImage)
                        // Refresh full profile so other screens get the updated URL too
                        loadProfile(userId)
                    } else {
                        _photoUploadState.value = PhotoUploadState.Error("Upload failed: empty response")
                    }
                } else {
                    _photoUploadState.value = PhotoUploadState.Error(
                        "Upload failed: HTTP ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _photoUploadState.value = PhotoUploadState.Error(e.message ?: "Photo upload failed")
            }
        }
    }

    fun updatePreferences(userId: Int, notificationsEnabled: Boolean, dataSharingConsent: Boolean) {
        viewModelScope.launch {
            _actionState.value = SettingsActionState.Loading
            try {
                val response = repository.updatePreferences(userId, notificationsEnabled, dataSharingConsent)
                _actionState.value = SettingsActionState.Success(response.message ?: "Preferences saved!")
            } catch (e: Exception) {
                _actionState.value = SettingsActionState.Error(e.message ?: "Failed to update preferences")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = SettingsActionState.Idle
    }

    fun resetPhotoUploadState() {
        _photoUploadState.value = PhotoUploadState.Idle
    }
}
