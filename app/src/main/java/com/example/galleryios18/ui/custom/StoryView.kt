package com.example.galleryios18.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import timber.log.Timber
import kotlin.math.abs

class StoryView : FrameLayout {

    private var currentItemShow = 0
    private lateinit var viewItem1: View
    private lateinit var viewItem2: View
    private val listItem = mutableListOf<Media>()
    private val handler = Handler(Looper.getMainLooper())

    private var lastPostTime = 0L
    private var delayMillis = 3000L
    private var remainingDelay = delayMillis
    private var velocityTracker: VelocityTracker? = null
    private val flingThresholdVelocity = 1000  // đơn vị: px/s
    private var resetAlphaOneTime = false
    private var bindItemWhenMoveChange = 0

    private val runnable = object : Runnable {
        override fun run() {
            currentItemShow++
            if (currentItemShow >= listItem.size) currentItemShow = 0
            lastPostTime = System.currentTimeMillis()
            if (currentItemShow % 2 == 0) {
                bindItem(viewItem1, currentItemShow)
                handleTransition(viewItem1, viewItem2)
            } else {
                bindItem(viewItem2, currentItemShow)
                handleTransition(viewItem2, viewItem1)
            }
            handler.postDelayed(this, delayMillis)
        }
    }

    private var downX = 0f
    private var isSwiping = false
    private var swipeThreshold = 200

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
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
                resetPositionItemView()
                viewIn.visibility = VISIBLE
                viewOut.visibility = INVISIBLE
                clearDataView(viewOut)
            }

            FADE -> {
                resetPositionItemView()
                fadeIn(viewIn)
                fadeOut(viewOut)
            }

            SLIDE -> {
                slideIn(viewIn)
                slideOut(viewOut)
            }

            FADE_ZOOM -> {
                resetPositionItemView()
                zoomOut(viewIn)
                fadeOut(viewOut)
            }
        }
    }

    private fun resetPositionItemView() {
        viewItem1.translationX = 0f
        viewItem1.translationY = 0f
        viewItem1.alpha = 1f
        viewItem1.alpha = 1f

        viewItem2.translationX = 0f
        viewItem2.translationY = 0f
        viewItem2.alpha = 1f
        viewItem2.alpha = 1f
    }

    private fun clearDataView(viewOut: View) {
        val image = viewOut.findViewById<ImageView>(R.id.img)
        val video = viewOut.findViewById<VideoPlayerView>(R.id.video)
        Glide.with(this).clear(image)
        video.stop()
    }

    private fun fadeIn(viewIn: View) {
        viewIn.alpha = 0f
        viewIn.animate().alpha(1f).setDuration(1000).withStartAction {
            viewIn.visibility = VISIBLE
        }.start()
    }

    private fun fadeOut(viewOut: View) {
        viewOut.animate().alpha(0f).setDuration(1000).withEndAction {
            viewOut.visibility = INVISIBLE
            viewOut.alpha = 1f
            clearDataView(viewOut)
        }.start()
    }

    private fun slideIn(viewIn: View) {
        viewIn.translationX = viewIn.width.toFloat()
        viewIn.animate().translationX(0f).setDuration(1000).withStartAction {
            viewIn.visibility = VISIBLE
        }.start()
    }

    private fun slideOut(viewOut: View) {
        viewOut.animate().translationX(-viewOut.width.toFloat()).setDuration(1000).withEndAction {
            viewOut.visibility = INVISIBLE
            viewOut.translationX = 0f
            clearDataView(viewOut)
        }.start()
    }

    private fun zoomOut(viewIn: View) {
        viewIn.scaleX = 1.2f
        viewIn.scaleY = 1.2f
        viewIn.animate().scaleX(1f).scaleY(1f).setDuration(1000).withStartAction {
            viewIn.visibility = VISIBLE
        }.start()
    }


    private fun bindItem(viewIn: View, position: Int) {
        val image = viewIn.findViewById<ImageView>(R.id.img)
        val video = viewIn.findViewById<VideoPlayerView>(R.id.video)
        val media = listItem[position]

        if (media.isImage) {
            Timber.e("LamPro | bindItem - is image")
            image.visibility = VISIBLE
            video.visibility = INVISIBLE
            image.scaleX = 1.1f
            image.scaleY = 1.1f
            image.animate().scaleX(1f).scaleY(1f).setDuration(3000).start()
            Glide.with(this).load(media.path).signature(ObjectKey(media.id)).into(image)
        } else {
            Timber.e("LamPro | bindItem - is video")
            val bitmapPreview = getFirstFrameVideo(media.path)
            if (bitmapPreview != null) {
                Glide.with(this).load(bitmapPreview).signature(ObjectKey(media.id)).into(image)
                image.visibility = VISIBLE
            } else {
                image.visibility = INVISIBLE
            }

            video.visibility = VISIBLE
            video.alpha = 0f
            video.setDataVideo(media.path) {
                video.alpha = 1f
                image.visibility = INVISIBLE
                if (!isSwiping) {
                    video.start()
                }
            }
        }
    }

    private fun getFirstFrameVideo(path: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            retriever.getFrameAtTime(1_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!::viewItem1.isInitialized || !::viewItem2.isInitialized) {
            init()
        }
    }

    fun init() {
        Timber.e("LamPro | init - swipe threshold: $swipeThreshold")
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        viewItem1 = inflate(context, R.layout.view_item_1, null)
        viewItem2 = inflate(context, R.layout.view_item_2, null)
        addView(viewItem1, layoutParams)
        addView(viewItem2, layoutParams)
        viewItem1.visibility = INVISIBLE
        viewItem2.visibility = INVISIBLE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        swipeThreshold = width / 2
    }

    fun setListItem(listItem: List<Media>) {
        this.listItem.clear()
        this.listItem.addAll(listItem)
        start()
    }

    private fun start() {
        Timber.e("LamPro | start - ")
        if (listItem.isNotEmpty()) {
            bindFirstItem()
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, delayMillis)
            lastPostTime = System.currentTimeMillis()
        }
    }

    private fun bindFirstItem() {
        bindItem(viewItem1, currentItemShow)
        viewItem1.visibility = VISIBLE
        viewItem2.visibility = INVISIBLE
    }


    private fun stopRunnable() {
        Timber.e("LamPro | stop - ")
        handler.removeCallbacks(runnable)
        val elapsed = System.currentTimeMillis() - lastPostTime
        remainingDelay = delayMillis - elapsed
    }

    private fun continueRunnable() {
        if (remainingDelay < 1000) {
            remainingDelay = 1000
        }
        handler.postDelayed(runnable, remainingDelay)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (listItem.isEmpty()) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                resetAlphaOneTime = false
                bindItemWhenMoveChange = 0
                Timber.e("LamPro | onTouchEvent - down")
                downX = event.x
                isSwiping = true
                stopVideo()
                stopRunnable()
                cancelAnimate()
                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isSwiping) return false

                if (resetAlphaOneTime == false) {
                    resetViewWhenMove()
                    resetAlphaOneTime = true
                }
                velocityTracker?.addMovement(event)
                val deltaX = event.x - downX
                Timber.e("LamPro | onTouchEvent - deltax : $deltaX")
                getCurrentView().translationX = deltaX

                Timber.e("HaiPd | onTouchEvent - deltax : $deltaX")
                if (abs(deltaX) >= 50) {
                    if (deltaX > 0) {
                        showItemLeft(deltaX)
                    } else {
                        showItemRight(deltaX)
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isSwiping) return false
                isSwiping = false
                resetAlphaOneTime = false
                bindItemWhenMoveChange = 0

                velocityTracker?.addMovement(event)
                velocityTracker?.computeCurrentVelocity(1000)  // px/s
                val velocityX = velocityTracker?.xVelocity ?: 0f
                velocityTracker?.recycle()
                velocityTracker = null
                val isFling = abs(velocityX) > flingThresholdVelocity

                Timber.e("LamPro | onTouchEvent - up")


                val deltaX = event.x - downX
                if (isFling) {
                    if (velocityX < 0) {
                        goToNext()
                    } else {
                        goToPrevious()
                    }
                } else if (abs(deltaX) > swipeThreshold) {
                    if (deltaX < 0) {
                        goToNext()
                    } else {
                        goToPrevious()
                    }
                } else {
                    getCurrentView().animate()?.translationX(0f)?.setDuration(300)?.start()
                    resetUnusedView(deltaX)
                }
                viewItem1.alpha = 1f
                viewItem2.alpha = 1f

                startVideo()
                continueRunnable()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun showItemRight(deltaX: Float) {
        if (currentItemShow <= listItem.size - 2) {
            val itemNext = currentItemShow + 1
            if (itemNext % 2 == 0) {
                Timber.e("LamPro | onTouchEvent - viewitem1")
                if (bindItemWhenMoveChange == 0 || bindItemWhenMoveChange > 0) {
                    bindItem(viewItem1, itemNext)
                }
                viewItem1.translationX = deltaX + 50 + viewItem1.width

            } else {

                Timber.e("LamPro | onTouchEvent - viewitem2")
                if (bindItemWhenMoveChange == 0 || bindItemWhenMoveChange > 0) {
                    bindItem(viewItem2, itemNext)
                }
                viewItem2.translationX = deltaX + 50 + viewItem2.width
            }
            bindItemWhenMoveChange = -1
        }
    }

    private fun showItemLeft(deltaX: Float) {
        if (currentItemShow >= 1) {
            val itemPrevious = currentItemShow - 1
            if (itemPrevious % 2 == 0) {
                Timber.e("LamPro | onTouchEvent - viewitme1")
                if (bindItemWhenMoveChange == 0 || bindItemWhenMoveChange < 0) {
                    bindItem(viewItem1, itemPrevious)
                }
                viewItem1.translationX = -viewItem1.width.toFloat() - 50 + deltaX

            } else {
                Timber.e("LamPro | onTouchEvent - viewitme2")
                if (bindItemWhenMoveChange == 0 || bindItemWhenMoveChange < 0) {
                    bindItem(viewItem2, itemPrevious)
                }
                viewItem2.translationX = -viewItem1.width.toFloat() - 50 + deltaX
            }
            bindItemWhenMoveChange = 1
        }
    }

    private fun stopVideo() {
        val media = listItem[currentItemShow]
        if (!media.isImage) {
            getCurrentView().findViewById<VideoPlayerView>(R.id.video).pause()
        }
    }

    private fun startVideo() {
        val media = listItem[currentItemShow]
        if (!media.isImage) {
            getCurrentView().findViewById<VideoPlayerView>(R.id.video).start()
        }

    }

    private fun resetViewWhenMove() {
        viewItem1.visibility = VISIBLE
        viewItem1.alpha = 1f
        viewItem1.scaleX = 1f
        viewItem1.scaleY = 1f
        viewItem2.visibility = VISIBLE
        viewItem2.alpha = 1f
        viewItem2.scaleX = 1f
        viewItem2.scaleY = 1f

    }

    private fun cancelAnimate() {
        viewItem1.animate().cancel()
        viewItem2.animate().cancel()
    }

    private fun resetAlphaItemView() {
        viewItem1.alpha = 1f
        viewItem2.alpha = 1f
    }

    private fun goToNext() {
        if (currentItemShow < listItem.size - 1) {
            currentItemShow++
            if (currentItemShow % 2 == 0) {
                viewItem1.animate().translationX(0f).setDuration(300).start()
                viewItem2.animate().translationX(-viewItem2.width.toFloat()).setDuration(300)
                    .start()
            } else {
                viewItem2.animate().translationX(0f).setDuration(300).start()
                viewItem1.animate().translationX(-viewItem1.width.toFloat()).setDuration(300)
                    .start()
            }
            remainingDelay = delayMillis
        } else {
            getCurrentView().animate()?.translationX(0f)?.setDuration(300)?.start()
        }
    }

    private fun goToPrevious() {
        if (currentItemShow > 0) {
            currentItemShow--
            if (currentItemShow % 2 == 0) {
                viewItem1.animate().translationX(0f).setDuration(300).start()
                viewItem2.animate().translationX(viewItem2.width.toFloat()).setDuration(300).start()
            } else {
                viewItem2.animate().translationX(0f).setDuration(300).start()
                viewItem1.animate().translationX(viewItem1.width.toFloat()).setDuration(300).start()
            }
            remainingDelay = delayMillis
        } else {
            getCurrentView().animate()?.translationX(0f)?.setDuration(300)?.start()
        }
    }

    private fun resetUnusedView(deltaX: Float) {
        if (deltaX < 0 && currentItemShow < listItem.size - 1) {
            if (currentItemShow % 2 == 0) {
                viewItem2.animate().translationX(viewItem2.width.toFloat()).setDuration(300).start()
            } else {
                viewItem1.animate().translationX(viewItem1.width.toFloat()).setDuration(300).start()
            }
        } else if (deltaX > 0 && currentItemShow > 0) {
            if (currentItemShow % 2 == 0) {
                viewItem2.animate().translationX(-viewItem2.width.toFloat()).setDuration(300)
                    .start()
            } else {
                viewItem1.animate().translationX(-viewItem1.width.toFloat()).setDuration(300)
                    .start()
            }
        }
    }

    private fun getCurrentView(): View {
        return if (currentItemShow % 2 == 0) viewItem1 else viewItem2
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRunnable()
    }

    companion object TransitionType {
        const val NONE = 0
        const val FADE = 1
        const val SLIDE = 2
        const val FADE_ZOOM = 3
    }

}