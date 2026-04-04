package com.simats.zengraph

import android.Manifest
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.simats.zengraph.databinding.ActivityMainBinding
import com.simats.zengraph.network.DashboardResponse
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.SessionStatsResponse
import com.simats.zengraph.notifications.NotificationScheduler
import com.simats.zengraph.repository.DashboardRepository
import com.simats.zengraph.repository.SettingsRepository
import com.simats.zengraph.utils.AnimationUtils
import com.simats.zengraph.viewmodel.ActionState
import com.simats.zengraph.viewmodel.DashboardState
import com.simats.zengraph.viewmodel.DashboardViewModel
import com.simats.zengraph.viewmodel.DashboardViewModelFactory
import com.simats.zengraph.viewmodel.ProfileState
import com.simats.zengraph.viewmodel.SessionStatsState
import com.simats.zengraph.viewmodel.SettingsViewModel
import com.simats.zengraph.viewmodel.SettingsViewModelFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import com.simats.zengraph.network.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(DashboardRepository(RetrofitClient.apiService))
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsRepository(RetrofitClient.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("ZenGraph", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)
        val userName = sharedPrefs.getString("user_name", "Explorer")

        if (userId == -1) {
            startActivity(Intent(this, AuthChoiceActivity::class.java))
            finish()
            return
        }

        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
        binding.tvGreeting.text = "$greeting, $userName \u2600\uFE0F"

        observeProfilePhoto()
        setupObservers(userId)
        dashboardViewModel.loadDashboard(userId)
        dashboardViewModel.loadSessionStats(userId)
        settingsViewModel.loadProfile(userId)

        setupAnimations(userId)
        setupClickListeners(userId)

        // ── Push Notifications Setup ───────────────────────────
        setupNotifications()
        // 🔥 STOP alarm if app opens
        try {
            com.simats.zengraph.notifications.DailyReminderReceiver.stopAlarm()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupNotifications() {

        // Request permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // ✅ ADD THIS PART HERE (INSIDE FUNCTION)
        val prefs = getSharedPreferences("reminder", Context.MODE_PRIVATE)

        val hour = prefs.getInt("hour", -1)
        val minute = prefs.getInt("minute", -1)

        if (hour != -1 && minute != -1) {
            NotificationScheduler.scheduleDailyReminder(this, hour, minute)
        }
    }
    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("ZenGraph", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)
        if (userId != -1) {
            dashboardViewModel.loadDashboard(userId)
            dashboardViewModel.loadSessionStats(userId)
            settingsViewModel.loadProfile(userId)
        }
    }

    private fun observeProfilePhoto() {
        lifecycleScope.launchWhenStarted {
            settingsViewModel.profileState.collectLatest { state ->
                if (state is ProfileState.Success) {
                    val photoUrl = state.data.profileImage
                    if (!photoUrl.isNullOrBlank()) {
                        Glide.with(this@MainActivity)
                            .load(photoUrl)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transform(CircleCrop())
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(binding.ivHeaderProfile)
                        binding.ivHeaderProfile.visibility = View.VISIBLE
                    } else {
                        binding.ivHeaderProfile.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupObservers(userId: Int) {
        lifecycleScope.launchWhenStarted {
            dashboardViewModel.dashboardState.collectLatest { state ->
                when (state) {
                    is DashboardState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is DashboardState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        updateDashboardUI(state.data)
                    }
                    is DashboardState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            dashboardViewModel.sessionStatsState.collectLatest { state ->
                when (state) {
                    is SessionStatsState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is SessionStatsState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        updateSessionStatsUI(state.data)
                    }
                    is SessionStatsState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            dashboardViewModel.actionState.collectLatest { state ->
                when (state) {
                    is ActionState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is ActionState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                        dashboardViewModel.resetActionState()
                    }
                    is ActionState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                        dashboardViewModel.resetActionState()
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            dashboardViewModel.startSessionState.collectLatest { state ->
                when (state) {
                    is ActionState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is ActionState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        dashboardViewModel.resetStartSessionState()
                        val intent = Intent(this@MainActivity, FeelingSelectionActivity::class.java)
                        intent.putExtra("EXTRA_USER_ID", userId)
                        startAnimatedActivity(intent)
                    }
                    is ActionState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                        dashboardViewModel.resetStartSessionState()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateDashboardUI(data: DashboardResponse) {
        setupWeeklyProgress(data.weeklyProgress)
    }

    private fun updateSessionStatsUI(data: SessionStatsResponse) {
        binding.tvStreakCount.text = data.currentStreak.toString()
        binding.tvLevelVal.text = "Lvl ${data.level}"
        binding.tvTotalHoursVal.text = "${data.totalMinutes / 60}h"
        binding.tvLevelTitle.text = when {
            data.level >= 10 -> "MASTER"
            data.level >= 5  -> "EXPERT"
            else             -> "EXPLORER"
        }
    }

    private fun getLevelTitle(level: Int): String {
        return when {
            level >= 50 -> "Master"
            level >= 30 -> "Expert"
            level >= 15 -> "Practitioner"
            level >= 5  -> "Apprentice"
            else        -> "Explorer"
        }
    }

    private fun setupWeeklyProgress(weeklyProgress: List<Float>?) {
        binding.weeklyProgressContainer.removeAllViews()
        val days = listOf("M", "T", "W", "T", "F", "S", "S")
        val safeProgress = weeklyProgress ?: emptyList()
        val progress = if (safeProgress.size >= 7) safeProgress.takeLast(7)
        else List(7 - safeProgress.size) { 0f } + safeProgress

        progress.forEachIndexed { index, value ->
            val dayView = android.widget.TextView(this).apply {
                text = days[index]
                textSize = 10f
                gravity = Gravity.CENTER
                setTextColor(if (value > 0) Color.WHITE else Color.GRAY)
                setBackgroundResource(
                    if (value > 0) R.drawable.bg_accent_circle
                    else R.drawable.bg_gray_circle
                )
                val size = (24 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(6, 0, 6, 0)
                }
            }
            binding.weeklyProgressContainer.addView(dayView)
        }
    }







    private fun dip(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()





    private fun setupAnimations(userId: Int) {
        AnimationUtils.apply3DEntrance(binding.header)
        AnimationUtils.apply3DEntrance(binding.graphCard, 100)
        AnimationUtils.apply3DEntrance(binding.statsRow, 300)
        AnimationUtils.apply3DEntrance(binding.bottomNavContainer, 400)
        AnimationUtils.apply3DEntrance(binding.historyCard, 500)
        AnimationUtils.startFloatingAnimation(binding.graphCard)
    }


    private fun startAnimatedActivity(intent: Intent, finishCurrent: Boolean = false) {
        startActivity(intent)
        overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
        if (finishCurrent) finish()
    }

    private fun setupClickListeners(userId: Int) {
        val moodCards = listOf(
            binding.llMoodHappy, binding.llMoodSad, binding.llMoodAngry,
            binding.llMoodAnxious, binding.llMoodNeutral, binding.llMoodExcited
        )
        moodCards.forEach { card ->
            card.setOnClickListener { selectedView ->
                AnimationUtils.applyScalePop(selectedView)
                moodCards.forEach { it.isSelected = (it == selectedView) }
            }
        }

        binding.navHome.setOnClickListener { AnimationUtils.applyScalePop(it) }

        binding.ivBell.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, NotificationsSettingsActivity::class.java))
        }
        binding.navLibrary.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, MeditationLibraryActivity::class.java))
        }
        binding.navReminder.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, ReminderActivity::class.java))
        }
        binding.navProgress.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, AnalyticsDashboardActivity::class.java))
        }
        binding.navSettings.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.graphCard.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            val intent = Intent(this, GoalSelectionActivity::class.java)
            intent.putExtra("EXTRA_DURATION", "5 min")
            intent.putExtra("EXTRA_USER_ID", userId)
            startAnimatedActivity(intent)
        }
        binding.suggestionCard2.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            val intent = Intent(this, GoalSelectionActivity::class.java)
            intent.putExtra("EXTRA_DURATION", "10 min")
            intent.putExtra("EXTRA_USER_ID", userId)
            startAnimatedActivity(intent)
        }
        binding.checkInCard.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            val intent = Intent(this, GoalSelectionActivity::class.java)
            intent.putExtra("EXTRA_DURATION", "15 min")
            intent.putExtra("EXTRA_USER_ID", userId)
            startAnimatedActivity(intent)
        }
        binding.historyCard.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.friendsCard.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            startAnimatedActivity(Intent(this, FriendsActivity::class.java))
        }

    }
    override fun onUserInteraction() {
        super.onUserInteraction()

        try {
            com.simats.zengraph.notifications.DailyReminderReceiver.stopAlarm()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}