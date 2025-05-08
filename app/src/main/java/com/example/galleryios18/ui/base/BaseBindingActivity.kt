package com.example.galleryios18.ui.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider

abstract class BaseBindingActivity<B : ViewDataBinding?, VM : BaseViewModel> :
    BaseActivity() {
    var binding: B? = null
    var viewModel: VM? = null
    abstract val layoutId: Int

    abstract fun getViewModel(): Class<VM>
    abstract fun setupView(savedInstanceState: Bundle?)
    abstract fun setupData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this)[getViewModel()]
        setupView(savedInstanceState)
        setupData()
        changeSystemUIColor(
            Color.TRANSPARENT, Color.TRANSPARENT,
            darkStatusBar = false, darkNavigation = false
        )
    }

    fun changeSystemUIColor(
        colorStatusBar: Int,
        colorNavigationBar: Int,
        darkStatusBar: Boolean,
        darkNavigation: Boolean,
    ) {
        val systemUiScrim = Color.parseColor("#40000000")
        var systemUiVisibility = 0
        // Use a dark scrim by default since light status is API 23+
        val winParams = window.attributes
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (darkNavigation) {
                window.navigationBarColor = colorNavigationBar
            } else {
                window.navigationBarColor = systemUiScrim
            }
            if (darkStatusBar) {
                window.statusBarColor = colorStatusBar
            } else {
                window.statusBarColor = systemUiScrim
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!darkStatusBar) {
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.statusBarColor = colorStatusBar
            if (darkNavigation) {
                window.navigationBarColor = colorNavigationBar
            } else {
                window.navigationBarColor = systemUiScrim
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!darkNavigation) {
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            window.navigationBarColor = colorNavigationBar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    window.isNavigationBarContrastEnforced = false
                } catch (ignored: NoSuchMethodError) {
                }
            }
        }
        systemUiVisibility = systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = systemUiVisibility
        window.attributes = winParams
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
}