package com.filter.transition.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import com.filter.base.OpenGlUtils
import java.nio.FloatBuffer

class GLFilterOverlayVideo(context: Context) :
    GLFilterVideo("filter_shader/filter_bitmap_on_bitmap.glsl", context) {
    private var mGLUniformTextureOverlay = 0
    private var textureOverlay:Int = -1

    init {
        isTransition = false
    }

    fun draw(mVertexBuffer: FloatBuffer, mTextureBuffer: FloatBuffer,currentId:Int,overLayId:Int) {
        mTextureId = currentId
        textureOverlay = overLayId
        if (mTextureId != -1) {
            initDefMatrix()

            createGLPrg()
            // 【激活灵魂纹理单元】
            activateTextureVideo()

            doDraw(mVertexBuffer, mTextureBuffer)
        }
    }


    override fun createGLPrgMore() {
        mGLUniformTextureOverlay = GLES20.glGetUniformLocation(mProgram, "inputImageTextureOverlay")
    }


    override fun drawMore() {
        if (textureOverlay != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureOverlay)
            GLES20.glUniform1i(mGLUniformTextureOverlay, 1)
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