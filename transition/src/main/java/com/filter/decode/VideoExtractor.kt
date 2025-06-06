package com.filter.transition.decode

import android.media.MediaFormat
import com.filter.decode.base.MMExtractor
import com.filter.transition.decode.listener.IExtractor
import java.nio.ByteBuffer


/**
 * 视频数据提取器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-09-03 11:06
 *
 */
class VideoExtractor(path: String): IExtractor {

    private val mMediaExtractor = MMExtractor(path)

    override fun getFormat(): MediaFormat? {
        return mMediaExtractor.getVideoFormat()
    }

    override fun readBuffer(byteBuffer: ByteBuffer): Int {
        return mMediaExtractor.readBuffer(byteBuffer)
    }

    override fun getCurrentTimestamp(): Long {
        return mMediaExtractor.getCurrentTimestamp()
    }

    override fun getSampleFlag(): Int {
        return mMediaExtractor.getSampleFlag()
    }

    override fun seek(pos: Long): Long {
        return mMediaExtractor.seek(pos)
    }

    override fun setStartPos(pos: Long) {
        return mMediaExtractor.setStartPos(pos)
    }

    fun advance(){
        mMediaExtractor.advance()
    }

    override fun stop() {
        mMediaExtractor.stop()
    }
}