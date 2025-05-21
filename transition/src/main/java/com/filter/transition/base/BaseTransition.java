package com.filter.transition.base;

import android.opengl.GLES20;

import com.filter.base.GPUImageFilter;
import com.filter.base.OpenGlUtils;

import java.nio.FloatBuffer;

public abstract class BaseTransition extends GPUImageFilter {

    private int glUniformTexture2;
    private int progressLocation = 0;
    private float progress = 0.5f;

    public BaseTransition(String fragmentShader) {
        super(NO_FILTER_VERTEX_SHADER, fragmentShader);
    }

    @Override
    public void onInit() {
        super.onInit();
        glUniformTexture2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
        progressLocation = GLES20.glGetUniformLocation(getProgram(), "progress");

        onInitTransition();
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setProgress(progress);
        onInitializedTransition();
    }


    public void onDraw(int currentTexture, int nextTexture, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
//        this.texture2 = texture2;
//        onDraw(texture1, cubeBuffer, textureBuffer);

        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }

        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        if (currentTexture != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentTexture);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        if (nextTexture != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nextTexture);
            GLES20.glUniform1i(glUniformTexture2, 1);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

//    @Override
//    protected void onDrawArraysPre() {
//        super.onDrawArraysPre();
//        if (texture2 != OpenGlUtils.NO_TEXTURE) {
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2);
//            GLES20.glUniform1i(glUniformTexture2, 1);
//        }
//    }

    public void setProgress(float progress) {
        this.progress = progress;
        setFloat(progressLocation, progress);
    }

    protected abstract void onInitTransition();

    protected abstract void onInitializedTransition();


}
