package com.example.galleryios18.feature.makestory

import android.opengl.GLES20

class GLProgram(vertexSource: String, fragmentSource: String) {
    val programId: Int

    init {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)
        GLES20.glLinkProgram(programId)
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    private fun loadShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }
}