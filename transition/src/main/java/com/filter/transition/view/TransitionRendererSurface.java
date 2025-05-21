package com.filter.transition.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.filter.transition.base.BaseTransitionRenderer;
import com.filter.transition.model.AnimationType;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TransitionRendererSurface extends BaseTransitionRenderer implements GLSurfaceView.Renderer {
    public TransitionRendererSurface(Context context, boolean isCustomTemplate, ArrayList<AnimationType> animationTypes) {
        super(context,isCustomTemplate, animationTypes);
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
