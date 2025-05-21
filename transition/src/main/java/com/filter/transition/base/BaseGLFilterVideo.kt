package com.filter.transition.base

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.filter.base.GPUImageFilter
import com.filter.base.IDrawer
import java.nio.FloatBuffer

abstract class BaseGLFilterVideo(var fragmentShader: String?, val context: Context) : IDrawer {
    private val TAG = "HaiPd"
    private var mWorldWidth: Int = -1
    private var mWorldHeight: Int = -1
    private var mVideoWidth: Int = -1
    private var mVideoHeight: Int = -1

    //OpenGL程序ID
    var mProgram: Int = -1
    protected var loadByAsset=true

    //矩阵变换接收者
    protected var mVertexMatrixHandler: Int = -1

    // 顶点坐标接收者
    protected var mVertexPosHandler: Int = -1

    // 纹理坐标接收者
    protected var mTexturePosHandler: Int = -1

    // 灵魂缩放进度接收者
    protected var mProgressHandler: Int = -1
    protected var process: Float = 0f

    protected var mMatrix: FloatArray? = null

    protected var mAlphaHandler: Int = -1

    protected var mAlpha = 1f

    protected var mWidthRatio = 1f
    protected var mHeightRatio = 1f


    protected fun getVertexShader(): String {
        return "attribute vec4 aPosition;" +
                "precision mediump float;" +
                "uniform mat4 uMatrix;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 textureCoordinate;" +
                "attribute float alpha;" +
                "varying float inAlpha;" +
                "void main() {" +
                "    gl_Position = uMatrix*aPosition;" +
                "    textureCoordinate = aCoordinate;" +
                "    inAlpha = alpha;" +
                "}"
    }

    abstract fun loadShader(type: Int, shaderCode: String): Int

    abstract fun createGLPrgMore()

    abstract fun drawMore()


    open fun createGLPrg() {
        if (mProgram == -1) {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader())
            val fragmentShader: Int = if(loadByAsset) {
                loadShader(GLES20.GL_FRAGMENT_SHADER, loadShaderFromAssets(context, this.fragmentShader))
            }else{
                loadShader(GLES20.GL_FRAGMENT_SHADER, this.fragmentShader!!)
            }

            //创建OpenGL ES程序，注意：需要在OpenGL渲染线程中创建，否则无法渲染
            mProgram = GLES20.glCreateProgram()
            //将顶点着色器加入到程序
            GLES20.glAttachShader(mProgram, vertexShader)
            //将片元着色器加入到程序中

            GLES20.glAttachShader(mProgram, fragmentShader)
            //连接到着色器程序
            GLES20.glLinkProgram(mProgram)

            mVertexMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMatrix")
            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")

            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate")
            mAlphaHandler = GLES20.glGetAttribLocation(mProgram, "alpha")

            mProgressHandler = GLES20.glGetUniformLocation(mProgram, "progress")

            createGLPrgMore()

        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram)
    }

    protected var isTransition = true
    protected fun initDefMatrix() {
        if (mMatrix != null) return
        if (mVideoWidth != -1 && mVideoHeight != -1 &&
            mWorldWidth != -1 && mWorldHeight != -1
        ) {
            mMatrix = FloatArray(16)
            val prjMatrix = FloatArray(16)
            val originRatio = mVideoWidth / mVideoHeight.toFloat()
            val worldRatio = mWorldWidth / mWorldHeight.toFloat()

            if (isTransition) {
                if (mWorldWidth > mWorldHeight) {
                    if (originRatio > worldRatio) {
                        mHeightRatio = originRatio / worldRatio
                        Matrix.orthoM(
                            prjMatrix, 0,
                            -mWidthRatio, mWidthRatio,
                            -mHeightRatio, mHeightRatio,
                            3f, 5f
                        )
                    } else {// 原始比例小于窗口比例，缩放高度度会导致高度超出，因此，高度以窗口为准，缩放宽度
                        mWidthRatio = worldRatio / originRatio
                        Matrix.orthoM(
                            prjMatrix, 0,
                            -mWidthRatio, mWidthRatio,
                            -mHeightRatio, mHeightRatio,
                            3f, 5f
                        )
                    }
                } else {
                    if (originRatio > worldRatio) {
                        mHeightRatio = originRatio / worldRatio
                        Matrix.orthoM(
                            prjMatrix, 0,
                            -mWidthRatio, mWidthRatio,
                            -mHeightRatio, mHeightRatio,
                            3f, 5f
                        )
                    } else {// 原始比例小于窗口比例，缩放高度会导致高度超出，因此，高度以窗口为准，缩放宽度
                        mWidthRatio = worldRatio / originRatio
                        Matrix.orthoM(
                            prjMatrix, 0,
                            -mWidthRatio, mWidthRatio,
                            -mHeightRatio, mHeightRatio,
                            3f, 5f
                        )
                    }
                }
            } else {
                Matrix.orthoM(
                    prjMatrix, 0,
                    -1f, 1f,
                    -1f, 1f,
                    3f, 5f
                )
            }

            //设置相机位置
            val viewMatrix = FloatArray(16)
            Matrix.setLookAtM(
                viewMatrix, 0,
                0f, 0f, 5.0f,
                0f, 0f, 0f,
                0f, 1.0f, 0f
            )
            //计算变换矩阵
            Matrix.multiplyMM(mMatrix, 0, prjMatrix, 0, viewMatrix, 0)
        }
    }

    override fun setVideoSize(videoW: Int, videoH: Int) {
        mVideoWidth = videoW
        mVideoHeight = videoH
//        if(isTransition) {
//            mMatrix = null
//        }
    }

    override fun setWorldSize(worldW: Int, worldH: Int) {
        mWorldWidth = worldW
        mWorldHeight = worldH
    }

    fun setShaderFragment(shader: String) {
        if (fragmentShader != shader) {
            fragmentShader = shader
            mProgram = -1
        }
    }

    fun loadShaderFromAssets(context: Context, file: String?): String {
        try {
            val assetManager = context.assets
            val ims = assetManager.open(file!!)
            val re = GPUImageFilter.convertStreamToString(ims)
            ims.close()
            return re
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun activateTexture(type: Int, textureId: Int, index: Int, textureHandler: Int) {
        //激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index)
        //绑定纹理ID到纹理单元
        GLES20.glBindTexture(type, textureId)
        //将激活的纹理单元传递到着色器里面
        GLES20.glUniform1i(textureHandler, index)
        //配置边缘过渡参数
        GLES20.glTexParameterf(type, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(type, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    }


    open fun setProgress(process: Float) {
        this.process = process
    }

    override fun release() {
    }


    fun translate(dx: Float, dy: Float) {
        Matrix.translateM(mMatrix, 0, dx * mWidthRatio * 2, -dy * mHeightRatio * 2, 0f)
    }

    fun scale(sx: Float, sy: Float) {
        Matrix.scaleM(mMatrix, 0, sx, sy, 1f)
        mWidthRatio /= sx
        mHeightRatio /= sy
    }

    fun doDraw(mVertexBuffer: FloatBuffer, mTextureBuffer: FloatBuffer) {
        //启用顶点的句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler)
        GLES20.glEnableVertexAttribArray(mTexturePosHandler)
        GLES20.glUniformMatrix4fv(mVertexMatrixHandler, 1, false, mMatrix, 0)


        GLES20.glUniform1f(mProgressHandler, process)

        drawMore()

////        //设置着色器参数， 第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glVertexAttribPointer(
            mTexturePosHandler,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mTextureBuffer
        )
        GLES20.glVertexAttrib1f(mAlphaHandler, mAlpha)
////        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }


    protected fun setTypeShaderVideoToVideo(shaderCode: String): String {
        var strShader = shaderCode.replace(
            "uniform sampler2D inputImageTexture;",
            "uniform samplerExternalOES inputImageTexture;")

        strShader = strShader.replace(
            "uniform sampler2D inputImageTexture2;",
            "uniform samplerExternalOES inputImageTexture2;")
        return strShader
    }

    protected fun setTypeShaderVideoToImage(shaderCode: String): String {
        return shaderCode.replace(
            "uniform sampler2D inputImageTexture;",
            "uniform samplerExternalOES inputImageTexture;")
    }

    protected fun setTypeShaderImageToVideo(shaderCode: String): String {
        return shaderCode.replace(
            "uniform sampler2D inputImageTexture2;",
            "uniform samplerExternalOES inputImageTexture2;")
    }


}