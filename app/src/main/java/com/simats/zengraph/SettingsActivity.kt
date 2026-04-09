package com.simats.zengraph

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.simats.zengraph.databinding.ActivitySettingsBinding
import com.simats.zengraph.network.ProfileResponse
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.repository.SettingsRepository
import com.simats.zengraph.utils.AnimationUtils
import com.simats.zengraph.viewmodel.ProfileState
import com.simats.zengraph.viewmodel.SettingsActionState
import com.simats.zengraph.viewmodel.SettingsViewModel
import com.simats.zengraph.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsRepository(RetrofitClient.apiService))
    }

    // Track in-flight preference changes to avoid re-emitting on load
    private var preferencesLoaded = false
    private var currentUserId: Int = -1

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadPhoto(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("ZenGraph", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)
        currentUserId = userId

        setupObservers(userId)
        setupAnimations()
        setupClickListeners(userId)

        if (userId != -1) {
            settingsViewModel.loadProfile(userId)
        }
    }

    private fun setupObservers(userId: Int) {
        lifecycleScope.launchWhenStarted {
            settingsViewModel.profileState.collectLatest { state ->
                when (state) {
                    is ProfileState.Loading -> {
                        binding.settingsProgressBar.visibility = View.VISIBLE
                    }
                    is ProfileState.Success -> {
                        binding.settingsProgressBar.visibility = View.GONE
                        bindProfile(state.data, userId)
                    }
                    is ProfileState.Error -> {
                        binding.settingsProgressBar.visibility = View.GONE
                        Toast.makeText(this@SettingsActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            settingsViewModel.actionState.collectLatest { state ->
                when (state) {
                    is SettingsActionState.Loading -> {
                        binding.settingsProgressBar.visibility = View.VISIBLE
                    }
                    is SettingsActionState.Success -> {
                        binding.settingsProgressBar.visibility = View.GONE
                        Toast.makeText(this@SettingsActivity, state.message, Toast.LENGTH_SHORT).show()
                        settingsViewModel.resetActionState()
                        // Reload profile so the server-side photo URL is fetched and displayed
                        if (currentUserId != -1) settingsViewModel.loadProfile(currentUserId)
                    }
                    is SettingsActionState.Error -> {
                        binding.settingsProgressBar.visibility = View.GONE
                        Toast.makeText(this@SettingsActivity, state.message, Toast.LENGTH_LONG).show()
                        settingsViewModel.resetActionState()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun bindProfile(data: ProfileResponse, userId: Int) {
        binding.tvProfileName.text = data.name.ifEmpty { "User" }
        binding.tvProfileEmail.text = data.email

        // Load profile image with Glide into the dedicated ImageView
        if (!data.profileImage.isNullOrBlank()) {
            binding.tvProfileAvatar.visibility = View.GONE
            binding.ivProfilePhoto.visibility = View.VISIBLE
            Glide.with(this)
                .load(data.profileImage)
                .circleCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.ivProfilePhoto)
        } else {
            // Show first letter of name as avatar initial
            binding.ivProfilePhoto.visibility = View.GONE
            binding.tvProfileAvatar.visibility = View.VISIBLE
            binding.tvProfileAvatar.text = data.name.firstOrNull()?.uppercase() ?: "U"
        }

        // Seed toggles from API response (enable_notifications)
        preferencesLoaded = false
        binding.switchNotifications.setOnCheckedChangeListener(null)
        binding.switchNotifications.isChecked = data.enableNotifications

        // Wire toggle listeners — only fire after initial values are set
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (preferencesLoaded) {
                // Keep data sharing as is from the data object or default to false
                settingsViewModel.updatePreferences(userId, isChecked, data.dataSharingConsent)
            }
        }
        preferencesLoaded = true
    }

    private fun uploadPhoto(uri: Uri) {
        if (currentUserId == -1) return
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
            tempFile.outputStream().use { output -> inputStream.copyTo(output) }

            // Immediately show local preview so user sees their photo right away
            binding.tvProfileAvatar.visibility = View.GONE
            binding.ivProfilePhoto.visibility = View.VISIBLE
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.ivProfilePhoto)

            val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            // FastAPI expects the file field named "file"
            val part = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)
            settingsViewModel.uploadPhoto(currentUserId, part)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not read photo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAnimations() {
        AnimationUtils.apply3DEntrance(binding.settingsTitle)
        AnimationUtils.apply3DEntrance(binding.profileCard, 100)
        AnimationUtils.apply3DEntrance(binding.itemNotifications.parent as android.view.View, 300)
        AnimationUtils.apply3DEntrance(binding.bottomNavContainer, 500)
        AnimationUtils.startFloatingAnimation(binding.profileCard)
    }

    private fun startAnimatedActivity(intent: Intent, finishCurrent: Boolean = false) {
        startActivity(intent)
        overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
        if (finishCurrent) finish()
    }

    private fun setupClickListeners(userId: Int) {
        binding.navHome.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, MainActivity::class.java), true)
        }

        binding.navLibrary.setOnClickListener {
            startAnimatedActivity(Intent(this, MeditationLibraryActivity::class.java))
            finish()
        }

        binding.navReminder.setOnClickListener {
            startAnimatedActivity(Intent(this, ReminderActivity::class.java))
            finish()
        }

        binding.navProgress.setOnClickListener {
            startAnimatedActivity(Intent(this, ProgressDashboardActivity::class.java))
            finish()
        }

        binding.navSettings.setOnClickListener {
            // Already here
        }


        binding.btnUploadPhoto.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            pickImageLauncher.launch("image/*")
        }



        binding.itemSubscription.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, SubscriptionActivity::class.java))
        }

        binding.itemNotifications.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            // Toggle is handled by the switch listener set in bindProfile
        }

        binding.logoutLayout.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            getSharedPreferences("ZenGraphPrefs", Context.MODE_PRIVATE).edit().clear().apply()
            val intent = Intent(this, AuthChoiceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
