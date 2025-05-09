package com.tapbi.spark.launcherios18.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.galleryios18.utils.ViewUtils

object PermissionHelper {

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissionLessApi33(fragmentActivity: FragmentActivity, code: Int) {
        ActivityCompat.requestPermissions(
            fragmentActivity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            code
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissionApi33(fragmentActivity: FragmentActivity, code: Int) {
        ActivityCompat.requestPermissions(
            fragmentActivity,
            arrayOf(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES),
            code
        )
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun requestMediaPermissionApi34(fragmentActivity: FragmentActivity, code: Int) {
        ActivityCompat.requestPermissions(
            fragmentActivity,
            arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ),
            code
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissionAudioApi33(fragmentActivity: FragmentActivity, code: Int) {
        ActivityCompat.requestPermissions(
            fragmentActivity,
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
            code
        )
    }

    internal fun checkPermissionMedia(activity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            (activity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED &&
                    activity.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_DENIED &&
                    activity.checkSelfPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_DENIED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (activity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED &&
                    activity.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_DENIED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        } else {
            false
        }
    }

    private fun checkPermissionReadWallpaper(activity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        } else {
            false
        }
    }

    fun checkAndRequestReadWallpaper(activity: FragmentActivity, code: Int): Boolean {
        val check = checkPermissionReadWallpaper(activity)
        if (check) {
            if (DeviceHelper.checkAndroidApi23to32()) {
                requestPermissionLessApi33(activity, code)
            }
        }
        return check
    }

    fun checkAndRequestMedia(activity: FragmentActivity, code: Int): Boolean {
        val check = checkPermissionMedia(activity)
        if (checkPermissionMedia(activity)) {
            if (DeviceHelper.checkAndroidApi34()) {
                requestMediaPermissionApi34(activity, code)
            } else if (DeviceHelper.checkAndroidApi33()) {
                requestPermissionApi33(activity, code)
            } else if (DeviceHelper.checkAndroidApi23()) {
                requestPermissionLessApi33(activity, code)
            }
        }
        return check
    }

    fun isLimitMedia(activity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val readMediaImage =
                activity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            val readMediaVideo =
                activity.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
            val readMediaVisual =
                activity.checkSelfPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
            readMediaVisual && !readMediaImage && !readMediaVideo
        } else {
            false
        }
    }

    fun checkPermissionAudioApi33(activity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (activity.checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_DENIED)
        } else {
            false
        }
    }

    fun checkRequestMedia(grantResults: IntArray, activity: Activity): Boolean {
        if (DeviceHelper.checkAndroidApi34() && grantResults.size >= 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED || grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ViewUtils.showDialogPermission(activity)
                return false
            }
        } else {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ViewUtils.showDialogPermission(activity)
                return false
            }
        }
    }

    fun checkAndRequestLocationPermission(activity: FragmentActivity, requestCode: Int) {
        if (!checkLocationPermission(activity)) {
            requestLocationPermission(activity, requestCode)
        }
    }

    private fun checkLocationPermission(activity: FragmentActivity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission(activity: FragmentActivity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestCode
        )
    }

    fun checkAndRequestCalendarPermission(activity: FragmentActivity, requestCode: Int) {
        if (!checkCalendarPermission(activity)) {
            requestCalendarPermission(activity, requestCode)
        }
    }

     fun checkCalendarPermission(activity: FragmentActivity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCalendarPermission(activity: FragmentActivity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            ),
            requestCode
        )
    }
}