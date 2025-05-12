package com.example.galleryios18.ui.custom

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView.LayoutManager

class FastPagerSnapHelper(private val context: Context) : PagerSnapHelper() {

    override fun createScroller(layoutManager: LayoutManager?): LinearSmoothScroller? {
        if (layoutManager == null) return null

        return object : LinearSmoothScroller(context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 20f / displayMetrics.densityDpi
            }
        }
    }
}