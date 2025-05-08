package com.example.galleryios18.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.galleryios18.App
import com.example.galleryios18.R
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

object GlideUtils {
    val errorPath: ArrayList<String> = ArrayList()

    fun loadThumbSrc(
        src: String,
        callback: (Bitmap?) -> Unit
    ) {
        Glide.with(App.instance).asBitmap().load(src)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    fun loadThumbSrcScale(
        src: String,
        widthThumb: Int,
        heightThumb: Int,
        callback: (Bitmap?) -> Unit
    ) {
        Glide.with(App.instance).asBitmap().load(src).override(widthThumb, heightThumb)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    fun loadThumbBitmapScale(
        bitmap: Bitmap,
        name: String,
        widthThumb: Int,
        heightThumb: Int,
        callback: (Bitmap?) -> Unit
    ) {
        val tempFile = File(App.instance.cacheDir, "$name.png")
        FileOutputStream(tempFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        }
        Glide.with(App.instance).asBitmap().load(tempFile).override(widthThumb, heightThumb)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    fun loadThumbBitmapScaleCutInHalfTransformation(
        bitmap: Bitmap,
        name: String,
        widthThumb: Int,
        heightThumb: Int,
        isCreateBitmapBgrLeft: Boolean = false,
        callback: (Bitmap?) -> Unit
    ) {
        val tempFile = File(App.instance.cacheDir, "$name.png")
        FileOutputStream(tempFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        }
        Glide.with(App.instance)
            .asBitmap()
            .load(tempFile)
            .transform(CutInHalfTransformation(isCreateBitmapBgrLeft))
            .override(widthThumb, heightThumb)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun loadThumbBitmapScaleHorizontalFlipTransformation(
        bitmap: Bitmap,
        name: String,
        widthThumb: Int,
        heightThumb: Int,
        callback: (Bitmap?) -> Unit
    ) {
        val tempFile = File(App.instance.cacheDir, "$name.png")
        FileOutputStream(tempFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        }
        Glide.with(App.instance)
            .asBitmap()
            .load(tempFile)
            .override(widthThumb, heightThumb)
            .transform(HorizontalFlipTransformation())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    fun loadImage(imageView: AppCompatImageView, str: String, screenWidth: Int) {
        Glide.with(imageView.context)
            .load(str)
            .error(R.drawable.imagepicker_image_error)
            .placeholder(R.drawable.imagepicker_image_placeholder)
            .override(screenWidth / 3, (screenWidth / 3 * 2.04).toInt())
            .apply(RequestOptions().centerCrop())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    errorPath.add(str)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

            })
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}

class CutInHalfTransformation(private val isCreateBitmapBgrLeft: Boolean) : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return if (isCreateBitmapBgrLeft)
            Bitmap.createBitmap(
                toTransform, 0, 0, toTransform.width / 2, toTransform.height
            ) else Bitmap.createBitmap(
            toTransform, toTransform.width / 2, 0, toTransform.width / 2, toTransform.height
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("cut_in_half_transformation".toByteArray())
    }
}

class HorizontalFlipTransformation : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val matrix = Matrix()
        matrix.postScale(
            -1f,
            -1f,
            toTransform.width / 2f,
            toTransform.height / 2f
        )
        return Bitmap.createBitmap(
            toTransform,
            0,
            0,
            toTransform.width,
            toTransform.height,
            matrix,
            true
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("horizontal_flip_transformation".toByteArray())
    }
}