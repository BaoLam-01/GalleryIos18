package com.example.galleryios18.ui.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.net.toUri
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.galleryios18.common.models.Media
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber
import java.io.File

class StoryPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val imageView = AppCompatImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        alpha = 0f
    }

    private val videoView = PlayerView(context).apply {
        useController = false
        alpha = 0f
    }

    private val exoPlayer = ExoPlayer.Builder(context).build()

    private val handler = Handler(Looper.getMainLooper())
    private var mediaList: List<Media> = listOf()
    private var currentIndex = 0

    init {
        addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(videoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        videoView.player = exoPlayer
    }

    fun setMediaList(list: List<Media>) {
        mediaList = list
    }

    fun start() {
        currentIndex = 0
        playNext()
    }

    private fun playNext() {
        if (currentIndex >= mediaList.size) {
            currentIndex = 0
            return
        }

        val media = mediaList[currentIndex]
        val file = File(media.path)
        if (media.isImage) {
            showImage(Uri.fromFile(file))
        } else if (!media.isImage) {
            playVideo(Uri.fromFile(file))
        }

        currentIndex++
    }

    private fun showImage(uri: Uri) {
        Timber.e("LamPro | showImage - uri image : $uri")
        exoPlayer.pause()
        videoView.alpha = 0f
        Glide.with(context)
            .load(uri)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    imageView.setImageDrawable(resource)

                    imageView.scaleX = 1.1f
                    imageView.scaleY = 1.1f
                    imageView.alpha = 1f

                    imageView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(3000)
                        .setInterpolator(LinearInterpolator())
                        .start()

                    handler.postDelayed({
                        imageView.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .withEndAction { playNext() }
                            .start()
                    }, 3000)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
//        // Reset scale trước khi animate
//        imageView.scaleX = 1.1f
//        imageView.scaleY = 1.1f
//
//        // Start zoom out
//        val zoomAnim = imageView.animate()
//            .scaleX(1f)
//            .scaleY(1f)
//            .setDuration(3000)
//            .setInterpolator(LinearInterpolator())
//
//        zoomAnim.start()
//
//// Fade out sau 3s
//        handler.postDelayed({
//            imageView.animate()
//                .alpha(0f)
//                .setDuration(500)
//                .withEndAction { playNext() }
//                .start()
//        }, 3000)
    }

    private fun playVideo(uri: Uri) {
        Timber.e("LamPro | playVideo - uri video: $uri")
        imageView.alpha = 0f

        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        videoView.alpha = 0f
        videoView.animate().alpha(1f).setDuration(500).start()

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    videoView.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction {
                            playNext()
                        }.start()
                }
            }
        })
    }

    fun release() {
        exoPlayer.release()
    }
}
