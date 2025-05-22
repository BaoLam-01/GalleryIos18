package com.example.galleryios18.feature.makestory

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class TextureRenderer(context: Context) {
    private val program: GLProgram

    init {
        val vertexShader = loadShaderFromAssets(context, "vertex_shader.glsl")
        val fragmentShader = loadShaderFromAssets(context, "fade_fragment_shader.glsl")
        program = GLProgram(vertexShader, fragmentShader)
    }

    private fun loadTexture(bitmap: Bitmap): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        return textureIds[0]
    }

    fun draw(bitmap1: Bitmap, bitmap2: Bitmap, alpha: Float) {
        val texture1 = loadTexture(bitmap1)
        val texture2 = loadTexture(bitmap2)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        program.use()

        // setup vị trí vertices và texture coordinates
        val vertexData = floatArrayOf(
            -1f, -1f, 0f, 1f,
            1f, -1f, 1f, 1f,
            -1f, 1f, 0f, 0f,
            1f, 1f, 1f, 0f
        )
        val buffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(vertexData).position(0)

        val posLoc = GLES20.glGetAttribLocation(program.programId, "a_Position")
        val texLoc = GLES20.glGetAttribLocation(program.programId, "a_TexCoord")

        buffer.position(0)
        GLES20.glVertexAttribPointer(posLoc, 2, GLES20.GL_FLOAT, false, 4 * 4, buffer)
        GLES20.glEnableVertexAttribArray(posLoc)

        buffer.position(2)
        GLES20.glVertexAttribPointer(texLoc, 2, GLES20.GL_FLOAT, false, 4 * 4, buffer)
        GLES20.glEnableVertexAttribArray(texLoc)

        // Gắn 2 texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture1)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program.programId, "u_Texture1"), 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2)
        GLES20.glUniform1i(GLES20.glGetUniformLocation(program.programId, "u_Texture2"), 1)

        // Gửi alpha
        GLES20.glUniform1f(GLES20.glGetUniformLocation(program.programId, "u_Alpha"), alpha)

        // Vẽ
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // Dọn dẹp
        GLES20.glDisableVertexAttribArray(posLoc)
        GLES20.glDisableVertexAttribArray(texLoc)

        GLES20.glDeleteTextures(2, intArrayOf(texture1, texture2), 0)
    }

    fun drawToBitmap(
        bitmap1: Bitmap,
        bitmap2: Bitmap,
        alpha: Float,
        width: Int,
        height: Int
    ): Bitmap {
        val frameBuffer = IntArray(1)
        val renderBuffer = IntArray(1)
        val texture = IntArray(1)

        // Tạo texture rỗng để gắn với FBO
        GLES20.glGenTextures(1, texture, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width,
            height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null
        )

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        // Tạo framebuffer
        GLES20.glGenFramebuffers(1, frameBuffer, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0])
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            texture[0],
            0
        )

        // Vẽ cảnh vào FBO
        GLES20.glViewport(0, 0, width, height)
        draw(bitmap1, bitmap2, alpha)

        // Đọc pixels
        val buffer = IntArray(width * height)
        val intBuffer = IntBuffer.wrap(buffer)
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer)

        // Chuyển sang Bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(buffer, 0, width, 0, 0, width, height)

        // Giải phóng
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glDeleteFramebuffers(1, frameBuffer, 0)
        GLES20.glDeleteTextures(1, texture, 0)

        return bitmap
    }

    private fun loadShaderFromAssets(context: Context, filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }
}
