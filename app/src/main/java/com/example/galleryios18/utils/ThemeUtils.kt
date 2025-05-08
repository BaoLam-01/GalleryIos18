package com.example.galleryios18.utils

import android.content.Context
import android.content.Intent
import com.example.galleryios18.common.Constant

object ThemeUtils {

    fun changeThemeApply(context: Context) {
        val intent = Intent()
        intent.`package` = context.packageName
        intent.action = Constant.ACTION_CHANGE_THEME_APPLY
        context.sendBroadcast(intent)
    }
}