package com.example.galleryios18.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import com.example.galleryios18.App
import com.example.galleryios18.service.AppService
import com.example.galleryios18.utils.ServiceUtils
import com.example.galleryios18.utils.Utils
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    private var alarmManager: AlarmManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        setAlarm(context)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PeriSecure:MyWakeLock")
        wakeLock.acquire(10 * 60 * 1000L)
        wakeLock.release()
        startService(context)
    }

    private fun startService(context: Context) {
        if (!Utils.isServiceRunning(
                context,
                AppService::class.java
            ) && !App.instance.isAppInForeground
        ) {
            try {
               ServiceUtils.startServiceAway(context,Intent(context,AppService::class.java))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    fun setAlarm(context: Context) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.MINUTE, 1)
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager?.setExact(
                    AlarmManager.RTC, calendar.timeInMillis, pendingIntent
                )
            }
        }
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

}