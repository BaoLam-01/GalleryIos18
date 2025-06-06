package com.filter.decode.base

import android.media.MediaCodec
import android.media.MediaFormat
import com.filter.transition.decode.listener.IDecoderProgress
import com.filter.transition.decode.listener.IDecoderStateListener
import com.filter.transition.decode.listener.IExtractor
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * 解码器基类
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-09-02 09:43
 *
 */
abstract class BaseDecoder(private val mFilePath: String) : IDecoder {

    private val TAG = "HaiPd"

    //-------------线程相关------------------------
    /**
     * 解码器是否在运行
     */
    private var mIsRunning = true

    /**
     * 线程等待锁
     */
    private val mLock = Object()

    /**
     * 是否可以进入解码
     */
    private var mReadyForDecode = false

    //---------------状态相关-----------------------
    /**
     * 音视频解码器
     */
    private var mCodec: MediaCodec? = null

    /**
     * 音视频数据读取器
     */
    private var mExtractor: IExtractor? = null

    /**
     * 解码输入缓存区
     */
    private var mInputBuffers: Array<ByteBuffer>? = null

    /**
     * 解码输出缓存区
     */
    private var mOutputBuffers: Array<ByteBuffer>? = null

    /**
     * 解码数据信息
     */
    private var mBufferInfo = MediaCodec.BufferInfo()

    private var mState = DecodeState.STOP

    protected var mStateListener: IDecoderStateListener? = null

    /**
     * 流数据是否结束
     */
    private var mIsEOS = false

    protected var mVideoWidth = 0

    protected var mVideoHeight = 0

    private var mDuration: Long = 0

    private var mStartPos: Long = 0

    private var mEndPos: Long = 0

    /**
     * 开始解码时间，用于音视频同步
     */
    private var mStartTimeForSync = -1L

    // 是否需要音视频渲染同步
    private var mSyncRender = true

    final override fun run() {
        if (mState == DecodeState.STOP) {
            mState = DecodeState.START
        }
        mStateListener?.decoderPrepare(this)

        //【解码步骤：1. 初始化，并启动解码器】
        if (!init()) return

        try {
            while (mIsRunning) {
                if (mState != DecodeState.START &&
                    mState != DecodeState.DECODING &&
                    mState != DecodeState.SEEKING
                ) {

                    waitDecode()

                    // ---------【同步时间矫正】-------------
                    //恢复同步的起始时间，即去除等待流失的时间
                    mStartTimeForSync = System.currentTimeMillis() - getCurTimeStamp()
                }
                if (!mIsRunning ||
                    mState == DecodeState.STOP
                ) {
                    mIsRunning = false
                    break
                }

                if (mStartTimeForSync == -1L) {
                    mStartTimeForSync = System.currentTimeMillis()
                }

                //如果数据没有解码完毕，将数据推入解码器解码
                if (!mIsEOS) {
                    //【解码步骤：2. 见数据压入解码器输入缓冲】
                    mIsEOS = pushBufferToDecoder()
                }

                //【解码步骤：3. 将解码好的数据从缓冲区拉取出来】
                val index = pullBufferFromDecoder()
                if (index >= 0) {

                    // ---------【音视频同步】-------------
                    if (mSyncRender && mState == DecodeState.DECODING) {
                        sleepRender()
                    }
                    //【解码步骤：4. 渲染】
                    if (mSyncRender) {// 如果只是用于编码合成新视频，无需渲染
                        render(mOutputBuffers!![index], mBufferInfo)
                    }

                    //将解码数据传递出去
                    val frame = Frame()
                    frame.textureName = index
                    frame.buffer = mOutputBuffers!![index]
                    frame.setBufferInfo(mBufferInfo)
                    mStateListener?.decodeOneFrame(this, frame)

                    //【解码步骤：5. 释放输出缓冲】
                    mCodec!!.releaseOutputBuffer(index, true)

                    if (mState == DecodeState.START) {
                        mState = DecodeState.PAUSE
                    }
                }
                //【解码步骤：6. 判断解码是否完成】
                if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    mState = DecodeState.FINISH
                    mStateListener?.decoderFinish(this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            doneDecode()
            release()
        }
    }

    private fun init(): Boolean {
        if (mFilePath.isEmpty() || !File(mFilePath).exists()) {
            mStateListener?.decoderError(this, "文件路径为空")
            return false
        }

        if (!check()) return false

        //初始化数据提取器
        mExtractor = initExtractor(mFilePath)
        if (mExtractor == null ||
            mExtractor!!.getFormat() == null
        ) {
            return false
        }

        //初始化参数
        if (!initParams()) return false

        //初始化渲染器
        if (!initRender()) return false

        //初始化解码器
        if (!initCodec()) return false
        return true
    }

    private fun initParams(): Boolean {
        try {
            val format = mExtractor!!.getFormat()!!
            mDuration = format.getLong(MediaFormat.KEY_DURATION)
            if (mEndPos == 0L) mEndPos = mDuration

            initSpecParams(mExtractor!!.getFormat()!!)

        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun initCodec(): Boolean {
        try {
            val type = mExtractor!!.getFormat()!!.getString(MediaFormat.KEY_MIME)
            mCodec = MediaCodec.createDecoderByType(type!!)
            if (!configCodec(mCodec!!, mExtractor!!.getFormat()!!)) {
                waitDecode()
            }
            mCodec!!.start()

            mInputBuffers = mCodec?.inputBuffers
            mOutputBuffers = mCodec?.outputBuffers

        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun pushBufferToDecoder(): Boolean {
        val inputBufferIndex = mCodec!!.dequeueInputBuffer(10000)
        var isEndOfStream = false

        if (inputBufferIndex >= 0) {
            val inputBuffer = mInputBuffers!![inputBufferIndex]
            val sampleSize = mExtractor!!.readBuffer(inputBuffer)

            if (sampleSize < 0) {
                //如果数据已经取完，压入数据结束标志：MediaCodec.BUFFER_FLAG_END_OF_STREAM
                mCodec!!.queueInputBuffer(
                    inputBufferIndex, 0, 0,
                    0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
                isEndOfStream = true
            } else {
                mCodec!!.queueInputBuffer(
                    inputBufferIndex, 0,
                    sampleSize, mExtractor!!.getCurrentTimestamp(), 0
                )
            }

        }
        return isEndOfStream
    }

    private fun pullBufferFromDecoder(): Int {
        /* 查询是否有解码完成的数据，index >=0 时，表示数据有效，并且index为缓冲区索引 */
        val index = mCodec!!.dequeueOutputBuffer(mBufferInfo, 10000)

        when (index) {
            MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {}
            MediaCodec.INFO_TRY_AGAIN_LATER -> {}
            MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                mOutputBuffers = mCodec!!.outputBuffers
            }
            else -> {
                return index
            }
        }
        return -1
    }

    private fun sleepRender() {
        val passTime = System.currentTimeMillis() - mStartTimeForSync
        val curTime = getCurTimeStamp()
        if (curTime > passTime) {
            Thread.sleep(curTime - passTime)
        }
    }

    fun release() {
        try {
            mState = DecodeState.STOP
            mIsEOS = false
            mExtractor?.stop()
            mCodec?.stop()
            mCodec?.release()
            mStateListener?.decoderDestroy(this)
        } catch (_: Exception) {
        }
    }

    /**
     * 解码线程进入等待
     */
    private fun waitDecode() {
        try {
            if (mState == DecodeState.PAUSE) {
                mStateListener?.decoderPause(this)
            }
            synchronized(mLock) {
                mLock.wait()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 通知解码线程继续运行
     */
    protected fun notifyDecode() {
        synchronized(mLock) {
            mLock.notifyAll()
        }
        if (mState == DecodeState.DECODING) {
            mStateListener?.decoderRunning(this)
        }
    }

    override fun pause() {
        mState = DecodeState.PAUSE
    }

    override fun goOn() {
        mState = DecodeState.DECODING
        notifyDecode()
    }

    fun restart(){
        mStartTimeForSync = -1
        mState = DecodeState.DECODING
        notifyDecode()
    }



    private var executorService: ExecutorService? = null
    open fun getExecutorService(): ExecutorService? {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor()
        }
        return executorService
    }
    override fun seekTo(pos: Long): Long {
        getExecutorService()!!.execute {
            if (pos > mExtractor!!.getCurrentTimestamp() && pos < mDuration) {
                decodeSeekTo(pos)
            } else {
//                seekBackward(pos)
            }
        }
        return /*mExtractor?.seek(pos*1000)!!*/0
    }

    private fun seekBackward(pos: Long) {
        mExtractor!!.seek(pos)
        mState = DecodeState.DECODING
        try {
            while (mExtractor!!.getCurrentTimestamp() > pos && pos > 0) {
                if (!mIsEOS) {
                    mIsEOS = pushBufferToDecoder()
                }
                if(mExtractor!!.getCurrentTimestamp()==0L) {
                    decodeSeekTo(pos)
                    break
                }
                val index = pullBufferFromDecoder()
                if (index >= 0) {
                    if (kotlin.math.abs(pos - mExtractor!!.getCurrentTimestamp()) > 10000) {
                        mCodec!!.releaseOutputBuffer(index, true)
                    } else {
                        mCodec!!.releaseOutputBuffer(index, false)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    private fun decodeSeekTo(pos: Long) {
//        mExtractor!!.seek(pos)
        mState = DecodeState.DECODING
        try {
            while (mExtractor!!.getCurrentTimestamp() < pos) {
                if (!mIsEOS) {
                    mIsEOS = pushBufferToDecoder()
                }
                if(mExtractor!!.getCurrentTimestamp()==0L) {
                    decodeSeekTo(pos)
                    break
                }
                val index = pullBufferFromDecoder()
                if (index >= 0) {
                    if (kotlin.math.abs(pos - mExtractor!!.getCurrentTimestamp()) < 100000) {
                        mCodec!!.releaseOutputBuffer(index, true)
                    } else {
                        mCodec!!.releaseOutputBuffer(index, false)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }


    override fun seekAndPlay(pos: Long): Long {
        return 0
    }

    override fun stop() {
        mState = DecodeState.STOP
        mIsRunning = false
        notifyDecode()
    }

    override fun isDecoding(): Boolean {
        return mState == DecodeState.DECODING
    }

    override fun isSeeking(): Boolean {
        return mState == DecodeState.SEEKING
    }

    override fun isStop(): Boolean {
        return mState == DecodeState.STOP
    }

    override fun setSizeListener(l: IDecoderProgress) {
    }

    override fun setStateListener(l: IDecoderStateListener?) {
        mStateListener = l
    }

    override fun getWidth(): Int {
        return mVideoWidth
    }

    override fun getHeight(): Int {
        return mVideoHeight
    }

    override fun getDuration(): Long {
        return mDuration
    }

    override fun getCurTimeStamp(): Long {
        return mBufferInfo.presentationTimeUs / 1000
    }

    override fun getRotationAngle(): Int {
        return 0
    }

    override fun getMediaFormat(): MediaFormat? {
        return mExtractor?.getFormat()
    }

    override fun getTrack(): Int {
        return 0
    }

    override fun getFilePath(): String {
        return mFilePath
    }

    override fun withoutSync(): IDecoder {
        mSyncRender = false
        return this
    }

    /**
     * 检查子类参数
     */
    abstract fun check(): Boolean

    /**
     * 初始化数据提取器
     */
    abstract fun initExtractor(path: String): IExtractor

    /**
     * 初始化子类自己特有的参数
     */
    abstract fun initSpecParams(format: MediaFormat)

    /**
     * 配置解码器
     */
    abstract fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean

    /**
     * 初始化渲染器
     */
    abstract fun initRender(): Boolean

    /**
     * 渲染
     */
    abstract fun render(
        outputBuffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo
    )

    /**
     * 结束解码
     */
    abstract fun doneDecode()
}