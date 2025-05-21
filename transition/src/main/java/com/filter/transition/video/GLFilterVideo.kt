package com.filter.transition.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import com.filter.transition.base.BaseGLFilterVideo
import java.nio.FloatBuffer

open class GLFilterVideo(fragmentShader: String?, context: Context) : BaseGLFilterVideo(fragmentShader, context) {
    protected var mTextureId: Int = -1

    protected var mTextureHandlerVideo: Int = -1

    override fun getSurfaceTexture(cb: (st: SurfaceTexture) -> Unit) {
    }
    init {
        isTransition = false
    }
    open fun draw(mVertexBuffer: FloatBuffer, mTextureBuffer: FloatBuffer, currentId: Int) {
        mTextureId = currentId
        if (mTextureId != -1) {
            initDefMatrix()

            createGLPrg()
            // 【激活灵魂纹理单元】
            activateTextureVideo()

            doDraw(mVertexBuffer, mTextureBuffer)
        }
    }

    override fun loadShader(type: Int, shaderCode: String): Int {
        //根据type创建顶点着色器或者片元着色器
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        return shader
    }

    override fun createGLPrgMore() {
        mTextureHandlerVideo = GLES20.glGetUniformLocation(mProgram, "inputImageTexture")
    }

    override fun drawMore() {
    }

    protected open fun activateTextureVideo() {
        if (mTextureHandlerVideo != -1) {
            activateTexture(GLES20.GL_TEXTURE_2D,
                mTextureId,
                0,
                mTextureHandlerVideo)
        }
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler)
        GLES20.glDisableVertexAttribArray(mTexturePosHandler)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDeleteTextures(1, intArrayOf(mTextureId), 0)
        GLES20.glDeleteProgram(mProgram)
    }
}