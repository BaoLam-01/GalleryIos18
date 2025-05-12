package com.example.galleryios18.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.example.galleryios18.data.local.SharedPreferenceHelper
import com.example.galleryios18.utils.LocaleUtils
import com.example.galleryios18.utils.ViewUtils
import javax.inject.Inject


@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var mPrefHelper: SharedPreferenceHelper
    var statusBarHeight = 0
    var navigationBarHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleUtils.applyLocale(this)
        super.onCreate(savedInstanceState)
        statusBarHeight = ViewUtils.getStatusBarHeight(this)
        navigationBarHeight = ViewUtils.getNaviBarHeight(this)
    }

    override fun attachBaseContext(newBase: Context) {
        LocaleUtils.applyLocale(newBase)
        super.attachBaseContext(newBase)
    }
}
