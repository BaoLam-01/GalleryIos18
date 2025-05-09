package com.example.galleryios18.ui.main.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentSplashBinding
import kotlinx.coroutines.Runnable
import com.example.galleryios18.ui.main.MainViewModel
import com.example.galleryios18.ui.base.BaseBindingFragment

class SplashFragment : BaseBindingFragment<FragmentSplashBinding, MainViewModel>() {
    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_splash

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if (isAdded) {
                navigateScreen(null, R.id.homeFragment)
            }
        }, 1000)
    }

    override fun needInsetTop(): Boolean {
        return true
    }

    override fun observerData() {

    }

}