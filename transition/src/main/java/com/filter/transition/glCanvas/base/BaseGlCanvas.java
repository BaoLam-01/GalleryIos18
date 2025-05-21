/**
 * Created by Matthew Stewart on 10/30/2017 10:47:00 AM
 */


package com.filter.transition.glCanvas.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.filter.transition.GLFilter;

import java.nio.FloatBuffer;

public abstract class BaseGlCanvas extends GLFilter {
    public BaseGlCanvas(Context context) {
        super();
    }

    private int[] textures = new int[1];

    protected float currentProgress;
    protected int time = 2000;


    private Bitmap bitmap = null;
    protected Bitmap bm = null;

    @Override
    public void onInitialized() {
        super.onInitialized();
        runOnDraw(() -> {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            createBitmap();
        });
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bm = bitmap;
    }

    private void createBitmap() {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(mOutputWidth, mOutputHeight, Bitmap.Config.ARGB_8888);
        }
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
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
        currentProgress++;
        createBitmap();
        bitmap.eraseColor(Color.argb(0, 0, 0, 0));
        Canvas bitmapCanvas = new Canvas(bitmap);
        drawCanvas(bitmapCanvas);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);


        if (bitmap != null && !bitmap.isRecycled()) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);
        }

        GLES20.glUniform1i(mGLUniformTexture, 0);

        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    protected abstract void drawCanvas(Canvas canvas);

}
