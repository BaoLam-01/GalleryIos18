package com.filter.decode

import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.filter.transition.decode.listener.IExtractor
import com.filter.decode.base.BaseDecoder
import com.filter.transition.decode.VideoExtractor
import java.nio.ByteBuffer


/**
 * 视频解码器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-09-03 10:52
 *
 */
class VideoDecoder(path: String, sfv: SurfaceView?, surface: Surface?): BaseDecoder(path) {
    private val TAG = "VideoDecoder"
    
    private val mSurfaceView = sfv
    private var mSurface = surface
    
    override fun check(): Boolean {
        if (mSurfaceView == null && mSurface == null) {
            mStateListener?.decoderError(this, "显示器为空")
            return false
        }
        return true
    }

    override fun initExtractor(path: String): IExtractor {
        return VideoExtractor(path)
    }

    override fun initSpecParams(format: MediaFormat) {
    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        if (mSurface != null) {
            codec.configure(format, mSurface , null, 0)
            notifyDecode()
        } else if (mSurfaceView?.holder?.surface != null) {
            mSurface = mSurfaceView.holder?.surface
            configCodec(codec, format)
        } else {
            mSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback2 {
                override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    mSurface = holder.surface
                    configCodec(codec, format)
                }
            })

            return false
        }
        return true
    }

    override fun initRender(): Boolean {
        return true
    }

    override fun render(outputBuffer: ByteBuffer,
                        bufferInfo: MediaCodec.BufferInfo) {
    }

    override fun doneDecode() {
    }
}