package com.example.galleryios18.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorInt
import com.github.chrisbanes.photoview.OnScaleChangedListener
import com.github.chrisbanes.photoview.PhotoView
import timber.log.Timber

class MyCustomImageView : PhotoView {


    private val currentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 6f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentPath: Path = Path()
    private var drawMatrix = Matrix()
    private val inverseMatrix = Matrix()
    private var ignoreDrawing = false
    private var hasDrawnSomething = false
    private var isMultiTouch = false


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun clearDrawing() {
        paths.clear()
        invalidate()
    }

    fun setStrokeColor(@ColorInt color: Int) {
        currentPaint.color = color
        invalidate()
    }

    fun setStrokeWidth(width: Float) {
        currentPaint.strokeWidth = width
        invalidate()
    }

    init {
        setDrag(false)
        attacher.setOnScaleChangeListener(object : OnScaleChangedListener {
            override fun onScaleChange(
                scaleFactor: Float,
                focusX: Float,
                focusY: Float
            ) {
                Timber.e("LamPro | onScaleChange - ")
                getSuppMatrix(drawMatrix)

            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.concat(drawMatrix)

        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }

        if (!ignoreDrawing) {
            canvas.drawPath(currentPath, currentPaint)
        }

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        if (event.pointerCount == 1) {
            handleSingleTouch(event)
        } else {
            ignoreDrawing = true
        }

        return true
    }


    private fun handleSingleTouch(event: MotionEvent) {
        val mapped = floatArrayOf(event.x, event.y)
        drawMatrix.invert(inverseMatrix)
        inverseMatrix.mapPoints(mapped)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount > 1 || isMultiTouch) return
                ignoreDrawing = false
                hasDrawnSomething = false
                currentPath.moveTo(mapped[0], mapped[1])
            }

            MotionEvent.ACTION_MOVE -> {
                if (isMultiTouch || ignoreDrawing) return
                currentPath.lineTo(mapped[0], mapped[1])
                hasDrawnSomething = true
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (isMultiTouch || ignoreDrawing) {
                    currentPath.reset()
                    return
                }
                if (hasDrawnSomething) {
                    paths.add(Pair(Path(currentPath), Paint(currentPaint)))
                }
                currentPath.reset()
                invalidate()
            }
        }
    }

}
