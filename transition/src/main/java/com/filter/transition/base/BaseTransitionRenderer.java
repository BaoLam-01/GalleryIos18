package com.filter.transition.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.filter.advanced.adjust.GPUImageBrightnessFilter;
import com.filter.base.OpenGlUtils;
import com.filter.helper.FilterManager;
import com.filter.helper.InterpolatorType;
import com.filter.helper.MagicFilterType;
import com.filter.transition.GLFilter;
import com.filter.transition.OverlayFilter;
import com.filter.transition.TransitionFilter;
import com.filter.transition.TransitionFilterGroup;
import com.filter.transition.model.AnimationType;
import com.filter.transition.model.Scene;
import com.filter.transition.model.TimeFilter;
import com.filter.transition.model.TimeTransition;
import com.filter.utils.BitmapUtils;
import com.filter.utils.InterpolatorUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BaseTransitionRenderer {
    public static final int NO_IMAGE = -1;
    public static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };
    public static final float[] TEXTURE_NO_ROTATION = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    private Bitmap currentBitmap, nextBitmap;

    private int currentTexture = NO_IMAGE;
    private int nextTexture = NO_IMAGE;

    private final FloatBuffer glCubeBuffer;
    private final FloatBuffer glTextureBuffer;

    private int outputWidth;
    private int outputHeight;
    private int imageWidth;
    private int imageHeight;
    private final Queue<Runnable> runOnDraw;
    private final ArrayList<TimeTransition> timeTransitions;
    private final ArrayList<TimeFilter> timeFilters;
    private ArrayList<Scene> scenes = new ArrayList<>();
    private final FilterManager filterManager = FilterManager.getInstance();
    private int currentTime = 0;
    private float progressTransition = 0;
    private float progressEffect = 0;
    private float progressFilter = 0;
    private boolean needAdjustSize = true;
    private TransitionFilterGroup filterGroup;
    private BitmapFactory.Options options;
    private Bitmap bitmapQueued;
    private String currentPath = "";
    private final Context context;
    private boolean isTemplate = false;
    private final boolean isCustomTemplate;
    private final ArrayList<AnimationType> animationTypes = new ArrayList<>();
    private final ArrayList<Float> progressFilterEffect = new ArrayList<>();
    private int totalTime = 0;
    private int currentPos = 0;
    private boolean isJustOne = false;


    public BaseTransitionRenderer(Context context, boolean isCustomTemplate, ArrayList<AnimationType> animationTypes) {
        this.context = context;
        this.isCustomTemplate = isCustomTemplate;
        this.animationTypes.clear();
        this.animationTypes.addAll(animationTypes);
        runOnDraw = new LinkedList<>();
        timeTransitions = new ArrayList<>();
        timeFilters = new ArrayList<>();
        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glCubeBuffer.clear();
        glCubeBuffer.put(CUBE).position(0);

        glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        initFilter();
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    private void initFilter() {
        filterGroup = new TransitionFilterGroup();
        filterGroup.addFilter(new TransitionFilter());

        if (!isCustomTemplate && animationTypes.isEmpty()) {
            filterGroup.addFilter(new GLFilter());
            filterGroup.addFilter(new GLFilter());
        }

        filterGroup.addFilter(new OverlayFilter(context));

        filterGroup.addFilter(new GPUImageBrightnessFilter(0f));

        if (!animationTypes.isEmpty()) {
            for (int i = 0; i < animationTypes.size(); i++) {
                filterGroup.addFilter(new GLFilter(), posAddFilterEffect());
                progressFilterEffect.add(0f);
            }
        }
    }

    public void onSurfaceCreate() {
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        filterGroup.init();
    }

    public void onSurfaceChange(final int width, final int height) {
        outputWidth = width;
        outputHeight = height;
        GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(filterGroup.getProgram());
        filterGroup.onOutputSizeChanged(width, height);
//        adjustImageScaling(imageWidth, imageHeight);
    }

    public void onDraw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll(runOnDraw);
        if (progressTransition >= 0) {
            filterGroup.setProgressTransition(progressTransition);
        }
        if (isCustomTemplate) {
            setProgressListEffect();
        } else {
            if (progressEffect >= 0) {
                filterGroup.setProcessEffect(progressEffect);
            }
            if (progressFilter >= 0) {
                filterGroup.setProcessFilter(progressFilter);
            }
            setProgressListEffect();
        }
        filterGroup.onDraw(currentTexture, nextTexture, -1, glCubeBuffer, glTextureBuffer);
    }

    private void setProgressListEffect() {
        if (!progressFilterEffect.isEmpty()) {
            for (int i = 0; i < progressFilterEffect.size(); i++) {
                if (progressFilterEffect.get(0) >= 0) {
                    filterGroup.setProcessEffectCustom(progressFilterEffect.get(i), i + 1);
                }
            }
        }
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    public void setScenes(ArrayList<Scene> scenes) {
        deleteImage();
        this.scenes = scenes;
        updateTimeTransition();
    }

    public void addScene(Scene scene, int pos) {
//        deleteImage();
        this.scenes.add(pos, scene);
        updateTimeTransition();
    }


    public void addScenes(ArrayList<Scene> scenes) {
//        deleteImage();
        this.scenes.addAll(scenes);
        updateTimeTransition();
    }

    public void addScenesByPos(ArrayList<Scene> scenes, int pos) {
//        deleteImage();
        this.scenes.addAll(pos, scenes);
        updateTimeTransition();
    }

    public void addGLFilter(AnimationType animationType) {
        filterGroup.addFilter(new GLFilter(), posAddFilterEffect());
        animationTypes.add(animationType);
        progressFilterEffect.add(0f);
    }

    public void addNoneGLFilter() {
        filterGroup.addFilter(new GLFilter(), posAddFilterEffect());
        animationTypes.add(new AnimationType());
        progressFilterEffect.add(0f);
    }

    public void removeGLFilter(long id) {
        int pos = -1;
        for (int i = 0; i < animationTypes.size(); i++) {
            if (animationTypes.get(i).getId() == id) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            filterGroup.removeFilter(pos);
            animationTypes.remove(pos);
            progressFilterEffect.remove(pos);
        }
    }

    public int getPosFilterEffect(long id) {
        for (int i = 0; i < animationTypes.size(); i++) {
            if (animationTypes.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private int posAddFilterEffect() {
        return filterGroup.getSize() - 2;
    }

    public void replaceGLFilter(AnimationType animationType, int pos) {
        filterGroup.replaceFilter(pos);
        animationTypes.set(pos, animationType);
        progressFilterEffect.set(pos, 0f);
    }

    public void changeValueFilter(long id, float value) {
        for (int i = 0; i < animationTypes.size(); i++) {
            if (animationTypes.get(i).getId() == id) {
                animationTypes.get(i).setValueFilter(value);
            }
        }
    }

    public void changeTimeDelayAndTimeAnimation(long id, int timeDelay, int timeAnimation) {
        for (int i = 0; i < animationTypes.size(); i++) {
            if (animationTypes.get(i).getId() == id) {
                animationTypes.get(i).setTimeAnimation(timeAnimation);
                animationTypes.get(i).setTimeDelay(timeDelay);
            }
        }
    }

    public void changeTimeDelay(long id, int time) {
        for (int i = 0; i < animationTypes.size(); i++) {
            if (animationTypes.get(i).getId() == id) {
                animationTypes.get(i).setTimeDelay(time);
            }
        }
    }

    public void changeTimeAnimation(long id, int time) {
        for (int i = 0; i < animationTypes.size(); i++) {
            if (animationTypes.get(i).getId() == id) {
                animationTypes.get(i).setTimeAnimation(time);
            }
        }
    }

    public void setTotalTime(int time) {
        this.totalTime = time;
    }

    public void updateTimeTransition() {
        timeTransitions.clear();
        int t = 0;
        int tVideo = 0;
        int tBeforeTrans = 0;
        if (scenes.isEmpty())
            return;
        if (scenes.size() > 1) {
            isJustOne = false;
            for (int i = 0; i < scenes.size() - 1; i++) {
                Scene s = scenes.get(i);
                AnimationType animationTransition = s.getAnimationTransition();
                int begin = t;
//                t += s.getDuration();
                t = t + s.getRealDuration();
                int timeTransition = 0;
                if (animationTransition != null) {
                    timeTransition = animationTransition.getTimeTransition();
                    int minTrans = (int) Math.min(0.5 * s.getRealDuration(), 0.5 * scenes.get(i + 1).getRealDuration());
                    if (timeTransition > minTrans) {
                        t = t - (timeTransition - minTrans);
                        timeTransition = minTrans;
                    }
                }
                if (isCustomTemplate) {
                    timeTransitions.add(new TimeTransition(begin - tBeforeTrans, t - timeTransition - tBeforeTrans, t - tBeforeTrans, tVideo));
                } else {
                    timeTransitions.add(new TimeTransition(begin, t - timeTransition, t + timeTransition, tVideo));
                }
                tVideo = t - timeTransition;
                tBeforeTrans += timeTransition;
            }
        } else {
            isJustOne = true;
            Scene s = scenes.get(0);
            int timeTransition = 0;
            AnimationType animationTransition = s.getAnimationTransition();
            if (animationTransition != null) {
                timeTransition = animationTransition.getTimeTransition();
            }
            timeTransitions.add(new TimeTransition(0, t - timeTransition, t + timeTransition, 0));
        }
        timeFilters.clear();
        totalTime = 0;
        int duration = 0;
        for (int i = 0; i < scenes.size(); i++) {
            Scene s = scenes.get(i);
            timeFilters.add(new TimeFilter(duration, duration + s.getDuration()));
            duration += s.getDuration();
            totalTime += s.calVisibleDuration();
        }
    }

    public void setCurrentTime() {
        setCurrentTime(currentTime);
    }

    public void setCurrentTimeAfterFormat(int currentTime) {
        this.currentTime = currentTime;
    }

    public void setCurrentTime(int currentTimes) {
        runOnDraw(() -> {
            try {
                currentTime = currentTimes;
                updateTransition();
                if (isCustomTemplate || !animationTypes.isEmpty()) {
                    updateFilterEffectToCustomTemplate();
                } else {
                    updateFilterEffect();
                }
            } catch (Exception ignored) {
            }
            updateTexture();
        });
    }

    private Bitmap decodeBitmapWithOptimize(String path, String pathMedia, boolean isFit) {
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        if (bitmapQueued != null) {
            options.inBitmap = bitmapQueued;

        }
        Bitmap bitmap;
        if (!isTemplate) {
            try {
                bitmap = BitmapUtils.resizedBitmap(BitmapFactory.decodeFile(path, options), outputWidth, outputHeight, isFit);
            } catch (Exception e) {
                bitmap = BitmapUtils.resizedBitmap(BitmapFactory.decodeFile(path, null), outputWidth, outputHeight, isFit);
            }
        } else {
            bitmap = BitmapFactory.decodeFile(path, options);
        }
        if (bitmapQueued == null || currentPath.isEmpty() || !currentPath.equals(pathMedia)) {
            bitmapQueued = BitmapFactory.decodeFile(path, options);
            currentPath = pathMedia;
        }
        return bitmap;
    }

    private void updateTransition() {
        if ((!timeTransitions.isEmpty()) && (currentTime >= timeTransitions.get(timeTransitions.size() - 1).getEnd())) {
            Scene s = scenes.get(scenes.size() - 1);
            TimeTransition timeTransition = timeTransitions.get(timeTransitions.size() - 1);
            getBitmap(s, timeTransition, true, 0);
            nextBitmap = null;
            filterGroup.setTransitionFragmentShader(context, filterManager.getTransitionString(MagicFilterType.NONE));
            progressTransition = 0;
            if (isCustomTemplate && currentTime > totalTime) {
                currentBitmap = null;
            }
            currentPos = isJustOne ? 0 : timeTransitions.size();
        } else {
            for (int i = 0; i < timeTransitions.size(); i++) {
                TimeTransition timeTransition = timeTransitions.get(i);
                if (timeTransition != null && currentTime >= timeTransition.getBegin() && currentTime < timeTransition.getEnd()) {
                    Scene scene = scenes.get(i);
                    getBitmap(scene, timeTransition, false, i);
                    AnimationType animationTransition = scene.getAnimationTransition();
                    if (animationTransition != null) {
                        filterGroup.setTransitionFragmentShader(context, filterManager.getTransitionString(animationTransition.getEffect()));
                    }
                    currentPos = i;
                    if (currentTime < timeTransition.getStart()) {
                        progressTransition = 0;
                    } else {
                        progressTransition = (float) (currentTime - timeTransition.getStart())
                                / (float) (timeTransition.getEnd() - timeTransition.getStart());
                        if (animationTransition != null) {
                            progressTransition = InterpolatorUtils.INSTANCE.getInterpolator(animationTransition.getInterpolatorType(), progressTransition);
                        }
                        currentPos++;
                    }
                    break;
                }
            }
        }
    }

    private void getBitmap(Scene s, TimeTransition timeTransition, boolean isLast, int i) {
        List<String> listVideo = s.getListPathVideo();
        if (listVideo != null) {
            int countCurrentVideo = getCountCurrentVideo(s, timeTransition, listVideo, isLast);
            if (countCurrentVideo < listVideo.size()) {
                currentBitmap = decodeBitmapWithOptimize(listVideo.get(countCurrentVideo), s.getPath(), s.isFit());
            }
            if (currentBitmap == null) {
                currentBitmap = s.getPreviewBitmap();
            }
        } else {
            currentBitmap = s.getPreviewBitmap();
        }
        if (!isLast && currentTime >= timeTransition.getStart() && currentTime <= timeTransition.getEnd()) {
            if (scenes.size() > 1) {
                List<String> listVideoNext = scenes.get(i + 1).getListPathVideo();
                getNextBitmap(s, timeTransition, i, listVideoNext);
            } else {
                List<String> listVideoNext = scenes.get(i).getListPathVideo();
                getNextBitmap(s, timeTransition, i, listVideoNext);
            }
        }
    }

    private void getNextBitmap(Scene s, TimeTransition timeTransition, int i, List<String> listVideoNext) {
        if (listVideoNext != null) {
            int countCurrentVideo = getCountCurrentVideo(s, timeTransition, listVideoNext, true);
            if (countCurrentVideo < listVideoNext.size()) {
                nextBitmap = decodeBitmapWithOptimize(listVideoNext.get(countCurrentVideo), scenes.get(i + 1).getPath(), scenes.get(i + 1).isFit());
            }
            if (nextBitmap == null) {
                nextBitmap = scenes.get(i + 1).getPreviewBitmap();
            }
        } else {
            nextBitmap = scenes.get(i + 1).getPreviewBitmap();
        }
    }

    private int getCountCurrentVideo(Scene s, TimeTransition timeTransition, List<String> listVideo, boolean isNext) {
        float process = 0;
        int timeDelay = timeTransition.getStartVideo();
        if (isNext) {
            timeDelay = timeTransition.getStart();
        }
        float timeInAnimation = (float) (currentTime - timeDelay + s.getTimeStart());
        if (timeInAnimation >= 0) {
            float input = timeInAnimation / ((listVideo.size() - 1) * 33f);
            process = Math.min(input, 1f);
        }
        return (int) (process * (listVideo.size() - 1));
    }

    private void updateFilterEffect() {
        for (int i = 0; i < timeFilters.size(); i++) {
            Scene scene = scenes.get(i);
            AnimationType animationFilter = scene.getAnimationFilter();
            AnimationType animationEffect = scene.getAnimationEffect();
            TimeFilter timeFilter = timeFilters.get(i);
            if (currentTime >= timeFilter.getStart() && currentTime <= timeFilter.getEnd()) {
                if (currentTime < timeFilter.getStart()) {
                    progressEffect = 0;
                    progressFilter = 0;
                } else {
                    if (animationEffect != null) {
                        if (currentTime >= (timeFilter.getStart() + animationEffect.getTimeDelay())
                                && currentTime <= (timeFilter.getStart() + animationEffect.getTimeDelay() + animationEffect.getTimeAnimation())) {
                            filterGroup.setEffectFragmentShader(filterManager.getFilterString(animationEffect.getEffect()), filterManager.getFilter(animationEffect.getEffect(), context), currentBitmap);
                            progressEffect = setUpTimeAnimation(animationEffect, timeFilter);
                        } else {
                            filterGroup.setEffectFragmentShader("filter_shader/filter_default.glsl", filterManager.getFilter(MagicFilterType.NONE, context), currentBitmap);
                            progressEffect = 0;
                        }

                    } else {
                        filterGroup.setEffectFragmentShader("filter_shader/filter_default.glsl", filterManager.getFilter(MagicFilterType.NONE, context), currentBitmap);
                        progressEffect = 0;
                    }
                    if (animationFilter != null) {
                        if (currentTime >= (timeFilter.getStart() + animationFilter.getTimeDelay())
                                && currentTime <= (timeFilter.getStart() + animationFilter.getTimeDelay() + animationFilter.getTimeAnimation())) {
                            filterGroup.setFilterFragmentShader(filterManager.getFilterString(animationFilter.getEffect())
                                    , filterManager.getFilter(animationFilter.getEffect(), context));
                            progressFilter = animationFilter.getValueFilter();
                        } else {
                            filterGroup.setFilterFragmentShader("filter_shader/filter_default.glsl", filterManager.getFilter(MagicFilterType.NONE, context));
                            progressFilter = 0;
                        }
                    } else {
                        filterGroup.setFilterFragmentShader("filter_shader/filter_default.glsl", filterManager.getFilter(MagicFilterType.NONE, context));
                        progressFilter = 0;
                    }
                }
                break;
            }
        }
    }

    private void updateFilterEffectToCustomTemplate() {
        for (int i = 0; i < animationTypes.size(); i++) {
            int timeDelay = animationTypes.get(i).getTimeDelay();
            int timeAnimation = animationTypes.get(i).getTimeAnimation();
            if (currentTime >= timeDelay && currentTime <= timeAnimation + timeDelay) {
                filterGroup.setEffectFragmentShaderToCustom(filterManager.getFilterString(animationTypes.get(i).getEffect()),
                        filterManager.getFilter(animationTypes.get(i).getEffect(), context), i + 1);
                if (animationTypes.get(i).getValueFilter() != -1f) {
                    progressFilterEffect.set(i, animationTypes.get(i).getValueFilter());
                } else {
                    progressFilterEffect.set(i, setUpProgressAnimation(animationTypes.get(i)));
                }
            } else {
                filterGroup.setEffectFragmentShaderToCustom("filter_shader/filter_default.glsl",
                        filterManager.getFilter(MagicFilterType.NONE, context), i + 1);
                progressFilterEffect.set(i, 0f);
            }

        }
    }

    private void updateTexture() {
        if (filterGroup.isNeedInit()) {
            filterGroup.init();
        }
        deleteImage();
        if (currentBitmap != null) {
            imageWidth = currentBitmap.getWidth();
            imageHeight = currentBitmap.getHeight();
            currentTexture = OpenGlUtils.loadTexture(currentBitmap, currentTexture, false);
            adjustImageScaling(imageWidth, imageHeight);
        } else {
            currentTexture = NO_IMAGE;
        }

        if (nextBitmap != null) {
            nextTexture = OpenGlUtils.loadTexture(nextBitmap, nextTexture, false);
        } else {
            nextTexture = NO_IMAGE;
        }
    }

    private float setUpProgressAnimation(AnimationType animationType) {
        float progress = 0;
        float from = animationType.getFrom();
        float to = animationType.getTo();
        int timeDelay = animationType.getTimeDelay();
        InterpolatorType type = animationType.getInterpolatorType();
        float timeInAnimation = (float) (currentTime - timeDelay);
        if (timeInAnimation >= 0) {
            float input = timeInAnimation / (float) (animationType.getTimeAnimation());
            if (type != InterpolatorType.NONE) {
                input = InterpolatorUtils.INSTANCE.getInterpolator(type, input);
            } else {
                input = Math.min(input, 1f);
            }
            progress = from + (to - from) * input;
        }
        return progress;
    }


    private float setUpTimeAnimation(AnimationType animationEffect, TimeFilter timeFilter) {
        float process = 0;
        float from = animationEffect.getFrom();
        float to = animationEffect.getTo();
        int timeDelay = animationEffect.getTimeDelay();
        InterpolatorType type = animationEffect.getInterpolatorType();
        float timeInAnimation = (float) (currentTime - timeFilter.getStart() - timeDelay);
        if (timeInAnimation >= 0) {
            float input = timeInAnimation / (float) (animationEffect.getTimeAnimation());
            if (type != InterpolatorType.NONE) {
                input = InterpolatorUtils.INSTANCE.getInterpolator(type, input);
            } else {
                input = Math.min(input, 1f);
            }
            process = from + (to - from) * input;
        }
        return process;
    }

    public ArrayList<Scene> getScenes() {
        return scenes;
    }

    public ArrayList<AnimationType> getAnimationTypes() {
        return animationTypes;
    }

    public ArrayList<Float> getProgressFilterEffect() {
        return progressFilterEffect;
    }

    public ArrayList<TimeTransition> getTimeTransitions() {
        return timeTransitions;
    }

    public ArrayList<TimeFilter> getTimeFilters() {
        return timeFilters;
    }

    public TransitionFilterGroup getFilterGroup() {
        return filterGroup;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void deleteImage() {
        GLES20.glDeleteTextures(1, new int[]{currentTexture}, 0);
        GLES20.glDeleteTextures(1, new int[]{nextTexture}, 0);
        currentTexture = NO_IMAGE;
        nextTexture = NO_IMAGE;
    }

    private void adjustImageScaling(int imageWidth, int imageHeight) {
        if (!needAdjustSize) {
            return;
        }
        needAdjustSize = false;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        float outputWidth = this.outputWidth;
        float outputHeight = this.outputHeight;

        float ratio1 = outputWidth / imageWidth;
        float ratio2 = outputHeight / imageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(imageWidth * ratioMax);
        int imageHeightNew = Math.round(imageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        float[] textureCords = TEXTURE_NO_ROTATION;
//        float[] cube = CUBE;
//        if (false) {
        float distHorizontal = (1 - 1 / ratioWidth) / 2;
        float distVertical = (1 - 1 / ratioHeight) / 2;
        textureCords = new float[]{
                addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical),
        };
//        } else {
//            cube = new float[]{
//                    CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
//                    CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
//                    CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
//                    CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,
//            };
//        }

        glTextureBuffer.clear();
//        glTextureBuffer.put(cube).position(0);
//        glTextureBuffer.clear();
        glTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.add(runnable);
        }
    }

}
