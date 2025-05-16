package com.example.galleryios18.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import com.alexvasilkov.gestures.views.GestureImageView

class DrawView : GestureImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val path = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isDither = true
        isFilterBitmap = true
        isAntiAlias = true
    }
    private var currentX = 0f
    private var currentY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        event.let {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_POINTER_DOWN -> {
    //                    return super.onTouchEvent(event)
                }

                MotionEvent.ACTION_DOWN -> {
                    path.reset()
                    path.moveTo(x, y)
                    currentX = x
                    currentY = y
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount < 2) {

                        path.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2)

                        currentX = x
                        currentY = y
                        return true
                    }


                }

                MotionEvent.ACTION_UP -> {
                    path.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2)
                    return true
                }

                MotionEvent.ACTION_POINTER_UP -> {
    //                    return super.onTouchEvent(event)
                }
            }

            invalidate()
    //            return true
        }
        return super.onTouchEvent(event)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

}