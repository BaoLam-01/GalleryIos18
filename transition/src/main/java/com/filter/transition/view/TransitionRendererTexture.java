package com.filter.transition.view;

import android.content.Context;

import com.filter.base.GLTextureView;
import com.filter.transition.base.BaseTransitionRenderer;
import com.filter.transition.model.AnimationType;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TransitionRendererTexture extends BaseTransitionRenderer implements GLTextureView.Renderer {
    public TransitionRendererTexture(Context context, boolean isCustomTemplate, ArrayList<AnimationType> animationTypes) {
        super(context, isCustomTemplate, animationTypes);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        onSurfaceCreate();
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        onSurfaceChange(width, height);
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        onDraw();
    }
}
