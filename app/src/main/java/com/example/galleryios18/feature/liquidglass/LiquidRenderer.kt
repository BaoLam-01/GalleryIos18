package com.example.galleryios18.feature.liquidglass

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.io.bufferedReader
import kotlin.io.readText
import kotlin.io.use

class LiquidRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var programId = 0
    private var resolutionHandle = 0
    private var timeHandle = 0
    private var touchHandle = 0

    private var width = 0
    private var height = 0
    private var startTime = System.currentTimeMillis()
    private var touchPos = floatArrayOf(0f, 0f)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShaderCode = readShaderFromAssets(context, "vertexShaderCode.glsl")
        val fragmentShaderCode = readShaderFromAssets(context, "fragmentShaderCode.glsl")

        programId = ShaderUtils.createProgram(vertexShaderCode, fragmentShaderCode)
        GLES20.glUseProgram(programId)

        resolutionHandle = GLES20.glGetUniformLocation(programId, "iResolution")
        timeHandle = GLES20.glGetUniformLocation(programId, "iTime")
        touchHandle = GLES20.glGetUniformLocation(programId, "iMouse")
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        val currentTime = (System.currentTimeMillis() - startTime) / 1000f

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUniform2f(resolutionHandle, width.toFloat(), height.toFloat())
        GLES20.glUniform1f(timeHandle, currentTime)
        GLES20.glUniform2f(touchHandle, touchPos[0], touchPos[1])

        // Vẽ hình vuông full màn
        FullScreenQuad.draw(programId)
    }

    fun setTouchPosition(x: Float, y: Float) {
        touchPos[0] = x
        touchPos[1] = height - y // đảo ngược trục y
    }

    fun readShaderFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }
    }
}
