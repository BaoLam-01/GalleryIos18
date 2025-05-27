package com.example.galleryios18.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import timber.log.Timber

class StoryView : FrameLayout {

    private var currentItemShow = 0
    private lateinit var viewItem1: View
    private lateinit var viewItem2: View
    private val listItem = mutableListOf<Media>()
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (currentItemShow % 2 == 0) {
                bindItem(viewItem1)
                handleTransition(viewItem1, viewItem2)
            } else {
                bindItem(viewItem2)
                handleTransition(viewItem2, viewItem1)
            }
            currentItemShow++
            if (currentItemShow >= listItem.size) currentItemShow = 0
            handler.postDelayed(this, 3000)
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private fun handleTransition(viewIn: View, viewOut: View) {
        val random: Int = (0..3).random()
        Timber.e("LamPro - random: $random")
        when (random) {
            NONE -> {
                viewIn.visibility = VISIBLE
                viewOut.visibility = INVISIBLE
            }

            FADE -> {
                fadeIn(viewIn)
                fadeOut(viewOut)
            }

            SLIDE -> {
                slideIn(viewIn)
                slideOut(viewOut)
            }

            FADE_ZOOM -> {
                zoomOut(viewIn)
                fadeOut(viewOut)
            }
        }
    }

    private fun fadeIn(view: View) {
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(1000).withStartAction {
            view.visibility = VISIBLE
        }.start()
    }

    private fun fadeOut(view: View) {
        view.animate().alpha(0f).setDuration(1000).withEndAction {
            view.visibility = INVISIBLE
            view.alpha = 1f
        }.start()
    }

    private fun slideIn(view: View) {
        Timber.e("LamPro | slideIn - view width: ${view.width.toFloat()}")
        view.translationX = view.width.toFloat()
        view.animate().translationX(0f).setDuration(1000).withStartAction {
            view.visibility = VISIBLE
        }.start()
    }

    private fun slideOut(view: View) {
        view.animate().translationX(-view.width.toFloat()).setDuration(1000).withEndAction {
            view.visibility = INVISIBLE
            view.translationX = 0f
        }.start()
    }

    private fun zoomOut(view: View) {
        view.scaleX = 1.2f
        view.scaleY = 1.2f
        view.animate().scaleX(1f).scaleY(1f).setDuration(1000).withStartAction {
            view.visibility = VISIBLE
        }.start()
    }


    private fun bindItem(view: View) {
        val image = view.findViewById<ImageView>(R.id.img)
        val video = view.findViewById<TextureView>(R.id.video)
        val media = listItem[currentItemShow]

        if (media.isImage) {
            stopVideo()
            image.visibility = VISIBLE
            video.visibility = INVISIBLE
            image.scaleX = 1.1f
            image.scaleY = 1.1f
            image.animate().scaleX(1f).scaleY(1f).setDuration(3000).start()
            Glide.with(image).load(media.path).into(image)
        } else {
            val bitmapPreview = getFirstFrameVideo(media.path)
            if (bitmapPreview != null) {
                Glide.with(image).load(bitmapPreview).into(image)
                image.visibility = VISIBLE
            } else {
                image.visibility = INVISIBLE
            }
            video.visibility = VISIBLE
            video.alpha = 0f

            if (video.isAvailable) {
                playVideo(media.path, video) {
                    image.visibility = INVISIBLE
                    video.alpha = 1f
                }
            } else {
                video.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(
                        surface: android.graphics.SurfaceTexture,
                        width: Int,
                        height: Int,
                    ) {
                        playVideo(media.path, video) {
                            image.visibility = INVISIBLE
                            video.alpha = 1f
                        }
                        video.surfaceTextureListener = null
                    }

                    override fun onSurfaceTextureSizeChanged(
                        surface: android.graphics.SurfaceTexture,
                        width: Int,
                        height: Int,
                    ) {
                    }

                    override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean =
                        true

                    override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
                }
            }
        }
    }

    private fun getFirstFrameVideo(path: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val bitmap = retriever.getFrameAtTime(0)
        retriever.release()
        return bitmap
    }


    fun playVideo(path: String, textureView: TextureView, onStart: () -> Unit) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            setSurface(Surface(textureView.surfaceTexture))
            setOnPreparedListener {
                it.start()
                onStart()
            }
            prepareAsync()
        }
    }


    fun stopVideo() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!::viewItem1.isInitialized || !::viewItem2.isInitialized) {
            init()
        }
    }

    fun init() {
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        viewItem1 = inflate(context, R.layout.view_item_1, null)
        viewItem2 = inflate(context, R.layout.view_item_2, null)
        addView(viewItem1, layoutParams)
        addView(viewItem2, layoutParams)
        viewItem1.visibility = INVISIBLE
        viewItem2.visibility = INVISIBLE
    }

    fun setListItem(listItem: List<Media>) {
        this.listItem.clear()
        this.listItem.addAll(listItem)
        start()
    }

    private fun start() {
        if (listItem.isNotEmpty()) {
            handler.removeCallbacks(runnable)
            handler.post(runnable)
        }
    }

    private fun stop() {
        handler.removeCallbacks(runnable)
    }

    private var downX = 0f
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (event?.action == MotionEvent.ACTION_DOWN) {
//            stop()
//            if (currentItemShow % 2 == 0) {
//                downX = viewItem1.x - event.rawX
//            } else {
//                downX = viewItem2.x - event.rawX
//            }
//        } else if (event?.action == MotionEvent.ACTION_MOVE) {
//            translateItemShowing(event)
//        } else if (event?.action == MotionEvent.ACTION_UP) {
//            start()
//        }
//        return super.onTouchEvent(event)
//    }

    private fun translateItemShowing(event: MotionEvent) {
        if (currentItemShow % 2 == 0) {
            viewItem1.animate()
                .x(event.rawX + downX)
                .setDuration(0)
                .start()
        } else {
            viewItem2.animate()
                .x(event.rawX + downX)
                .setDuration(0)
                .start()
        }
    }

    companion object TransitionType {
        const val NONE = 0
        const val FADE = 1
        const val SLIDE = 2
        const val FADE_ZOOM = 3
    }

}