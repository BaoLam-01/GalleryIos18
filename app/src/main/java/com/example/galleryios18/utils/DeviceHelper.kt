package com.tapbi.spark.launcherios18.utils

import android.os.Build

object DeviceHelper {

    val isMinSdk29 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun checkAndroidApi33(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun checkAndroidApi34(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    fun checkAndroidApi23(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun checkAndroidApi23to32(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    }

    fun checkApi26(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.O
    }
}