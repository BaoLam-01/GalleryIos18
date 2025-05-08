package com.example.galleryios18.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.text.TextUtils
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Objects

object XiaomiUtilities {
    // custom permissions
    const val OP_ACCESS_XIAOMI_ACCOUNT: Int = 10015
    const val OP_AUTO_START: Int = 10008
    const val OP_BACKGROUND_START_ACTIVITY: Int = 10021
    const val OP_BLUETOOTH_CHANGE: Int = 10002
    const val OP_BOOT_COMPLETED: Int = 10007
    const val OP_DATA_CONNECT_CHANGE: Int = 10003
    const val OP_DELETE_CALL_LOG: Int = 10013
    const val OP_DELETE_CONTACTS: Int = 10012
    const val OP_DELETE_MMS: Int = 10011
    const val OP_DELETE_SMS: Int = 10010
    const val OP_EXACT_ALARM: Int = 10014
    const val OP_GET_INSTALLED_APPS: Int = 10022
    const val OP_GET_TASKS: Int = 10019
    const val OP_INSTALL_SHORTCUT: Int = 10017
    const val OP_NFC: Int = 10016
    const val OP_NFC_CHANGE: Int = 10009
    const val OP_READ_MMS: Int = 10005
    const val OP_READ_NOTIFICATION_SMS: Int = 10018
    const val OP_SEND_MMS: Int = 10004
    const val OP_SERVICE_FOREGROUND: Int = 10023
    const val OP_SHOW_WHEN_LOCKED: Int = 10020
    const val OP_WIFI_CHANGE: Int = 10001
    const val OP_WRITE_MMS: Int = 10006

    fun isMIUI(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))
    }

    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    fun isCustomPermissionGranted(permission: Int, context: Context): Boolean {
        try {
            val mgr = Objects.requireNonNull(context)
                .getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val m = AppOpsManager::class.java.getMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result =
                m.invoke(
                    mgr,
                    permission,
                    Process.myUid(),
                    context.packageName
                ) as Int
            return result == AppOpsManager.MODE_ALLOWED
        } catch (x: Exception) {
            Timber.e(x)
        }
        return true
    }

    fun getMIUIMajorVersion(): Int {
        val prop = getSystemProperty("ro.miui.ui.version.name")
        if (prop != null) {
            try {
                return prop.replace("V", "").toInt()
            } catch (ignore: NumberFormatException) {
            }
        }
        return -1
    }

    fun getPermissionManagerIntent(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_package_uid", Process.myUid())
        intent.putExtra("extra_pkgname", Objects.requireNonNull(context).packageName)
        return intent
    }

    fun goToXiaomiPermissions(context: Context) {
//        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
//        intent.setClassName(
//                "com.miui.securitycenter",
//                "com.miui.permcenter.permissions.PermissionsEditorActivity"
//        )
//        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(getPermissionManagerIntent(context))
    }

    fun setMiuiBackgroundLockAccess(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
            } else {
                intent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                )
            }
        }
        intent.putExtra("extra_pkgname", context.packageName)
        return intent
    }
}
