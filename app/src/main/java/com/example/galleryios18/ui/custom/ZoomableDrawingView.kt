package com.example.galleryios18.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.hypot

class ZoomableDrawingView : AppCompatImageView {

    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentPath = Path()
    private var currentPaint = createNewPaint()

    private var brushColor = Color.BLACK
    private var brushSize = 10f

    private val drawMatrix = Matrix()
    private val inverseMatrix = Matrix()

    private var lastTouchDistance = 0f
    private var lastFocusX = 0f
    private var lastFocusY = 0f
    private var isMultiTouch = false
    private var ignoreDrawing = false
    private var hasDrawnSomething = false
    private var baseScale = 1f
    private var currentScale = 1f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = drawMatrix

    }

    private fun createNewPaint(): Paint {
        return Paint().apply {
            color = brushColor
            strokeWidth = brushSize
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }
    }

    fun setBrushColor(color: Int) {
        brushColor = color
        currentPaint = createNewPaint()
    }

    fun setBrushSize(size: Float) {
        brushSize = size
        currentPaint = createNewPaint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val drawable = drawable ?: return
        val dWidth = drawable.intrinsicWidth
        val dHeight = drawable.intrinsicHeight

        // scale image with width height
        val scaleX = w.toFloat() / dWidth.toFloat()
        val scaleY = h.toFloat() / dHeight.toFloat()
        baseScale = minOf(scaleX, scaleY)
        currentScale = baseScale

        // calculator distance, image center
        val dx = (w - dWidth * baseScale) / 2f
        val dy = (h - dHeight * baseScale) / 2f

        drawMatrix.reset()
        drawMatrix.postScale(baseScale, baseScale)
        drawMatrix.postTranslate(dx, dy)
        imageMatrix = drawMatrix
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

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return hypot(x2 - x1, y2 - y1)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.pointerCount) {
            1 -> handleSingleTouch(event)
            2 -> handleMultiTouch(event)
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

    private var currentTranslateX = 0f
    private var currentTranslateY = 0f

    private fun handleMultiTouch(event: MotionEvent) {
        val x1 = event.getX(0)
        val y1 = event.getY(0)
        val x2 = event.getX(1)
        val y2 = event.getY(1)

        val currentDistance = distance(x1, y1, x2, y2)
        val focusX = (x1 + x2) / 2f
        val focusY = (y1 + y2) / 2f

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                isMultiTouch = true
                ignoreDrawing = true
                lastTouchDistance = currentDistance
                lastFocusX = focusX
                lastFocusY = focusY

                // get translate matrix
                val values = FloatArray(9)
                drawMatrix.getValues(values)
                currentTranslateX = values[Matrix.MTRANS_X]
                currentTranslateY = values[Matrix.MTRANS_Y]
            }

            MotionEvent.ACTION_MOVE -> {
                val scaleFactor = currentDistance / lastTouchDistance
                var newScale = currentScale * scaleFactor

                val maxScale = baseScale * 2f
                val minScale = baseScale

                if (newScale > maxScale) newScale = maxScale
                if (newScale < minScale) newScale = minScale

                val realScaleFactor = newScale / currentScale

                // Scale at focus point
                drawMatrix.postTranslate(-focusX, -focusY)
                drawMatrix.postScale(realScaleFactor, realScaleFactor)
                drawMatrix.postTranslate(focusX, focusY)

                // Pan limit

                val dx = focusX - lastFocusX
                val dy = focusY - lastFocusY

                // get size image, view
                val drawable = drawable ?: return
                val dWidth = drawable.intrinsicWidth
                val dHeight = drawable.intrinsicHeight

                val imageWidth = dWidth * newScale
                val imageHeight = dHeight * newScale

                val viewWidth = width.toFloat()
                val viewHeight = height.toFloat()

                // get translate after scale
                val values = FloatArray(9)
                drawMatrix.getValues(values)
                var transX = values[Matrix.MTRANS_X] + dx
                var transY = values[Matrix.MTRANS_Y] + dy

                // get translate X
                // prevent translate out of view
                val minTranslateX = if (imageWidth <= viewWidth) {
                    (viewWidth - imageWidth) / 2f
                } else {
                    viewWidth - imageWidth
                }
                val maxTranslateX = if (imageWidth <= viewWidth) {
                    (viewWidth - imageWidth) / 2f
                } else {
                    0f
                }
                transX = clamp(transX, minTranslateX, maxTranslateX)

// get translate Y
                // prevent translate out of view
                val minTranslateY = if (imageHeight <= viewHeight) {
                    (viewHeight - imageHeight) / 2f
                } else {
                    viewHeight - imageHeight
                }
                val maxTranslateY = if (imageHeight <= viewHeight) {
                    (viewHeight - imageHeight) / 2f
                } else {
                    0f
                }
                transY = clamp(transY, minTranslateY, maxTranslateY)

                // update translate
                val scaleOnlyMatrix = Matrix()
                scaleOnlyMatrix.setScale(newScale, newScale)

                drawMatrix.set(scaleOnlyMatrix)
                drawMatrix.postTranslate(transX, transY)

                imageMatrix = drawMatrix
                invalidate()

                currentScale = newScale
                lastTouchDistance = currentDistance
                lastFocusX = focusX
                lastFocusY = focusY

                currentTranslateX = transX
                currentTranslateY = transY
            }

            MotionEvent.ACTION_POINTER_UP -> {
                isMultiTouch = false
                ignoreDrawing = true
            }
        }
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }

}


