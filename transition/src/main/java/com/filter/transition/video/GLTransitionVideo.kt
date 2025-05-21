package com.filter.transition.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.filter.transition.base.BaseGLFilterVideo
import com.filter.utils.Constant
import java.nio.FloatBuffer

class GLTransitionVideo(fragmentShader: String?, context: Context) :
    BaseGLFilterVideo(fragmentShader, context) {

    private var mTextureId1: Int = -1
    private var mTextureId2: Int = -1

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurfaceTexture2: SurfaceTexture? = null

    private var mSftCb: ((SurfaceTexture) -> Unit)? = null
    private var mSftCb2: ((SurfaceTexture) -> Unit)? = null

    private var mTextureHandler1: Int = -1
    private var mTextureHandler2: Int = -1

    private var typeTransitionHandler: Int = -1

    private var typeTransition: Int = 1


    private fun setTextureID(id: Int, id1: Int) {
        mTextureId1 = id
        mTextureId2 = id1
        if (mSurfaceTexture == null) {
            if (typeTransition == Constant.VIDEO_TO_VIDEO || typeTransition == Constant.VIDEO_TO_IMAGE) {
                mSurfaceTexture = SurfaceTexture(id)
                mSftCb?.invoke(mSurfaceTexture!!)
            }
        }
        if (mSurfaceTexture2 == null) {
            if (typeTransition == Constant.IMAGE_TO_VIDEO || typeTransition == Constant.VIDEO_TO_VIDEO) {
                mSurfaceTexture2 = SurfaceTexture(id1)
                mSftCb2?.invoke(mSurfaceTexture2!!)
            }
        }
    }

    override fun getSurfaceTexture(cb: (st: SurfaceTexture) -> Unit) {
        mSftCb = cb
        mSftCb?.invoke(mSurfaceTexture!!)
    }

    fun getSurfaceTexture2(cb: (st: SurfaceTexture) -> Unit) {
            mSftCb2 = cb
            mSftCb2?.invoke(mSurfaceTexture2!!)
    }

    fun setTypeTransition(type: Int) {
        if (this.typeTransition != type) {
            mProgram = -1
            this.typeTransition = type
        }
    }

    fun draw(
        mVertexBuffer: FloatBuffer,
        mTextureBuffer: FloatBuffer,
        currentTexture: Int,
        nextTexture: Int,
    ) {
        setTextureID(currentTexture, nextTexture)
        if (mTextureId1 != -1) {
            initDefMatrix()
            //【步骤2: 创建、编译并启动OpenGL着色器】
            createGLPrg()
            // 【激活灵魂纹理单元】
            activateTexture1()

            activateTexture2()

            //【步骤4: 绑定图片到纹理单元】
            updateTexture()
            //【步骤5: 开始渲染绘制】
            doDraw(mVertexBuffer, mTextureBuffer)
        }
    }

    private fun activateTexture1() {
        if (mTextureId1 != -1) {
            var type = GLES20.GL_TEXTURE_2D
            if (typeTransition == Constant.VIDEO_TO_VIDEO || typeTransition == Constant.VIDEO_TO_IMAGE) {
                type = GLES11Ext.GL_TEXTURE_EXTERNAL_OES
            }
            activateTexture(type,
                mTextureId1,
                0,
                mTextureHandler1)
        }
    }

    private fun activateTexture2() {
        if (mTextureId2 != -1) {
            var type = GLES20.GL_TEXTURE_2D
            if (typeTransition == Constant.VIDEO_TO_VIDEO || typeTransition == Constant.IMAGE_TO_VIDEO) {
                type = GLES11Ext.GL_TEXTURE_EXTERNAL_OES
            }
            activateTexture(type,
                mTextureId2,
                1,
                mTextureHandler2)

        }
    }

    private fun updateTexture() {
        if (typeTransition == Constant.VIDEO_TO_VIDEO || typeTransition == Constant.VIDEO_TO_IMAGE) {
            mSurfaceTexture?.updateTexImage()
        }
    }


    override fun release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler)
        GLES20.glDisableVertexAttribArray(mTexturePosHandler)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDeleteTextures(1, intArrayOf(mTextureId1), 0)
        GLES20.glDeleteTextures(1, intArrayOf(mTextureId2), 0)
        GLES20.glDeleteProgram(mProgram)
    }

    override fun loadShader(type: Int, shaderCode: String): Int {
        var strShader = shaderCode
        when (typeTransition) {
            Constant.VIDEO_TO_VIDEO -> strShader = setTypeShaderVideoToVideo(shaderCode)
            Constant.VIDEO_TO_IMAGE -> strShader = setTypeShaderVideoToImage(shaderCode)
            Constant.IMAGE_TO_VIDEO -> strShader = setTypeShaderImageToVideo(shaderCode)
        }

        //根据type创建顶点着色器或者片元着色器
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, strShader)
        GLES20.glCompileShader(shader)

        return shader
    }

    override fun drawMore() {
    }

    override fun createGLPrgMore() {
        mTextureHandler1 = GLES20.glGetUniformLocation(mProgram, "inputImageTexture")
        mTextureHandler2 = GLES20.glGetUniformLocation(mProgram, "inputImageTexture2")
    }

}