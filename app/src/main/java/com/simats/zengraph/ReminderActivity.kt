package com.simats.zengraph

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.notifications.NotificationScheduler
import java.util.Locale

class ReminderActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var btnSetReminder: Button
    private lateinit var btnSelectSound: Button
    private lateinit var tvCurrentReminder: TextView
    private lateinit var tvSelectedSound: TextView
    private lateinit var ivBack: ImageView

    private var selectedSoundUri: Uri? = null

    companion object {
        private const val REQUEST_RINGTONE = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        timePicker       = findViewById(R.id.timePicker)
        btnSetReminder   = findViewById(R.id.btnSetReminder)
        btnSelectSound   = findViewById(R.id.btnSelectSound)
        tvCurrentReminder = findViewById(R.id.tvCurrentReminder)
        tvSelectedSound  = findViewById(R.id.tvSelectedSound)
        ivBack           = findViewById(R.id.ivBack)

        timePicker.setIs24HourView(false)

        ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupBottomNav()

        loadSavedReminder()
        loadSavedSound()

        // ── Open ringtone picker ──────────────────────────────────
        btnSelectSound.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Reminder Sound")
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_TYPE,
                    RingtoneManager.TYPE_RINGTONE or
                            RingtoneManager.TYPE_NOTIFICATION or
                            RingtoneManager.TYPE_ALARM
                )
                // Pre-select currently saved sound
                selectedSoundUri?.let {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, it)
                }
            }
            startActivityForResult(intent, REQUEST_RINGTONE)
        }

        btnSetReminder.setOnClickListener {
            saveReminder()
        }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
            }
        })
    }

    private fun setupBottomNav() {
        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
            finish()
        }
        findViewById<android.view.View>(R.id.navLibrary).setOnClickListener {
            startActivity(Intent(this, MeditationLibraryActivity::class.java))
            overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
            finish()
        }
        findViewById<android.view.View>(R.id.navReminder).setOnClickListener {
            // Already here
        }
        findViewById<android.view.View>(R.id.navProgress).setOnClickListener {
            startActivity(Intent(this, AnalyticsDashboardActivity::class.java))
            overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
            finish()
        }
        findViewById<android.view.View>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
            finish()
        }
    }


    // ── Ringtone picker result ────────────────────────────────────
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_RINGTONE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

            selectedSoundUri = uri

            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(this, uri)
                val soundName = ringtone?.getTitle(this) ?: "Selected Sound"
                tvSelectedSound.text = soundName

                // Save URI to prefs
                getSharedPreferences("reminder", Context.MODE_PRIVATE)
                    .edit()
                    .putString("sound_uri", uri.toString())
                    .apply()
            } else {
                // User picked Silent
                tvSelectedSound.text = "Silent"
                getSharedPreferences("reminder", Context.MODE_PRIVATE)
                    .edit()
                    .putString("sound_uri", "")
                    .apply()
            }
        }
    }

    // ── Load saved sound on screen open ──────────────────────────
    private fun loadSavedSound() {
        val sharedPrefs = getSharedPreferences("reminder", Context.MODE_PRIVATE)
        val savedUri = sharedPrefs.getString("sound_uri", null)

        if (!savedUri.isNullOrEmpty()) {
            selectedSoundUri = Uri.parse(savedUri)
            val ringtone = RingtoneManager.getRingtone(this, selectedSoundUri)
            tvSelectedSound.text = ringtone?.getTitle(this) ?: "Selected Sound"
        } else {
            // Show default notification sound name
            val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            selectedSoundUri = defaultUri
            val ringtone = RingtoneManager.getRingtone(this, defaultUri)
            tvSelectedSound.text = ringtone?.getTitle(this) ?: "Default notification sound"
        }
    }

    private fun loadSavedReminder() {
        val sharedPrefs = getSharedPreferences("reminder", Context.MODE_PRIVATE)
        val savedHour   = sharedPrefs.getInt("hour", -1)
        val savedMinute = sharedPrefs.getInt("minute", -1)

        if (savedHour != -1 && savedMinute != -1) {
            val isPm        = savedHour >= 12
            val displayHour = if (savedHour % 12 == 0) 12 else savedHour % 12
            val amPmStr     = if (isPm) "PM" else "AM"
            val formattedMinute = String.format(Locale.getDefault(), "%02d", savedMinute)

            tvCurrentReminder.text = "Reminder set for $displayHour:$formattedMinute $amPmStr"
            btnSetReminder.text    = "Update Reminder"

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                timePicker.hour   = savedHour
                timePicker.minute = savedMinute
            } else {
                timePicker.currentHour   = savedHour
                timePicker.currentMinute = savedMinute
            }
        } else {
            tvCurrentReminder.text = "No reminder set"
            btnSetReminder.text    = "Set Reminder"

            val cal = java.util.Calendar.getInstance()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                timePicker.hour   = cal.get(java.util.Calendar.HOUR_OF_DAY)
                timePicker.minute = cal.get(java.util.Calendar.MINUTE)
            } else {
                timePicker.currentHour   = cal.get(java.util.Calendar.HOUR_OF_DAY)
                timePicker.currentMinute = cal.get(java.util.Calendar.MINUTE)
            }
        }
    }

    private fun saveReminder() {
        val hour: Int
        val minute: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour   = timePicker.hour
            minute = timePicker.minute
        } else {
            hour   = timePicker.currentHour
            minute = timePicker.currentMinute
        }

        val isPm    = hour >= 12
        val amPmStr = if (isPm) "PM" else "AM"

        // Save time + sound URI together
        val soundUriString = selectedSoundUri?.toString() ?: ""
        getSharedPreferences("reminder", Context.MODE_PRIVATE)
            .edit()
            .putInt("hour", hour)
            .putInt("minute", minute)
            .putString("am_pm", amPmStr)
            .putString("sound_uri", soundUriString)
            .apply()

        // Pass sound URI to scheduler
        NotificationScheduler.scheduleDailyReminder(this, hour, minute, soundUriString)

        val displayHour     = if (hour % 12 == 0) 12 else hour % 12
        val formattedMinute = String.format(Locale.getDefault(), "%02d", minute)

        tvCurrentReminder.text = "Reminder set for $displayHour:$formattedMinute $amPmStr"
        btnSetReminder.text    = "Update Reminder"

        Toast.makeText(this, "Reminder saved successfully ✓", Toast.LENGTH_SHORT).show()
    }
}