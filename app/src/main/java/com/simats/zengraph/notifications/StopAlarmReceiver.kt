package com.simats.zengraph.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        DailyReminderReceiver.stopAlarm()
    }
}