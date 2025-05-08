package com.example.galleryios18.utils

import android.view.View

fun View.enable(isEnable : Boolean) {
    isEnabled = isEnable
    isClickable = isEnable
}
