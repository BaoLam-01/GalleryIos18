package com.filter.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View

object BitmapUtils {

    @JvmStatic
    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        return resizedBitmap
    }

    @JvmStatic
    fun resizedBitmap(bm: Bitmap?, newWidth: Int, newHeight: Int, fit: Boolean): Bitmap? {
        if (bm == null) {
            return null
        }
        val width = bm.width
        val height = bm.height
        if (width == newWidth && height == newHeight) {
            return bm
        }
        val paint = Paint(Paint.FILTER_BITMAP_FLAG)
        val bitmapNew = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmapNew)
        val w: Float
        val h: Float
        val tlH = height / (newHeight * 1f)
        val tlW = width / (newWidth * 1f)
        if (!fit) {
            if (tlH <= tlW) {
                h = newHeight.toFloat()
                w = h * width / height;

                val left = (newWidth - w) / 2f

                canvas.drawBitmap(
                    bm,
                    Rect(0, 0, width, height),
                    RectF(left, 0f, (left + w), h),
                    paint
                )
            } else {
                w = newWidth.toFloat()
                h = w * height / width
                val top = (newHeight - h) / 2f
                canvas.drawBitmap(
                    bm,
                    Rect(0, 0, width, height),
                    RectF(0f, top, w, (top + h)),
                    paint
                )
            }
        } else {
            if (tlH > tlW) {

                h = newHeight.toFloat()
                w = h * width / height

                val left = (newWidth - w) / 2f
                canvas.drawBitmap(
                    bm,
                    Rect(0, 0, width, height),
                    RectF(left, 0f, (left + w), h),
                    paint
                )

            } else {
                w = newWidth.toFloat()
                h = w * height / width

                val top = (newHeight - h) / 2f
                canvas.drawBitmap(
                    bm,
                    Rect(0, 0, width, height),
                    RectF(0f, top, w, (top + h)),
                    paint
                )
            }
        }

        return bitmapNew
    }


    @JvmStatic
    fun getBitmapFromView(bitmap1: Bitmap, v: View?): Bitmap? {
        if (v == null) return null
        if (v.width == 0 || v.height == 0) return null
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.drawColor(Color.BLACK)
        v.layout(v.left, v.top, v.right, v.bottom)
        val wBitmap = v.width
        val hBitmap = ((wBitmap / (bitmap1.width * 1f)) * bitmap1.height).toInt()
        val bitmap2 = Bitmap.createScaledBitmap(bitmap1, wBitmap, hBitmap, false)
        c.drawBitmap(bitmap2, 0f, 0f, null)
        v.draw(c)
        return b
    }

    @JvmStatic
    fun createFlippedBitmap(source: Bitmap): Bitmap? {
        val matrix = Matrix()
        matrix.postScale(
            -1f,
            -1f,
            source.width / 2f,
            source.height / 2f
        )
        var bitmap: Bitmap? = null
        bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        return bitmap
    }

}