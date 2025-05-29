package com.example.galleryios18.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.galleryios18.App.Companion.instance
import timber.log.Timber
import kotlin.math.abs

class CustomRecycleView : RecyclerView, RecyclerView.OnItemTouchListener {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addOnItemTouchListener(this)
    }

    var isScrollEnabled = true

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return isScrollEnabled && super.onInterceptTouchEvent(e)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return isScrollEnabled && super.onTouchEvent(e)
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        return isScrollEnabled && super.fling(velocityX, velocityY)
    }

    override fun onInterceptTouchEvent(
        rv: RecyclerView,
        e: MotionEvent,
    ): Boolean {
        return false
    }

    override fun onTouchEvent(
        rv: RecyclerView,
        e: MotionEvent,
    ) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}