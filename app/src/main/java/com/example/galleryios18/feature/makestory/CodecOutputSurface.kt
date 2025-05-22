package com.example.galleryios18.feature.makestory


import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.view.Surface
import java.nio.IntBuffer

class CodecOutputSurface(private val width: Int, private val height: Int) {

    private val eglDisplay: EGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
    private val eglContext: EGLContext
    private val eglSurface: EGLSurface
    private val surfaceTexture: SurfaceTexture
    val surface: Surface
    private val frameBuffer: IntBuffer = IntBuffer.allocate(width * height)
    private var frameAvailable = false

    init {
        EGL14.eglInitialize(eglDisplay, null, 0, null, 0)

        val attribList = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_NONE
        )

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.size, numConfigs, 0)
        val attrib_list = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
        eglContext =
            EGL14.eglCreateContext(eglDisplay, configs[0], EGL14.EGL_NO_CONTEXT, attrib_list, 0)

        val surfaceAttribs =
            intArrayOf(EGL14.EGL_WIDTH, width, EGL14.EGL_HEIGHT, height, EGL14.EGL_NONE)
        eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, configs[0], surfaceAttribs, 0)
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        surfaceTexture = SurfaceTexture(textures[0])
        surface = Surface(surfaceTexture)

        surfaceTexture.setOnFrameAvailableListener {
            frameAvailable = true
        }
    }

    fun awaitNewImage() {
        while (!frameAvailable) {
            Thread.sleep(5)
        }
        frameAvailable = false
        surfaceTexture.updateTexImage()
    }

    fun drawImage() {
        // Ở đây bạn có thể thêm shader xử lý nếu cần
    }

    fun getFrame(): Bitmap {
        val buffer = IntArray(width * height)
        GLES20.glReadPixels(
            0,
            0,
            width,
            height,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            frameBuffer
        )
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        frameBuffer.rewind()
        frameBuffer.get(buffer)
        bitmap.setPixels(buffer, 0, width, 0, 0, width, height)
        return bitmap
    }

    fun release() {
        surface.release()
        surfaceTexture.release()
        EGL14.eglDestroySurface(eglDisplay, eglSurface)
        EGL14.eglDestroyContext(eglDisplay, eglContext)
        EGL14.eglTerminate(eglDisplay)
    }
}
