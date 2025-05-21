package com.filter.transition.base;

import android.content.Context;
import android.opengl.GLES20;

import com.filter.base.GPUImageFilter;

public abstract class BaseGLFilter extends GPUImageFilter {

    private int displacement = -1;
    private int progressLocation = 0;
    protected float progress = 0.5f;

    public BaseGLFilter(String fragmentShader) {
        super(NO_FILTER_VERTEX_SHADER, fragmentShader);
    }

    public void setFragmentShader(Context context, String raw) {
//        mFragmentShader = readShaderFromRawResource(context, raw);
        mFragmentShader = loadShaderFromAssets(context, raw);
    }

    @Override
    public void onInit() {
        super.onInit();
        progressLocation = GLES20.glGetUniformLocation(getProgram(), "progress");
        onInitTransition();
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setProgress(progress);
        onInitializedTransition();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        setFloat(progressLocation, progress);
    }

    protected abstract void onInitTransition();

    protected abstract void onInitializedTransition();

}
