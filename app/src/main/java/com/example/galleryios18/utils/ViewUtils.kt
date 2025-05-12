package com.example.galleryios18.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.provider.Settings
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.Display
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.galleryios18.R
import com.example.galleryios18.ui.main.MainActivity

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

    fun showDialogPermission(context: Context) {
        AlertDialog.Builder(context).setMessage(
            context.getString(R.string.you_need_to_enable_permissions)
        ).setPositiveButton(
            context.getString(R.string.go_to_setting)
        ) { dialog: DialogInterface?, which: Int ->
            // navigate to settings
            intentToSettings(context)
        }.setNegativeButton(
            context.getString(R.string.go_back)
        ) { dialog: DialogInterface, which: Int ->
            // leave?
            dialog.dismiss()
        }.show()
    }

    fun intentToSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.setData(uri)
        context.startActivity(intent)
    }

    fun adjustViewWithSystemBar(
        viewTop: View? = null,
        viewBottom: View? = null,
        activity: MainActivity
    ) {

        viewTop?.apply {
            val layoutParams = this.layoutParams
            if (layoutParams is ConstraintLayout.LayoutParams) {
                layoutParams.topMargin = activity.statusBarHeight
                this.layoutParams = layoutParams
                requestLayout()
            }
        }

        viewBottom?.apply {
            val layoutParams = this.layoutParams
            if (layoutParams is ConstraintLayout.LayoutParams) {
                layoutParams.bottomMargin = activity.navigationBarHeight
                this.layoutParams = layoutParams
            }
        }

    }
}