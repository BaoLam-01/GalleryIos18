package com.example.galleryios18.feature.makestory

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log

class MediaEncoder(
    private val outputPath: String,
    private val width: Int,
    private val height: Int,
    private val frameRate: Int = 30
) {
    private val mimeType = "video/avc"
    private var trackIndex = -1
    private var muxerStarted = false
    private var isEncoderStarted = false

    fun encode(bitmaps: List<Bitmap>) {
        val encoder = MediaCodec.createEncoderByType(mimeType)
        val format = MediaFormat.createVideoFormat(mimeType, width, height).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_BIT_RATE, 4_000_000)
            setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        }
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        val inputSurface = encoder.createInputSurface()
        val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val eglHelper = EglHelper()
        eglHelper.init(inputSurface, width, height)

        encoder.start()
        isEncoderStarted = true

        val bufferInfo = MediaCodec.BufferInfo()
        val frameDurationUs = 1_000_000L / frameRate
        var presentationTimeUs = 0L

        // Chỉ draw liên tiếp các bitmap
        for (bitmap in bitmaps) {
            eglHelper.drawBitmap(bitmap)
            eglHelper.setPresentationTime(presentationTimeUs * 1000)
            eglHelper.swapBuffers()
            presentationTimeUs += frameDurationUs
        }

        // Kết thúc stream đầu vào
        if (isEncoderStarted) {
            try {
                encoder.signalEndOfInputStream()
            } catch (e: IllegalStateException) {
                Log.e("lampro", "Encoder already released before signalEndOfInputStream(): ${e.message}")
            }
        }

        // Sau khi kết thúc vẽ -> drain một lần
        drainEncoder(encoder, bufferInfo, muxer, endOfStream = true)

        try {
            encoder.stop()
            muxer.stop()
        } catch (e: Exception) {
            Log.e("MediaEncoder", "Stop error: ${e.message}")
        }

        encoder.release()
        muxer.release()
        eglHelper.release()
    }


    private fun drainEncoder(
        encoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        muxer: MediaMuxer,
        endOfStream: Boolean = false
    ) {
        while (true) {
            val outputBufferId = try {
                encoder.dequeueOutputBuffer(bufferInfo, 10000)
            } catch (e: IllegalStateException) {
                Log.e("MediaEncoder", "Encoder is in illegal state: ${e.message}")
                break
            }

            when {
                outputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    if (!endOfStream) break
                }
                outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (muxerStarted) throw RuntimeException("Format changed twice")
                    val newFormat = encoder.outputFormat
                    trackIndex = muxer.addTrack(newFormat)
                    muxer.start()
                    muxerStarted = true
                }
                outputBufferId >= 0 -> {
                    val encodedData = encoder.getOutputBuffer(outputBufferId)
                        ?: throw RuntimeException("Encoder output buffer was null")

                    if (bufferInfo.size > 0 && muxerStarted) {
                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        muxer.writeSampleData(trackIndex, encodedData, bufferInfo)
                    }

                    encoder.releaseOutputBuffer(outputBufferId, false)

                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) break
                }
            }
        }
    }

}
