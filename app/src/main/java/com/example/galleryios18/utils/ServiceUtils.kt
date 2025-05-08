package com.example.galleryios18.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.galleryios18.App


object ServiceUtils {

    private fun checkPermissionNotification(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED
        } else {
            false
        }
    }


    fun startService(context: Context, intent: Intent?) {
        Log.e("HaiPd", "startService: ")
        if (App.instance.isAppInForeground) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (checkPermissionNotification(context)) {
                        context.startService(intent)
                    } else {
                        intent?.let { ContextCompat.startForegroundService(context, it) }
                    }
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
            }
        }

    }

    fun startServiceAway(context: Context, intent: Intent?) {
        Log.e("HaiPd", "startService: ")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (checkPermissionNotification(context)) {
                    context.startService(intent)
                } else {
                    intent?.let { ContextCompat.startForegroundService(context, it) }
                }
            } else {
                context.startService(intent)
            }
        } catch (_: Exception) {
        }
    }
}