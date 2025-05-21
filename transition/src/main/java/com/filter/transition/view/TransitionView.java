package com.filter.transition.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.filter.base.GLTextureView;
import com.filter.helper.FilterManager;
import com.filter.transition.TransitionFilterGroup;
import com.filter.transition.base.BaseTransitionRenderer;
import com.filter.transition.model.AnimationType;
import com.filter.transition.model.Scene;
import com.filter.transition.model.TimeFilter;
import com.filter.transition.model.TimeTransition;

import java.util.ArrayList;

public class TransitionView extends FrameLayout {

    private GLSurfaceView surfaceView;
    private GLTextureView textureView;

    private BaseTransitionRenderer renderer;
    private boolean isTemplate;

    public TransitionView(Context context) {
        super(context);
        init(context, null, false, false, new ArrayList<>());
    }

    public TransitionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, false, false, new ArrayList<>());
    }

    public TransitionView(Context context, boolean isTemplate, boolean isCustomTemplate, ArrayList<AnimationType> animationTypes) {
        super(context);
        init(context, null, isTemplate, isCustomTemplate, animationTypes);
    }

    private void init(Context context, AttributeSet attrs, boolean isTemplate, boolean isCustomTemplate, ArrayList<AnimationType> animationTypes) {
        FilterManager.init(context);
        this.isTemplate = isTemplate || checkVersionApi22();
        if (this.isTemplate) {
            renderer = new TransitionRendererTexture(getContext(), isCustomTemplate, animationTypes);
            textureView = new GLTextureView(context, attrs);
            textureView.setEGLContextClientVersion(2);
            textureView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            textureView.setRenderer((GLTextureView.Renderer) renderer);
            textureView.setRenderMode(GLTextureView.RENDERMODE_WHEN_DIRTY);
            textureView.requestRender();
            addView(textureView);
        } else {
            renderer = new TransitionRendererSurface(getContext(), isCustomTemplate, animationTypes);
            surfaceView = new GLSurfaceView(context, attrs);
            surfaceView.setEGLContextClientVersion(2);
            surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            surfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
            surfaceView.setRenderer((GLSurfaceView.Renderer) renderer);
            surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            surfaceView.requestRender();
            addView(surfaceView);
        }
    }

    private boolean checkVersionApi22() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public void setScenes(ArrayList<Scene> scenes) {
        renderer.setScenes(scenes);
        requestRender();
    }

    public void addScenes(Scene scene, int pos) {
        renderer.addScene(scene, pos);
        requestRender();
    }

    public void addListScenes(ArrayList<Scene> scenes) {
        renderer.addScenes(scenes);
        requestRender();
    }

    public void addListScenesByPos(ArrayList<Scene> scenes, int pos) {
        renderer.addScenesByPos(scenes, pos);
        requestRender();
    }

    public void addFilterEffect(AnimationType animationType) {
        renderer.addGLFilter(animationType);
        requestRender();
    }

    public void addNoneFilterEffect() {
        renderer.addNoneGLFilter();
        requestRender();
    }

    public int getPosFilterEffect(long id) {
        return renderer.getPosFilterEffect(id);
    }

    public void setTotalTime(int time) {
        renderer.setTotalTime(time);
    }

    public void changeValueFilter(long id, float value) {
        renderer.changeValueFilter(id, value);
        requestRender();
    }

    public void changeTimeDelay(long id, int time) {
        renderer.changeTimeDelay(id, time);
        requestRender();
    }

    public void changeTimeAnimation(long id, int time) {
        renderer.changeTimeAnimation(id, time);
        requestRender();
    }

    public void changeTimeDelayAndTimeAnimation(long id, int timeDelay, int timeAnim) {
        renderer.changeTimeDelayAndTimeAnimation(id, timeDelay, timeAnim);
        requestRender();
    }

    public void replaceFilterEffect(AnimationType animationType, int pos) {
        renderer.replaceGLFilter(animationType, pos);
        requestRender();
    }

    public void deleteFilterEffect(long id) {
        renderer.removeGLFilter(id);
        requestRender();
    }

    public void setCurrentTime(int time) {
        renderer.setCurrentTime(time);
        requestRender();
    }

    public void setCurrentTime() {
        renderer.setCurrentTime();
        requestRender();
    }

    public void setCurrentTimeAfterFormat(int time) {
        renderer.setCurrentTimeAfterFormat(time);
        requestRender();
    }

    public void requestRender() {
        if (isTemplate) {
            textureView.requestRender();
        } else {
            surfaceView.requestRender();
        }
    }

    private boolean isResume;

    public void onPause() {
        if (isResume) {
            isResume = false;
            if (isTemplate) {
                textureView.onPause();
            } else {
                surfaceView.onPause();
            }
            renderer.deleteImage();
        }
    }

    public void onResume() {
        if (!isResume) {
            isResume = true;
            if (isTemplate) {
                textureView.post(() -> {
                    textureView.onResume();
                    setCurrentTime();
                });
            } else {
                surfaceView.post(() -> {
                    surfaceView.onResume();
                    setCurrentTime();
                });
            }
        }
    }

    public void setTemplate(boolean isTemplate) {
        if (renderer != null) {
            renderer.setTemplate(isTemplate);
        }
    }

    public ArrayList<Scene> getScenes() {
        return renderer.getScenes();
    }

    public ArrayList<AnimationType> getAnimationTypes() {
        return renderer.getAnimationTypes();
    }

    public ArrayList<Float> getListProgressFilterEffect() {
        return renderer.getProgressFilterEffect();
    }

    public void updateTimeTransition() {
        renderer.updateTimeTransition();
    }

    public int getCurrentPos() {
        return renderer.getCurrentPos();
    }

    public ArrayList<TimeTransition> getTimeTransitions() {
        return renderer.getTimeTransitions();
    }

    public ArrayList<TimeFilter> getTimeFilter() {
        return renderer.getTimeFilters();
    }

    public TransitionFilterGroup getTransitionFilterGroup() {
        return renderer.getFilterGroup();
    }
}