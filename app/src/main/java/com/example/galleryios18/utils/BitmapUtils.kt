package com.example.galleryios18.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.galleryios18.App


object BitmapUtils {
    fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return if (width > 0 && height > 0 && bitmap.width > 0 && bitmap.height > 0) {
            try {
                val tlW = width * 1f / bitmap.width
                val tlH = height * 1f / bitmap.height
                return if (tlW < tlH) {
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (height * (bitmap.width * 1f / bitmap.height)).toInt(), height, true
                    )
                } else Bitmap.createScaledBitmap(
                    bitmap,
                    width, (width * (bitmap.height * 1f / bitmap.width)).toInt(), true
                )
            } catch (e: OutOfMemoryError) {
                System.gc()
                bitmap
            }
        } else bitmap
    }

    fun scaleBitmapFit(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return if (width > 0 && height > 0) {
            return try {
                Bitmap.createScaledBitmap(bitmap, width, height, true)
            } catch (e: OutOfMemoryError) {
                System.gc()
                bitmap
            }
        } else bitmap
    }

    fun scaleBitmapFactor(originalBitmap: Bitmap?, scaleFactor: Float = 0.15f): Bitmap {
        val newWidth = (originalBitmap!!.width * scaleFactor).toInt()
        val newHeight = (originalBitmap.height * scaleFactor).toInt()
        return if (newWidth > 0 && newHeight > 0) {
            return try {
                Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            } catch (e: OutOfMemoryError) {
                System.gc()
                originalBitmap
            }
        } else originalBitmap
    }

    fun getBitmapFromAsset(path: String, width: Int, height: Int, callback: (Bitmap) -> Unit) {
        if (width > 0 && height > 0) {
            Glide.with(App.instance).asBitmap().load(path).override(width, height)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        callback(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        } else {
            Glide.with(App.instance).asBitmap().load(path)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        callback(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }

    @JvmStatic
    fun createFlippedBitmap(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(
            -1f,
            -1f,
            source.width / 2f,
            source.height / 2f
        )
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}