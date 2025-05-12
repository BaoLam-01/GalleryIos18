package com.example.galleryios18.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.example.galleryios18.App
import com.example.galleryios18.R
import com.example.galleryios18.databinding.ActivityMainBinding
import com.example.galleryios18.utils.Utils
import com.example.galleryios18.ui.base.BaseBindingActivity

class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    var navController: NavController? = null
    private var navGraph: NavGraph? = null
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun setupView(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.TRANSPARENT
        initView()
    }

    override fun setupData() {
        App.instance.stateLifecycle.observe(this) {
            if (it) {
                Utils.startService(this)
            }
        }
        viewModel?.mLiveEventNavigateScreen?.observe(this) { screenId ->
            if (screenId is Int) {
                if (screenId == R.id.homeFragment) {
                    changeMainScreen(R.id.homeFragment, null)
                }
            }
        }


    }

    private fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController?.let {
            it.addOnDestinationChangedListener { _: NavController?, _: NavDestination, _: Bundle? ->
            }
        }
        navController?.setGraph(R.navigation.main_nav)
    }


    private fun changeMainScreen(idScreen: Int, bundle: Bundle?) {
        if (navGraph == null) {
            navGraph = navController?.navInflater?.inflate(R.navigation.main_nav)
        }
        navGraph?.setStartDestination(idScreen)
        navController?.setGraph(navGraph!!, bundle)
    }


    private fun checkDismissDialogs() {
    }

    override fun onDestroy() {
        super.onDestroy()
        checkDismissDialogs()
    }

}