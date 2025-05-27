package com.example.galleryios18.ui.custom

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout

class VideoPlayerView : FrameLayout {

    private val textureView = TextureView(context)
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


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        init()
    }
    private fun init() {
        textureView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(textureView)
    }


    fun playVideo(path: String, onStart: () -> Unit) {
        if (textureView.isAvailable) {
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
        } else {
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(
                    surface: android.graphics.SurfaceTexture,
                    width: Int,
                    height: Int,
                ) {
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
                    textureView.surfaceTextureListener = null
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

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
