package com.example.galleryios18.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class SnappyRecyclerView : RecyclerView {
    private var snapHelper: MyLinearSnapHelper

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        snapHelper = MyLinearSnapHelper()
        snapHelper.attachToRecyclerView(this)
        addFadeOut()
    }

    private fun addFadeOut() {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }
        })
    }

    fun getPos(): Int {
        val view = snapHelper.findSnapView(layoutManager!!)

        return if (view != null) {
            getChildAdapterPosition(view)
        } else {
            -1
        }
    }
}