package com.example.galleryios18.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

object AutoStartUtils {
    fun goToXiaomiPermissionsAutoStart(context: Context) {
        val intent = Intent()
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            intent.setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            )
        } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
            intent.setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            )
        } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
            intent.setComponent(
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            )
        } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
            intent.setComponent(
                ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
            )
        } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
            intent.setComponent(
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            )
        } else {
            return
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isViVoDevice(context: Context?): Boolean {
        val manufacturer = Build.MANUFACTURER
        return "vivo".equals(manufacturer, ignoreCase = true)
    }

    fun requestAutostartPermission(context: Context) {
        val intentList: MutableList<Intent> = ArrayList()

        var intentLaunched = false
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.startupapp.StartupAppListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.FakeActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startupapp.StartupAppListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safe",
                    "com.coloros.safe.permission.startup.StartupAppListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safe",
                    "com.coloros.safe.permission.startupapp.StartupAppListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safe",
                    "com.coloros.safe.permission.startupmanager.StartupAppListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startsettings"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startupapp.startupmanager"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startupmanager.startupActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.startupapp.startupmanager"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.privacypermissionsentry.PermissionTopActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.FakeActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            )
        )
        intentList.add(
            Intent().setComponent(
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.privacypermissionsentry.PermissionTopActivity"
                )
            )
        )

        // Chạy lần lượt các Intent từ danh sách
        for (intent in intentList) {
            try {
                context.startActivity(intent)
                intentLaunched = true // Đánh dấu rằng đã chạy thành công một Intent
                break // Thoát khỏi vòng lặp nếu chạy thành công
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Kiểm tra xem có Intent nào đã được chạy thành công không
        if (!intentLaunched) {
        }
    }

    fun needAutoStartup(context: Context?): Boolean {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            }
            val list = context?.packageManager?.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (!list.isNullOrEmpty()) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}