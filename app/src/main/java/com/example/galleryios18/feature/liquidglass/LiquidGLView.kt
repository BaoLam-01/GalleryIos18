package com.example.galleryios18.feature.liquidglass

import android.content.Context
import android.opengl.GLSurfaceView

class LiquidGLView(context: Context) : GLSurfaceView(context) {
    private val renderer: LiquidRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = LiquidRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun updateTouch(x: Float, y: Float) {
        renderer.setTouchPosition(x, y)
    }
}
