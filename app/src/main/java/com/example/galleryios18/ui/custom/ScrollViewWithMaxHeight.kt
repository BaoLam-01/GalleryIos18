package com.example.galleryios18.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class ScrollViewWithMaxHeight : ScrollView {
    private var maxHeight = -1

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    private fun init(context: Context) {
        maxHeight = (context.resources.displayMetrics.heightPixels * 0.4).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        try {
            var heightSize = MeasureSpec.getSize(heightMeasureSpec)
            if (maxHeight != -1 && heightSize > maxHeight) {
                heightSize = maxHeight
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST)
            layoutParams.height = heightSize
        } catch (ignored: Exception) {
        } finally {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}