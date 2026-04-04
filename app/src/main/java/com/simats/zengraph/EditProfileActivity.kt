package com.simats.zengraph

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.simats.zengraph.databinding.ActivityEditProfileBinding
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.repository.SettingsRepository
import com.simats.zengraph.viewmodel.PhotoUploadState
import com.simats.zengraph.viewmodel.ProfileState
import com.simats.zengraph.viewmodel.SettingsViewModel
import com.simats.zengraph.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsRepository(RetrofitClient.apiService))
    }

    private var userId: Int = -1

    // ─── Image picker ─────────────────────────────────────────────────────────────

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedUri ->
                // 1. Instant local preview — shown immediately before upload completes
                showLocalPhoto(selectedUri)
                // 2. Upload to backend
                uploadPhoto(selectedUri)
            }
        }

    // ─── Lifecycle ────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("ZenGraph", Context.MODE_PRIVATE)
        userId = prefs.getInt("user_id", -1)

        observeProfile()
        observePhotoUpload()

        if (userId != -1) viewModel.loadProfile(userId)

        binding.backButton.setOnClickListener { finish() }

        binding.profileImageContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener {
            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // ─── Observers ────────────────────────────────────────────────────────────────

    private fun observeProfile() {
        lifecycleScope.launchWhenStarted {
            viewModel.profileState.collectLatest { state ->
                when (state) {
                    is ProfileState.Success -> {
                        binding.nameInput.setText(state.data.name)
                        val photoUrl = state.data.profileImage
                        if (!photoUrl.isNullOrBlank()) {
                            showRemotePhoto(photoUrl)
                        }
                    }
                    is ProfileState.Error -> Toast.makeText(
                        this@EditProfileActivity, state.message, Toast.LENGTH_SHORT
                    ).show()
                    else -> {}
                }
            }
        }
    }

    private fun observePhotoUpload() {
        lifecycleScope.launchWhenStarted {
            viewModel.photoUploadState.collectLatest { state ->
                when (state) {
                    is PhotoUploadState.Loading -> {
                        // Local preview is already showing — nothing extra needed
                    }
                    is PhotoUploadState.Success -> {
                        // Update the preview with the authoritative server URL (cache-bypassed)
                        showRemotePhoto(state.profileImageUrl)
                        Toast.makeText(
                            this@EditProfileActivity, "Profile photo updated!", Toast.LENGTH_SHORT
                        ).show()
                        viewModel.resetPhotoUploadState()
                    }
                    is PhotoUploadState.Error -> {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Upload failed: ${state.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.resetPhotoUploadState()
                    }
                    else -> {}
                }
            }
        }
    }

    // ─── Photo display helpers ────────────────────────────────────────────────────

    /** Show a locally-picked image URI immediately (no network, no delay). */
    private fun showLocalPhoto(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .transform(CircleCrop())
            .into(binding.ivProfilePhoto)
        binding.ivProfilePhoto.visibility = View.VISIBLE
    }

    /**
     * Show a remote URL.
     * skipMemoryCache + NONE disk strategy ensures stale images never appear
     * after the user updates their photo.
     */
    private fun showRemotePhoto(url: String) {
        Glide.with(this)
            .load(url)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transform(CircleCrop())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .into(binding.ivProfilePhoto)
        binding.ivProfilePhoto.visibility = View.VISIBLE
    }

    // ─── Upload ───────────────────────────────────────────────────────────────────

    private fun uploadPhoto(uri: Uri) {
        if (userId == -1) return

        val inputStream = contentResolver.openInputStream(uri) ?: return
        val file = File.createTempFile("profile_", ".jpg", cacheDir)
        file.outputStream().use { out -> inputStream.copyTo(out) }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        viewModel.uploadPhoto(userId, body)
    }
}