package com.example.galleryios18.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.galleryios18.R
import java.io.File
import java.io.FileOutputStream

object MethodUtils {

    @JvmStatic
    fun scaleBitmap(bitmap: Bitmap): Bitmap {
        if (bitmap.width > 1500) {
            return Bitmap.createScaledBitmap(
                bitmap,
                1500,
                1500 * bitmap.height / bitmap.width,
                true
            )
        } else if (bitmap.height > 1500) {
            return Bitmap.createScaledBitmap(
                bitmap,
                1500 * bitmap.width / bitmap.height,
                1500,
                true
            )
        }
        return bitmap
    }

    fun showDialogPermission(context: Context) {
        AlertDialog.Builder(context).setMessage(
            context.resources.getString(R.string.You_need_to_enable_permissions_to_use_this_feature)
        ).setPositiveButton(
            context.resources.getString(R.string.go_to_setting)
        ) { dialog: DialogInterface?, which: Int ->
            // navigate to settings
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri =
                Uri.fromParts("package", context.packageName, null)
            intent.setData(uri)
            context.startActivity(intent)
            dialog?.dismiss()
        }.setNegativeButton(
            context.resources.getString(R.string.go_back)
        ) { dialog: DialogInterface, which: Int ->
            // leave?
            dialog.dismiss()
        }.show()
    }

    fun closeSoftKeyboard(activity: Activity) {
        try {
            val view = activity.currentFocus
            if (view != null) {
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showKeyboard(context: Context, mEtSearch: EditText?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    fun saveBitmap(context: Context, bitmap: Bitmap): String {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath +
                    File.separator + context.getString(R.string.app_name)
        val myDir = File(path)
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val file =
            File(myDir.absolutePath + File.separator + "${context.getString(R.string.app_name)}_${System.currentTimeMillis()}.jpeg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun argumentsToString(arguments: Array<String>?): String {
        if (arguments == null) {
            return "null"
        }
        val stringBuilder = StringBuilder()
        for (i in arguments.indices) {
            if (i > 0) {
                stringBuilder.append(" ")
            }
            stringBuilder.append(arguments[i])
        }
        return stringBuilder.toString()
    }

    fun convertDurationAudio(duration: Long): String {
        var out = ""
        var hours: Long = 0
        hours = try {
            duration / 3600000
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return out
        }
        val remainingMinutes = (duration - hours * 3600000) / 60000
        var minutes = remainingMinutes.toString()
        if (minutes.length < 2) {
            minutes = "0$minutes"
        }
        val remainingSeconds = duration - hours * 3600000 - remainingMinutes * 60000
        var seconds = remainingSeconds.toString()
        seconds = if (seconds.length < 4) {
            "00." + seconds.substring(0, 1)
        } else if (seconds.length < 5) {
            "0" + seconds.substring(0, 1) + "." + seconds.substring(1, 2)
        } else {
            seconds.substring(0, 2) + "." + seconds.substring(2, 3)
        }
        out = if (hours > 0) {
            "$hours:$minutes:$seconds"
        } else {
            "$minutes:$seconds"
        }
        return out
    }
}