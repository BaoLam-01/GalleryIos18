/**
 * Created by Matthew Stewart on 10/30/2017 10:47:01 AM
 */
package com.filter.transition;

import android.content.Context;
import android.opengl.GLES20;

import com.filter.base.GPUImageFilter;
import com.filter.base.OpenGlUtils;

import java.nio.FloatBuffer;

public class OverlayFilter extends GPUImageFilter {
    protected int mGLUniformTextureOverlay;
    public OverlayFilter(Context context) {
        super(NO_FILTER_VERTEX_SHADER,loadShaderFromAssets(context,"filter_shader/filter_bitmap_on_bitmap.glsl"));
    }



    @Override
    public void onInitialized() {
        super.onInitialized();
    }

    @Override
    public void onInit() {
        super.onInit();
        mGLUniformTextureOverlay = GLES20.glGetUniformLocation(getProgram(), "inputImageTextureOverlay");
    }

    public void onDraw(int textureId, int textureOverlay, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
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
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }

        if (textureOverlay != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureOverlay);
            GLES20.glUniform1i(mGLUniformTextureOverlay, 1);
        }

        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}
