package com.simats.zengraph.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.simats.zengraph.MainActivity
import com.simats.zengraph.R

class DailyReminderReceiver : BroadcastReceiver() {

    companion object {
        private var ringtone: Ringtone? = null

        fun stopAlarm() {
            try {
                ringtone?.stop()
                ringtone = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        val messages = listOf(
            "🧘 Time to meditate! Your mind deserves a break.",
            "🌿 5 minutes of calm can change your whole day.",
            "✨ Your streak is waiting — don't break it!",
            "🌊 Take a breath. Your ZenGraph session is ready.",
            "🔥 Keep your streak alive — meditate today!",
            "💆 Stress less, breathe more. Start your session.",
            "🌙 Wind down with a calming meditation tonight."
        )

        val randomMessage = messages.random()
        val soundUriString = intent.getStringExtra("sound_uri") ?: ""

        playAlarmSound(context, soundUriString)
        showNotification(context, "ZenGraph", randomMessage)

        NotificationScheduler.scheduleDailyReminder(
            context,
            soundUriString = soundUriString
        )
    }

    private fun playAlarmSound(context: Context, soundUriString: String) {
        try {
            // 🔥 FIX: stop previous ringtone
            ringtone?.stop()
            ringtone = null

            val soundUri: Uri = if (soundUriString.isNotEmpty()) {
                Uri.parse(soundUriString)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }

            ringtone = RingtoneManager.getRingtone(context, soundUri)

            if (ringtone != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ringtone!!.audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    ringtone!!.streamType = AudioManager.STREAM_ALARM
                }

                ringtone!!.play()

                // auto stop after 30 sec (backup)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    try {
                        if (ringtone?.isPlaying == true) ringtone?.stop()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 10000) // 10 seconds
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotification(context: Context, title: String, body: String) {
        val channelId = "zen_silent_reminder"

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ STOP BUTTON (ONLY ONCE)
        val stopIntent = Intent(context, StopAlarmReceiver::class.java)

        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✅ BUILDER
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 300, 500))
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "ZenGraph Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                setSound(null, null)
            }
            manager.createNotificationChannel(channel)
        }

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}