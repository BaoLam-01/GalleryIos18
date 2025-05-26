package com.example.galleryios18.ui.custom

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class FadeZoomPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width // giữ trang không trượt ngang

        when {
            position < -1 || position > 1 -> {
                // Trang ngoài tầm nhìn
                page.alpha = 0f
            }
            position <= 0 -> {
                // Trang hiện tại (sắp bị thay thế)
                page.alpha = 1 + position // mờ dần (0 đến 1)
                val scale = 1f + 0.1f * position // nhỏ dần
                page.scaleX = scale
                page.scaleY = scale
            }
            position <= 1 -> {
                // Trang kế tiếp (đang dần hiện ra)
                page.alpha = 1 - position // rõ dần (1 đến 0)
                val scale = 0.9f + 0.1f * (1 - position) // lớn dần
                page.scaleX = scale
                page.scaleY = scale
            }
        }
    }

}
