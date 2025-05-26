package com.example.galleryios18.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media

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
                fadeIn(viewItem1)
                fadeOut(viewItem2)
            } else {
                bindItem(viewItem2)
                fadeIn(viewItem2)
                fadeOut(viewItem1)
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
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private fun fadeIn(view: View) {
        view.animate().alpha(1f).setDuration(1000).withStartAction {
            view.visibility = VISIBLE
        }.start()
    }

    private fun fadeOut(view: View) {
        view.animate().alpha(0f).setDuration(1000).withEndAction {
            view.visibility = GONE
        }.start()
    }


    private fun bindItem(view: View) {
        val image = view.findViewById<ImageView>(R.id.img)
        val video = view.findViewById<TextureView>(R.id.video)
        val media = listItem[currentItemShow]

        if (media.isImage) {
            stop()
            image.visibility = VISIBLE
            video.visibility = GONE
            Glide.with(image).load(media.path).into(image)
        } else {
            val bitmapPreview = getFirstFrameVideo(media.path)
            if (bitmapPreview != null) {
                Glide.with(image).load(bitmapPreview).into(image)
                image.visibility = VISIBLE
            } else {
                image.visibility = GONE
            }
            video.visibility = VISIBLE
            video.alpha = 0f

            if (video.isAvailable) {
                playVideo(media.path, video) {
                    image.visibility = GONE
                    video.alpha = 1f
                }
            } else {
                video.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(
                        surface: android.graphics.SurfaceTexture,
                        width: Int,
                        height: Int
                    ) {
                        playVideo(media.path, video) {
                            image.visibility = GONE
                            video.alpha = 1f
                        }
                        video.surfaceTextureListener = null // xóa listener để tránh leak
                    }

                    override fun onSurfaceTextureSizeChanged(
                        surface: android.graphics.SurfaceTexture,
                        width: Int,
                        height: Int
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
                onStart() // 👈 thông báo video bắt đầu, để ẩn ảnh preview
            }
            prepareAsync()
        }
    }


    fun stop() {
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
        viewItem1.visibility = GONE
        viewItem2.visibility = GONE
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

}