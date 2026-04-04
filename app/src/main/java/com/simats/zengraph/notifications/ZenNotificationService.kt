package com.simats.zengraph.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.simats.zengraph.MainActivity
import com.simats.zengraph.R

class ZenNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "ZenGraph"
        val body  = message.notification?.body  ?: "Time to meditate 🧘"
        showNotification(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    fun showNotification(title: String, body: String) {
        val channelId = "zen_notifications"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ZenGraph Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Meditation reminders and updates"
            }
            manager.createNotificationChannel(channel)
        }

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}