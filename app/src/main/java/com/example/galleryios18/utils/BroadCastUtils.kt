package com.example.galleryios18.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat

object BroadCastUtils {

    @SuppressLint("UnspecifiedRegisterReceiverFlag", "WrongConstant")
    fun registerMyReceiver(
        myBroadcastNotFromTheSystem: BroadcastReceiver,
        intentFilter: IntentFilter,
        context: Context,
        isExported: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val valueExported = if (isExported) ContextCompat.RECEIVER_EXPORTED
            else ContextCompat.RECEIVER_NOT_EXPORTED
            ContextCompat.registerReceiver(
                context,
                myBroadcastNotFromTheSystem,
                intentFilter,
                valueExported
            )
        } else {
            context.registerReceiver(myBroadcastNotFromTheSystem, intentFilter)
        }
    }
}