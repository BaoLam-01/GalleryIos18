package com.example.galleryios18.utils

import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import com.example.galleryios18.service.AppService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar


object Utils {

    fun getCurrentDayFormatted(context: Context): String {
        val calendar = Calendar.getInstance()
        val formatter =
            SimpleDateFormat("EEEE d MMMM", LocaleUtils.getLocaleCompat(context.resources))
        return formatter.format(calendar.time)
    }

    fun loadJSONFromAsset(context: Context, template: String?): String {
        val json: String
        try {
            val `is` = context.assets.open(template!!)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, charset("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    fun startService(context: Context, awayStart: Boolean = false) {
        if (!isServiceRunning(context, AppService::class.java) || awayStart) {
            val intent = Intent(context, AppService::class.java)
            ServiceUtils.startService(context, intent)
        }
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun isAtLeastSdkVersion(versionCode: Int): Boolean {
        return Build.VERSION.SDK_INT >= versionCode
    }

    fun getScreenWidth(context: Context): Int {
        val displaymetrics = context.resources.displayMetrics
        return displaymetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val displaymetrics = context.resources.displayMetrics
        return displaymetrics.heightPixels
    }

    fun checkClick(view: View, time: Int) {
        view.isClickable = false
        Handler().postDelayed({ view.isClickable = true }, time.toLong())
    }

    fun checkClickAndGone(view: View, time: Int) {
        view.visibility = View.VISIBLE
        Handler().postDelayed({
            view.visibility = View.GONE
        }, time.toLong())
    }

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun saveBitmapToLocal(bm: Bitmap, file: File): String? {
        var path: String? = null
        try {
            val fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            path = file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return path
    }

    fun goToCHPlay(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun shareText(context: Context, text: String?, subject: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        context.startActivity(Intent.createChooser(shareIntent, "Share..."))
    }

    fun sendEmail(context: Context, supportEmail: String) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setType("text/email")
        emailIntent.setPackage("com.google.android.gm")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
        var versionCode = "1.0"
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionCode = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val androidVersion = Build.VERSION.SDK_INT
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            "App Report (" + context.packageName + ") - version [" + versionCode + "] - [" + getDeviceName() + "] - Android[" + androidVersion + "]"
        )
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        context.startActivity(Intent.createChooser(emailIntent, "Send mail Report App !"))
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        if (model.startsWith(manufacturer)) {
            return capitalize(model)
        }
        return capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(c.uppercaseChar())
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
    }

    fun checkSystemLockOn(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val keyguardManager =
                context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.isDeviceSecure
        } else {
            isPatternSet(context)
        }
    }

    private fun isPatternSet(context: Context): Boolean {
        val cr = context.contentResolver
        return try {
            val lockPatternEnable: Int =
                Settings.Secure.getInt(cr, Settings.Secure.LOCK_PATTERN_ENABLED)
            lockPatternEnable == 1
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }
}