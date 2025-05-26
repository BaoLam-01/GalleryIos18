package com.example.galleryios18.ui.custom

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val scale = 0.85f
        var alpha = 0.5f

        view.apply {
            when {
                position < -1 -> { // page ngoài bên trái
                    alpha = 0f
                }
                position <= 1 -> {
                    val scaleFactor = max(scale, 1 - abs(position))
                    val vertMargin = height * (1 - scaleFactor) / 2
                    val horzMargin = width * (1 - scaleFactor) / 2
                    translationX = if (position < 0) horzMargin - vertMargin / 2 else -horzMargin + vertMargin / 2

                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha = max(alpha, 1 - abs(position))
                }
                else -> { // page ngoài bên phải
                    alpha = 0f
                }
            }
        }
    }
}
