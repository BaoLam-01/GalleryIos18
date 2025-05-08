package com.example.galleryios18.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.lang.Exception

object PermissionUtils {

    fun hasPermissionOverlayAndBackground(context: Context): Boolean {
        val permissionOverlay = checkHasPermissionOverlay(context)
        val backgroundPermission = checkBackgroundPermission(context)
        return permissionOverlay && backgroundPermission
    }

    fun checkHasPermissionOverlay(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Settings.canDrawOverlays(context)
        } else true
    }


    fun checkBackgroundPermission(context: Context): Boolean {
        return if (!XiaomiUtilities.isMIUI()) {
            true
        } else {
            XiaomiUtilities.isCustomPermissionGranted(
                XiaomiUtilities.OP_BACKGROUND_START_ACTIVITY,
                context
            )
        }
    }

    fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else false
    }

    @SuppressLint("BatteryLife")
    fun requestPermissionSaveBattery(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startActivity(
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:" + activity.packageName)
                )
            )
        }
    }

    fun requestPermissionOverlay(context: Context): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )
        } else null
    }

    fun requestPermissionBackgroundOnXiaomi(context: Context): Intent? {
        return try {
            XiaomiUtilities.setMiuiBackgroundLockAccess(context)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun intentOpenSetting(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.setData(uri)
        return intent
    }

    fun checkPermissionRedMediaImages(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) != PackageManager.PERMISSION_DENIED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_DENIED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_DENIED
        } else true
    }

    fun checkAllowAllReadMediaPermissionAndroid14(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_DENIED
        } else true
    }
}