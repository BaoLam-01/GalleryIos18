package com.example.galleryios18.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.galleryios18.R
import com.example.galleryios18.receiver.AlarmReceiver
import com.example.galleryios18.ui.main.MainActivity

class AppService : Service() {
    private var alarm: AlarmReceiver? = null
    private var notification: Notification? = null
    private val CHANNEL_ID = "CHANNEL_ZIP_LOCK"
    private val NOTIFICATION_ID = 101
    private val CHANNEL_NAME = "ZIP_LOCK"
    private val CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION_ZIP_LOCK"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.service)
        showNotification(this)
        if (alarm == null) alarm = AlarmReceiver()
        alarm?.setAlarm(this)
    }

    private fun startForeground(notification: Notification) {
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
                } else {
                    0
                }
            )
        } catch (_: Exception) {
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager: NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notification == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
                )
                channel.setShowBadge(false)
                channel.description = CHANNEL_DESCRIPTION
                notificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                notification = getNotification(context, channel.id)
            }
        } else {
            if (notification == null) {
                notification = getNotification(context, CHANNEL_ID)
            }
        }
        notification?.let {
            startForeground(it)
        }
    }

    private fun getNotification(context: Context, channelID: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(getString(R.string.tap_to_open))
            .setContentText(getString(R.string.tap_to_open)).setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent).setOngoing(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setChannelId(if (channelID == "NO") CHANNEL_ID else channelID).build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        alarm?.setAlarm(this)
    }
}