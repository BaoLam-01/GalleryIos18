package com.filter.transition.decode.listener

import com.filter.decode.base.BaseDecoder
import com.filter.decode.base.Frame


/**
 * 解码状态回调接口
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-09-02 09:56
 *
 */
interface IDecoderStateListener {
    fun decoderPrepare(decodeJob: BaseDecoder?)
    fun decoderReady(decodeJob: BaseDecoder?)
    fun decoderRunning(decodeJob: BaseDecoder?)
    fun decoderPause(decodeJob: BaseDecoder?)
    fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame)
    fun decoderFinish(decodeJob: BaseDecoder?)
    fun decoderDestroy(decodeJob: BaseDecoder?)
    fun decoderError(decodeJob: BaseDecoder?, msg: String)
}