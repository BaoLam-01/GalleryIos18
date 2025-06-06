package com.example.galleryios18.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        if (e?.pointerCount == 2) {
            // Trả về true để chặn click item
            return true
        }
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

    fun scrollItemToCenter(targetPosition: Int) {
        val layoutManager = this.layoutManager as LinearLayoutManager
        val position = targetPosition

// Tính toán offset để item nằm chính giữa
        val itemView = this.findViewHolderForAdapterPosition(position)?.itemView
        val itemHeight = itemView?.height ?: 0
        val recyclerViewHeight = this.height
        val offset = (recyclerViewHeight / 2) - (itemHeight / 2)
        layoutManager.scrollToPositionWithOffset(position, offset)
    }
}