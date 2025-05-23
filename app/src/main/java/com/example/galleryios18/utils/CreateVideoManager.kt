package com.example.galleryios18.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import com.base.capva.common.Common
import com.example.galleryios18.R
import com.example.galleryios18.feature.TemplateView
import com.example.galleryios18.interfaces.OnSaveVideoListener
import com.example.galleryios18.utils.createvideo.BitmapToVideoEncoder
import kotlinx.coroutines.Runnable
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.Calendar

class CreateVideoManager(
    val context: Activity,
    private val view: FrameLayout,
    private var templates: ArrayList<TemplateView>,
    private val width: Int,
    private val height: Int,
    private var duration: Int,
    private val onSaveVideoListener: OnSaveVideoListener
) {
    private var bitmapToVideoEncoder: BitmapToVideoEncoder? = null

    private var mediaPlayer: MediaPlayer? = null
    var previewing = false
    private var isPreview = false
    private var pathAudio = ""
    private var timeStartAudio = 0
    private var durationAudioSelected = 0
    private var durationAudio = 0
    private var positionFrame = 0
    private val frameRateVideo = Common.FRAME_RATE
    private val durationOneFrame = 1000 / frameRateVideo
    private var timeFrame = 0
    private val handle = Handler(Looper.getMainLooper())
    private var typeSave = Common.TYPE_SAVE_IMAGE
    private var listTemplateSave = ArrayList<Boolean>()
    private var thread: Thread? = null
    private var stopThread = true
    private val runnableTime = java.lang.Runnable {
        while (timeFrame < duration && !stopThread) {
            try {
                timeFrame += durationOneFrame
                Thread.sleep(durationOneFrame.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun startThread() {
        stopThread = false
        thread = Thread(runnableTime)
        thread!!.start()
    }


    private val runnable = java.lang.Runnable {
        setFrameInTime()
        if (!isPreview) {
            getFrame()
        } else {
            onSaveVideoListener.onProcessPreview(timeFrame)
        }
        if (!isPreview) {
            timeFrame += durationOneFrame
        }
        checkTime()
    }

    private fun setFrameInTime() {
        for (t in templates) {
            if (timeFrame >= t.delayPage && timeFrame <= t.delayPage + t.durationPageSave) {
                t.visibility = View.VISIBLE
            } else {
                t.visibility = View.INVISIBLE
            }
            t.setFrameInTime(timeFrame)
        }
    }


    private fun checkTime() {
        if (isPreview) {
            if (timeFrame <= duration) {
                handle.postDelayed(runnable, durationOneFrame.toLong())
            } else {
                releaseThread()
                timeFrame = 0
                previewing = false
                handlerMedia.removeCallbacks(runnableMedia)
                onSaveVideoListener.onStopPreview(true)
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                    }
                }
            }
        } else {
            if (timeFrame <= duration) {
                handle.postDelayed(runnable, 50)
            } else {
                bitmapToVideoEncoder?.stopEncoding()
            }
        }
    }

    private fun getFrame() {
        val bitmapRender = getBitmap()

        bitmapToVideoEncoder?.queueFrame(bitmapRender)
    }

    private fun getBitmap(): Bitmap {
//        val bitmap = if (isRelease) {
//            Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
//        } else {
//            view.drawToBitmap(Bitmap.Config.ARGB_8888)
//            val b = Bitmap.createBitmap(view.layoutParams.width, view.layoutParams.height, Bitmap.Config.ARGB_8888)
//            val c = Canvas(b)
//            view.layout(view.left, view.top, view.right, view.bottom)
//            view.draw(c)
//            b
//        }
//        return bitmap

        val b = Bitmap.createBitmap(
            view.layoutParams.width,
            view.layoutParams.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(c)
        return b
    }

    fun setupAudio(
        pathAudio: String,
        timeStartAudio: Int,
        durationSelected: Int,
        durationAudio: Int
    ) {
        this.pathAudio = pathAudio
        this.timeStartAudio = timeStartAudio
        this.durationAudioSelected = durationSelected
        this.durationAudio = durationAudio
    }

    fun setupAudioPreview(
        pathAudio: String,
        timeStartAudio: Int,
        durationSelected: Int
    ) {
        this.timeStartAudio = timeStartAudio
        this.durationAudioSelected = durationSelected
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.let {
                try {
//                    val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(pathAudio)
//                    it.setDataSource(
//                        assetFileDescriptor.fileDescriptor,
//                        assetFileDescriptor.startOffset,
//                        assetFileDescriptor.length
//                    )
                    it.setDataSource(pathAudio)
                    it.prepare()
                    it.isLooping = true
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setupSave( d: Int, t: ArrayList<TemplateView>) {
        templates = t
        duration = d
    }

    fun startEncoding() {
        startEncodeVideo()
    }

    private fun saveImage() {
        val listPath = arrayOfNulls<String>(templates.size)
        for (i in 0 until templates.size) {
            showTemplate(i)
            val bitmap = getBitmap()
            val path = MethodUtils.saveBitmap(context, bitmap)
            listPath[i] = path
        }
        scanFile(listPath)
    }

    private fun showTemplate(positionSave: Int) {
        for (i in 0 until templates.size) {
            if (i == positionSave) {
                templates[i].visibility = View.VISIBLE
            } else {
                templates[i].visibility = View.INVISIBLE
            }
        }
    }

    private fun scanFile(paths: Array<String?>?) {
        if (paths == null) {
            onSaveVideoListener.onSaveSuccess(ArrayList())
        } else {
            var dem = 0
            val list = ArrayList<String>()
//            MediaScannerConnection.scanFile(context, paths, null) { path, _ ->
//                dem++
//                list.add(path)
//                if (dem == paths.size) {
//                    onSaveVideoListener.onSaveSuccess(list)
//                }
//            }
            paths.forEach { path ->
                MediaScannerConnection.scanFile(context, arrayOf(path), null)
                { _, _ ->
                    dem++
                    path?.let { list.add(it) }
                    if (dem == paths.size) {
                        onSaveVideoListener.onSaveSuccess(list)
                    }
                }
            }
        }
    }

    private fun startEncodeVideo() {
        if (pathAudio.isEmpty()) {
            beginEncoding("")
        } else {
            convertAudio()
        }
    }

    private fun convertAudio() {
        val convertAudioLocal =
            ConvertAudioLocal(object : ConvertAudioLocal.OnConvertAudioListener {
                override fun onConvertSuccess(path: String) {
                    beginEncoding(path)
                }

                override fun onConvertFailed(path: String?) {
                    onSaveVideoListener.onSaveFailure(context.getString(R.string.error))
                }
            })

        convertAudioLocal.convertAudio(
            context,
            pathAudio,
            timeStartAudio.toLong(),
            durationAudioSelected.toLong(),
            durationAudio
        )
    }

    private fun beginEncoding(pathAudioConvert: String) {
        isPreview = false
        timeFrame = 0
        positionFrame = 0
        val outputFile: File
        val root =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File(root + File.separator + context.getString(R.string.app_name))
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val videoName =
            context.getString(R.string.app_name) + "_" + Calendar.getInstance().timeInMillis
        outputFile = File(myDir.absolutePath + File.separator + videoName + ".mp4")
        bitmapToVideoEncoder = BitmapToVideoEncoder(context)
        bitmapToVideoEncoder!!.setupEncoder(
            outputFile.absolutePath,
            width,
            height,
            object : BitmapToVideoEncoder.OnBitmapToVideoEncoderListener {
                override fun onStartEncoding() {
                }

                override fun onReadyDrawFrame() {
                    context.runOnUiThread {
                        prepareSave()
                        handle.removeCallbacks(runnable)
                        handle.post(runnable)
                    }

                }

                override fun onProcess(process: Int) {
                    context.runOnUiThread {
                        onSaveVideoListener.onProgress(((process * 100f) / duration).toInt())
                    }
                }

                override fun onSuccess(path: String?) {
                    if (outputFile != null && outputFile.exists()) {
                        scanFile(arrayOf(outputFile.absolutePath))
                    } else {
                        scanFile(null)
                    }
                }

                override fun onFailure() {
                    context.runOnUiThread {
                        onSaveVideoListener.onSaveFailure(context.getString(R.string.error))
                    }
                }

            })
        if (pathAudioConvert.isNotEmpty()) {
            bitmapToVideoEncoder!!.setupAudio(pathAudioConvert)
        }
        bitmapToVideoEncoder!!.startEncoding()
    }

    fun prepareSave() {
        for (t in templates) {
            t.prepareSave()
        }
    }

    fun startPreview() {
        isPreview = true
        previewing = true
        if (timeFrame > duration) {
            timeFrame = 0
        }
//        prepareSave()
        handle.removeCallbacks(runnable)
        handle.post(runnable)
        mediaPlayer?.start()
        if (timeFrame == 0) {
            mediaPlayer?.seekTo(timeStartAudio)
        } else {
            var time = 0
            val totalTime = durationAudioSelected + timeStartAudio
            if (totalTime > 0) {
                time = (timeStartAudio + timeFrame) % totalTime
            }
            mediaPlayer?.seekTo(time)
        }
        postDelayMedia()
        onSaveVideoListener.onStartPreview()
        startThread()
    }

    fun stopPreview() {
        previewing = false
        handle.removeCallbacks(runnable)
        onSaveVideoListener.onStopPreview(false)
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        handlerMedia.removeCallbacks(runnableMedia)
        releaseThread()
    }

    fun setTimePreview(progress: Int) {
        timeFrame = progress
        setFrameInTime()
    }

    fun abortEncoding() {
        handle.removeCallbacks(runnable)
        bitmapToVideoEncoder?.abortEncoding()
    }

    fun release() {
        handlerMedia.removeCallbacks(runnableMedia)
        handle.removeCallbacks(runnable)
        bitmapToVideoEncoder?.abortEncoding()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        releaseThread()
    }

    private fun releaseThread() {
        stopThread = true
        thread?.interrupt()
        thread = null
    }

    private val handlerMedia = Handler(Looper.getMainLooper())
    private val runnableMedia = java.lang.Runnable {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    private fun postDelayMedia() {
        handlerMedia.postDelayed(runnableMedia, duration.toLong() - timeFrame.toLong())
    }
}