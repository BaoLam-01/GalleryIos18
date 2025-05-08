package com.example.galleryios18.utils

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object OnBackPressed {
    fun onBackPressedFinishActivity(fragmentActivity: FragmentActivity, fragment: Fragment) {
        fragmentActivity.onBackPressedDispatcher.addCallback(
            fragment,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    fragmentActivity.finish()
                }
            })
    }
}