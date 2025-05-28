package com.example.galleryios18.ui.custom

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
    var gestureDetector: GestureDetector? = null
    private lateinit var gestureListener: GestureListener

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context?) {
        gestureListener = GestureListener(this)
        gestureDetector = GestureDetector(context, gestureListener)
        addOnItemTouchListener(this)

    }

    var isScrollEnabled = false

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return isScrollEnabled && super.onInterceptTouchEvent(e)
    }

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
        return true
    }

    override fun onTouchEvent(
        rv: RecyclerView,
        e: MotionEvent,
    ) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    inner class GestureListener(
        var recyclerView: ViewGroup,
    ) :
        GestureDetector.SimpleOnGestureListener() {
        private val Y_BUFFER = 10
        var isScrollOver: Boolean = false


        override fun onDown(e: MotionEvent): Boolean {
            // Prevent ViewPager from intercepting touch events as soon as a DOWN is detected.
            // If we don't do this the next MOVE event may trigger the ViewPager to switch
            // tabs before this view can intercept the event.
            recyclerView.getParent().requestDisallowInterceptTouchEvent(true)
            return super.onDown(e)
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float,
        ): Boolean {
            condition(isScrollOver)

            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        private fun condition(isScrollOver: Boolean) {
            if (isScrollOver) {
                // Detected a vertical scroll prevent the viewpager from switching tabs
                recyclerView.getParent().requestDisallowInterceptTouchEvent(true)
                //chan
            } else {
                // Detected a horizontal scroll, allow the viewpager from switching tabs
                recyclerView.getParent().requestDisallowInterceptTouchEvent(false)
                //khong chan
            }
        }
    }
}