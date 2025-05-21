package com.example.galleryios18.utils.createvideo;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FrameBuffer {
    private static final float[] TEXTURE_NO_ROTATION = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };

    private static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private FloatBuffer glCubeBuffer;
    private FloatBuffer glTextureBuffer;
    private int width = 0;
    private int height = 0;
    private int frameBufferName = 0;
    private int renderBufferName = 0;
    private int texName = 0;
    private final RenderFrame renderFrame = new RenderFrame();

    public void setup(int width, int height) {
        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        glCubeBuffer.clear();
        glCubeBuffer.put(CUBE).position(0);
        glTextureBuffer.clear();
        glTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid width and height!");
        }
        this.width = width;
        this.height = height;

        int[] args = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, args, 0);
        if (width > args[0] || height > args[0]) {
            throw new IllegalArgumentException("Width or height is higher than GL_MAX_RENDER_BUFFER");
        }

        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, args, 0);
        int savedFrameBuffer = args[0];

        GLES20.glGetIntegerv(GLES20.GL_RENDERBUFFER_BINDING, args, 0);
        int savedRenderBuffer = args[0];

        GLES20.glGetIntegerv(GLES20.GL_TEXTURE_BINDING_2D, args, 0);
        int savedTexName = args[0];

        release();

        try {

            GLES20.glGenFramebuffers(args.length, args, 0);
            frameBufferName = args[0];

            GLES20.glGenRenderbuffers(args.length, args, 0);
            renderBufferName = args[0];

            GLES20.glGenTextures(args.length, args, 0);
            texName = args[0];

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferName);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferName);

            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBufferName);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texName);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texName, 0);

            int frameBufferStatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

            if (frameBufferStatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                throw new RuntimeException("Failed to initialize framebuffer object: $frameBufferStatus");
            }
        } catch (RuntimeException e) {
            release();
            throw e;
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, savedFrameBuffer);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, savedRenderBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, savedTexName);
    }

    private void enable() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferName);
    }

    private void disable() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void renderTextureByBitmap(Bitmap bitmap) {
        enable();

        int texture = renderFrame.loadTexture(bitmap);
        renderFrame.renderTexture(texture, width, height, this.glCubeBuffer, this.glTextureBuffer);

        disable();
    }

    public int getTexName() {
        return texName;
    }

    public void release() {
        int[] args = new int[1];

        args[0] = texName;
        GLES20.glDeleteTextures(args.length, args, 0);
        texName = 0;

        args[0] = renderBufferName;
        GLES20.glDeleteRenderbuffers(args.length, args, 0);
        renderBufferName = 0;

        args[0] = frameBufferName;
        GLES20.glDeleteFramebuffers(args.length, args, 0);
        frameBufferName = 0;
    }
}
