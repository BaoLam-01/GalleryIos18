package com.example.galleryios18.feature.makestory

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat

class MediaDecoder(private val context: Context, private val videoPath: String) {
    fun decodeFrames(): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val extractor = MediaExtractor()
        extractor.setDataSource(videoPath)

        var videoTrackIndex = -1
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.startsWith("video/")) {
                videoTrackIndex = i
                extractor.selectTrack(i)
                break
            }
        }
        if (videoTrackIndex == -1) return bitmaps

        val format = extractor.getTrackFormat(videoTrackIndex)
        val width = format.getInteger(MediaFormat.KEY_WIDTH)
        val height = format.getInteger(MediaFormat.KEY_HEIGHT)
        val decoder = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME)!!)
        val surfaceTexture = CodecOutputSurface(width, height) // Custom SurfaceTexture -> bitmap
        decoder.configure(format, surfaceTexture.surface, null, 0)
        decoder.start()

        val bufferInfo = MediaCodec.BufferInfo()
        var isEOS = false
        val timeoutUs = 10000L

        while (true) {
            if (!isEOS) {
                val inputIndex = decoder.dequeueInputBuffer(timeoutUs)
                if (inputIndex >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputIndex)!!
                    val sampleSize = extractor.readSampleData(inputBuffer, 0)
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(
                            inputIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        isEOS = true
                    } else {
                        val presentationTime = extractor.sampleTime
                        decoder.queueInputBuffer(inputIndex, 0, sampleSize, presentationTime, 0)
                        extractor.advance()
                    }
                }
            }

            val outputIndex = decoder.dequeueOutputBuffer(bufferInfo, timeoutUs)
            if (outputIndex >= 0) {
                decoder.releaseOutputBuffer(outputIndex, true)
                if (bufferInfo.size != 0) {
                    surfaceTexture.awaitNewImage()
                    surfaceTexture.drawImage()
                    val bitmap = surfaceTexture.getFrame()
                    bitmaps.add(bitmap.copy(Bitmap.Config.ARGB_8888, false))
                }

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
            }
        }

        decoder.stop()
        decoder.release()
        extractor.release()
        surfaceTexture.release()

        return bitmaps
    }
}
