package com.example.galleryios18.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.Display
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

object ViewUtils {
    fun disableAnimateRCV(recycler: RecyclerView) {
        recycler.animation = null
        (recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            drawable?.colorFilter = PorterDuffColorFilter(
                color, PorterDuff.Mode.SRC_IN
            )
        }
    }


    fun measureTextWidth(view: TextView, text: String?): Float {
        if (TextUtils.isEmpty(text)) {
            return 0f
        } /*from  w  w  w. ja  v a2 s. c o  m*/
        val paint: TextPaint = view.paint
        return paint.measureText(text)
    }


    private fun hasNavigationBar(context: Context): Boolean {
        val hasNavBarId =
            context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (hasNavBarId > 0) {
            context.resources.getBoolean(hasNavBarId)
        } else {
            val hasPermanentMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
            val display =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val realSize = Point()
            val screenSize = Point()

            display.getRealSize(realSize)
            display.getSize(screenSize)

            realSize.y != screenSize.y || realSize.x != screenSize.x || !hasPermanentMenuKey
        }
    }
//    private fun hasNavigationBar(activity: Activity): Boolean {
//        val view = activity.window.decorView
//        return (view.systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) == 0
//    }


    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun hasOnScreenSystemBar(activity: Activity): Boolean {
        val display: Display = activity.windowManager.defaultDisplay
        var rawDisplayHeight = 0
        try {
            val getRawHeight = Display::class.java.getMethod("getRawHeight")
            rawDisplayHeight = getRawHeight.invoke(display) as Int
        } catch (ex: Exception) {
        }
        val UIRequestedHeight = display.height
        return rawDisplayHeight - UIRequestedHeight > 0
    }

    fun getNaviBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getScreenWidth(context: Context): Int {
        val displaymetrics = context.resources.displayMetrics
        return displaymetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val displaymetrics = context.resources.displayMetrics
        return displaymetrics.heightPixels
    }


    fun pxToDp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun dpToPx(context: Context, dipValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f)
    }

    fun pxToSp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun spToPx(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun formatDate(time: Long): String {
        return DateFormat.format("d.M.yyyy", time).toString()
    }

    fun isEnableClickView(view: View, isEnable: Boolean) {
        if (isEnable) {
            view.isClickable = true
            view.isEnabled = true
        } else {
            view.isClickable = false
            view.isEnabled = false
        }
    }

    fun checkBackgroundPadding(path: String): Boolean {
        val list = arrayOf("background51", "background42", "background02", "padding")
        for (str in list) {
            if (path.contains(str)) {
                return true
            }
        }
        return false
    }

}