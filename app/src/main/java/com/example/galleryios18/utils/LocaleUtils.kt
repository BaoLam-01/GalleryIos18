package com.example.galleryios18.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.models.Language
import com.example.galleryios18.common.models.ListLanguage
import com.example.galleryios18.ui.main.MainActivity
import java.util.Locale

object LocaleUtils {

    private var codeLanguageCurrent: String? = Constant.LANGUAGE_EN

    fun applyLocale(context: Context) {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(context)
        codeLanguageCurrent =
            preferences.getString(Constant.PREF_SETTING_LANGUAGE, Constant.LANGUAGE_EN)
        if (TextUtils.isEmpty(codeLanguageCurrent)) {
            codeLanguageCurrent = Constant.LANGUAGE_EN
        }
        val newLocale = Locale(codeLanguageCurrent)
        updateResource(context, newLocale)
        if (context !== context.applicationContext) {
            updateResource(context.applicationContext, newLocale)
        }
    }

    fun getLocalizedContext(context: Context): Context {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(context)
        val localeString = preferences.getString(Constant.PREF_SETTING_LANGUAGE, "")
        val locale = Locale(localeString ?: "en")
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }


    @JvmStatic
    fun isAtLeastSdkVersion(versionCode: Int): Boolean {
        return Build.VERSION.SDK_INT >= versionCode
    }

    private fun updateResource(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val resources = context.resources
        val current = getLocaleCompat(resources)
        if (current === locale) {
            return
        }
        val configuration = Configuration(resources.configuration)
        if (isAtLeastSdkVersion(Build.VERSION_CODES.N)) {
            configuration.setLocale(locale)
        } else if (isAtLeastSdkVersion(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getLocaleCompat(resources: Resources): Locale {
        val configuration = resources.configuration
        return if (isAtLeastSdkVersion(Build.VERSION_CODES.N)) configuration.locales[0] else configuration.locale
    }

    fun applyLocaleAndRestart(activity: Activity, localeString: String?) {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(activity)
        preferences.edit().putString(Constant.PREF_SETTING_LANGUAGE, localeString).apply()
        applyLocale(activity)
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(intent)
        ActivityCompat.finishAffinity(activity)
    }

    fun applyLocalInFirst(activity: Activity, localeString: String?) {
        val preferences = PreferenceManager
            .getDefaultSharedPreferences(activity)
        preferences.edit().putString(Constant.PREF_SETTING_LANGUAGE, localeString).apply()
        applyLocale(activity)
    }

    fun getLanguages(context: Context): List<Language> {
        val list: MutableList<Language> = ArrayList()
        list.add(Language(Constant.LANGUAGE_EN, context.resources.getString(R.string.english)))
        list.add(Language(Constant.LANGUAGE_VN, context.resources.getString(R.string.vn)))
        return list
    }

    fun getListLanguage(context: Context): ListLanguage {
        val list: List<Language> = getLanguages(context)
        var posSelected = 0
        for (i in list.indices) {
            if (list[i].codeLocale == codeLanguageCurrent
            ) {
                posSelected = i
                break
            }
        }
        return ListLanguage(list, posSelected)
    }

}