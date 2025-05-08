package com.example.galleryios18.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.galleryios18.R

object MethodUtils {

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
}