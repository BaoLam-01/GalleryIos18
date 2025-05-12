package com.example.galleryios18

import android.content.res.Configuration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.data.local.SharedPreferenceHelper
import com.example.galleryios18.manager.AnalyticsManager
import com.example.galleryios18.utils.LocaleUtils
import com.example.galleryios18.utils.MyDebugTree
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class App : MultiDexApplication() {
    @Inject
    lateinit var mPrefHelper: SharedPreferenceHelper
    val stateLifecycle = MutableLiveData<Boolean>()
    var isAppInForeground: Boolean = false
    var currentMediaShow: Media? = null
    var currentPositionShow: Int = -1

    override fun onCreate() {
        super.onCreate()
        instance = this
        initLog()
        initLanguage()
//        enableFirebaseCrashlytics(false)
        AnalyticsManager.init(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver);

    }

    private fun initLanguage() {
        var newUsing = mPrefHelper.getInt(Constant.NEW_USING, 0)
        if (newUsing == 0) {
            newUsing = 1
            mPrefHelper.storeInt(Constant.NEW_USING, newUsing)
            for (i in LocaleUtils.getLanguages(this).indices) {
                val newLocale = Locale(LocaleUtils.getLanguages(this)[i].codeLocale)
                if (newLocale.language.contains(LocaleUtils.getLocaleCompat(baseContext.resources).language)) {
                    mPrefHelper.storeString(
                        Constant.PREF_SETTING_LANGUAGE, LocaleUtils.getLanguages(this)[i].codeLocale
                    )
                    break
                }
            }
        }
    }

    private fun enableFirebaseCrashlytics(enable: Boolean) {
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(enable)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils.applyLocale(this)
    }

    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(MyDebugTree())
        }
    }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                isAppInForeground = false
                stateLifecycle.postValue(false)
            }

            Lifecycle.Event.ON_START -> {
                isAppInForeground = true
                stateLifecycle.postValue(true)
            }

            else -> {}
        }
    }


    companion object {
        @JvmStatic
        lateinit var instance: App
    }

}
