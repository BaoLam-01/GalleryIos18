package com.filter.transition.encoder;

import static com.filter.base.OpenGlUtils.NO_TEXTURE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.filter.base.GPUImageFilter;
import com.filter.helper.FilterManager;
import com.filter.helper.InterpolatorType;
import com.filter.helper.MagicFilterType;
import com.filter.transition.TransitionFilterGroup;
import com.filter.transition.model.AnimationType;
import com.filter.transition.model.Audio;
import com.filter.transition.model.Scene;
import com.filter.transition.model.TimeFilter;
import com.filter.transition.model.TimeTransition;
import com.filter.utils.BitmapUtils;
import com.filter.utils.Constant;
import com.filter.utils.CreateAudioManager;
import com.filter.utils.InterpolatorUtils;
import com.filter.utils.StateCodec;
import com.filter.utils.StateMuxer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoEncoderEffect {

    private StateMuxer mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
    private StateCodec mStateCodec = StateCodec.MUXER_STATE_UNINITIALIZED;
    private String currentPath = "";
    private static final float[] TEXTURE_NO_ROTATION = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    private static final float[] TEXTURE_NO_ROTATION_NOR = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    private static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 30;
    private static final int IFRAME_INTERVAL = 10;
    private static final int BIT_RATE = 16000000;
    private int currentTexture, nextTexture, overlayTexture = -1;

    private int outputWidth;
    private int outputHeight;

    private MediaCodec encoder;
    private CodecInputSurface inputSurface;
    private MediaMuxer muxer;
    private int trackIndex;
    private boolean muxerStarted;

    private MediaCodec.BufferInfo bufferInfo;

    private FloatBuffer glCubeBuffer;
    private FloatBuffer glTextureBuffer;

    private ExecutorService executorService;

    private boolean withAudio = false;
    private boolean abort = false;
    private int dem = 0;
    private final Object frameSync = new Object();
    private boolean keepEncode = true;
    private boolean noMoreFrames = false;
    private CountDownLatch newFrameLatch;
    private Queue<Bitmap> encodeQueue = new ConcurrentLinkedQueue<>();
    private String outputPath;
    private OnBitmapToVideoEncoderListener listener;
    private FrameBuffer frameBuffer;
    private FrameBuffer frameBuffer1;
    private FrameBuffer frameBufferOverlay;
    private ArrayList<Scene> scenes = new ArrayList<>();
    private ArrayList<TimeTransition> timeTransitions;
    private ArrayList<TimeFilter> timeFilters;
    private TransitionFilterGroup effect;
    private File tempFileNoAudio;
    private final Context context;
    private int duration;
    private final List<Audio> listAudio = new ArrayList<>();
    private boolean needQueue;
    private Bitmap currentBitmap, nextBitmap;
    private float progressTransition = 0;
    private float progressEffect = 0;
    private float progressFilter = 0;
    private Bitmap bitmapQueued;
    private BitmapFactory.Options options;
    private final boolean checkNotEffect;
    private RenderFrame renderFrame;
    private final boolean isCustomTemplate;
    private final ArrayList<AnimationType> animationTypes;
    private final ArrayList<Float> progressFilterEffect;
    private int totalTime;
    private boolean endStream = false;

    public VideoEncoderEffect(Context context, boolean checkNotEffect, boolean isCustomTemplate, ArrayList<AnimationType> animationTypes,
                              ArrayList<Float> progressFilterEffect, int totalTime) {
        this.context = context;
        this.checkNotEffect = checkNotEffect;
        this.isCustomTemplate = isCustomTemplate;
        this.animationTypes = animationTypes;
        this.progressFilterEffect = progressFilterEffect;
        this.totalTime = totalTime;
    }

    public boolean isNeedQueue() {
        return needQueue;
    }

    public void setNeedQueue(boolean needQueue) {
        this.needQueue = needQueue;
    }

    public void setupEncoder(ArrayList<TimeTransition> timeTransitions, ArrayList<TimeFilter> timeFilters, ArrayList<Scene> scenes, TransitionFilterGroup filterGroup,
                             String outputPath, int outputWidth, int outputHeight, int duration, List<Audio> listAudio,
                             OnBitmapToVideoEncoderListener listener) {
        if (!checkNotEffect) {
            frameBuffer = new FrameBuffer();
            frameBuffer1 = new FrameBuffer();
            frameBufferOverlay = new FrameBuffer();
            effect = new TransitionFilterGroup();
            for (GPUImageFilter f : filterGroup.getmFilters()) {
                effect.addFilter(f);
            }
            effect.setEffects(filterGroup.sizeEffect());
            effect.init();
            GLES20.glViewport(0, 0, outputWidth, outputHeight);
            GLES20.glUseProgram(effect.getProgram());
            effect.onOutputSizeChanged(outputWidth, outputHeight);
            this.scenes = scenes;
            this.timeTransitions = timeTransitions;
            this.timeFilters = timeFilters;
        } else {
            renderFrame = new RenderFrame();
        }
        this.listener = listener;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
        this.duration = duration;

        if (listAudio != null && !listAudio.isEmpty()) {
            withAudio = true;
            this.listAudio.clear();
            this.listAudio.addAll(listAudio);
        } else {
            withAudio = false;
        }
        this.outputPath = outputPath;

        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        if (checkNotEffect) {
            glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION_NOR.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        } else {
            glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        glCubeBuffer.clear();
        glCubeBuffer.put(CUBE).position(0);
        glTextureBuffer.clear();
        if (checkNotEffect) {
            glTextureBuffer.put(TEXTURE_NO_ROTATION_NOR).position(0);
        } else {
            glTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);
        }

    }

    public Queue<Bitmap> getEncodeQueue() {
        return encodeQueue;
    }

    public int getSizeQueue() {
        return encodeQueue.size();
    }

    private void mixAudioAndVideo(List<Audio> listAudio) {
        try {
            String t = this.duration / (1000 * 1.0f) + "";
            StringBuilder mix = new StringBuilder();
            StringBuilder paths = new StringBuilder();
            StringBuilder itemsAudio = new StringBuilder();
            if (!listAudio.isEmpty()) {
                for (int i = 0; i < listAudio.size(); i++) {
                    Audio audio = listAudio.get(i);
                    int timeDelay = audio.getTimeDelay();
                    int value = i + 1;
                    String strValue = "a" + value;
                    String item = "[" + value + "]adelay=" + timeDelay + "|" + timeDelay + "[" + strValue + "];";
                    itemsAudio.append(item);
                    mix.append("[").append(strValue).append("]");
                    paths.append(" -i ").append(audio.getPath());
                }
                mix.append("amix=inputs=").append(listAudio.size());
            }

            String filter = itemsAudio.append(mix).toString();
            String cmd = "-i " + tempFileNoAudio.getAbsolutePath() + " -t " + t + paths + " -filter_complex " + filter + " -c:v" + " copy" + " -c:a" + " aac " + outputPath;
            FFmpegKit.executeAsync(cmd, (session) -> {
                if (session.getReturnCode().isValueSuccess()) {
                    tempFileNoAudio.delete();
                    onSuccess();
                    for (int i = 0; i < listAudio.size(); i++) {
                        new File(listAudio.get(i).getPath()).delete();
                    }
                }
            });
        } catch (Exception exception) {
            for (int i = 0; i < listAudio.size(); i++) {
                new File(listAudio.get(i).getPath()).delete();
            }
            if (listener != null) {
                listener.onFailure();
            }
        }
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    private int posAddFilterEffect() {
        return effect.getSize() - 2;
    }

    private void onSuccess() {
        if (listener != null) {
            listener.onSuccess(outputPath);
        }
    }

    private void onFailure() {
        deleteOutputError();
        if (listener != null) {
            listener.onFailure();
        }
    }

    private void deleteOutputError() {
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public void startEncoding() {
        if (listener != null) {
            listener.onStartEncoding();
        }
        keepEncode = true;
        noMoreFrames = false;
        abort = false;
        getExecutorService().execute(this::encode);
    }

    public ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        return executorService;
    }

    private long currentTime = 0;

    public void encode() {
        try {
            prepareEncoder();
            inputSurface.makeCurrent();
            if (frameBuffer != null) {
                frameBuffer.setup(outputWidth, outputHeight);
            }
            if (frameBuffer1 != null) {
                frameBuffer1.setup(outputWidth, outputHeight);
            }
            if (frameBufferOverlay != null) {
                frameBufferOverlay.setup(outputWidth, outputHeight);
            }
            if (listener != null) {
                listener.onReadyDrawFrame();
            }
            while (keepEncode) {
                if (noMoreFrames && (encodeQueue.size() == 0)) {
                    break;
                }
                long time = computePresentationTimeNsec(dem);
                if (!checkNotEffect) {
                    currentTime = time / 1000000;
                    if (listener != null) {
                        listener.onProcess((int) currentTime);
                    }
                    if (currentTime > duration) {
                        break;
                    }
                }

                Bitmap overlayBitmap = null;
                if (needQueue) {
                    overlayBitmap = pollBitmapFromQueue();
                    if (overlayBitmap == null) {
                        continue;
                    }
                }
                drainEncoder(false);
                generateSurfaceFrame(overlayBitmap);
                inputSurface.setPresentationTime(time);
                inputSurface.swapBuffers();
                dem++;
            }
            drainEncoder(true);
        } catch (IOException ignored) {
        } finally {
            releaseEncoder();
        }
        executorService.shutdown();

        if (abort) {
            deleteOutputError();
        } else {
            if (withAudio) {
                CreateAudioManager createAudioManager = new CreateAudioManager(new CreateAudioManager.OnConvertAudioListener() {
                    @Override
                    public void onConvertSuccess(List<Audio> listAudio) {
                        mixAudioAndVideo(listAudio);
                    }

                    @Override
                    public void onConvertFailed(String path) {
                        onFailure();
                    }
                }, context);
                createAudioManager.convertListAudio(context, listAudio, isCustomTemplate);
            } else {
                if (keepEncode) {
                    onSuccess();
                } else {
                    onFailure();
                }
            }
        }
    }

    @Nullable
    private Bitmap pollBitmapFromQueue() {
        Bitmap bitmap = encodeQueue.poll();
        if (bitmap == null) {
            synchronized (frameSync) {
                newFrameLatch = new CountDownLatch(1);
            }

            try {
                newFrameLatch.await();
            } catch (InterruptedException e) {
                onFailure();
            }

            bitmap = encodeQueue.poll();
        }
        return bitmap;
    }

    public void queueFrame(Bitmap bitmap) {
        if (encoder == null || muxer == null) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            return;
        }
        encodeQueue.add(bitmap);
        synchronized (frameSync) {
            if ((newFrameLatch != null) && (newFrameLatch.getCount() > 0)) {
                newFrameLatch.countDown();
            }
        }
    }

    public void stopEncoding() {
        if (encoder == null || muxer == null) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            return;
        }
        noMoreFrames = true;
        synchronized (frameSync) {
            if ((newFrameLatch != null) && (newFrameLatch.getCount() > 0)) {
                newFrameLatch.countDown();
            }
        }
    }

    public void abortEncoding() {
        if (encoder == null || muxer == null) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            return;
        }
        noMoreFrames = true;
        abort = true;
        encodeQueue = new ConcurrentLinkedQueue<>(); // Drop all frames
        synchronized (frameSync) {
            if ((newFrameLatch != null) && (newFrameLatch.getCount() > 0)) {
                newFrameLatch.countDown();
            }
        }
    }

    private void generateSurfaceFrame(Bitmap overlayBitmap) {
        if (!checkNotEffect) {
            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            FilterManager filterManager = FilterManager.getInstance();
            updateTransition(filterManager);
            if (isCustomTemplate || !animationTypes.isEmpty()) {
                updateFilterEffectToCustomTemplate(filterManager);
            } else {
                updateFilterEffect(filterManager);
            }

            effect.init();
            effect.onOutputSizeChanged(outputWidth, outputHeight);
            if (isCustomTemplate) {
                setProgressListEffect(effect);
            } else {
                effect.setProcessEffect(progressEffect);
                effect.setProcessFilter(progressFilter);
                setProgressListEffect(effect);
            }
            effect.setProgressTransition(progressTransition);
            GLES20.glViewport(0, 0, outputWidth, outputHeight);
            GLES20.glUseProgram(effect.getProgram());
            if (currentBitmap != null && frameBuffer != null) {
                frameBuffer.renderTextureByBitmap(currentBitmap);
                currentTexture = frameBuffer.getTexName();
            } else {
                currentTexture = NO_TEXTURE;
            }
            if (nextBitmap != null && frameBuffer1 != null) {
                frameBuffer1.renderTextureByBitmap(nextBitmap);
                nextTexture = frameBuffer1.getTexName();
            } else {
                nextTexture = NO_TEXTURE;
            }
            if (overlayBitmap != null && frameBufferOverlay != null) {
                frameBufferOverlay.renderTextureByBitmap(overlayBitmap);
                overlayTexture = frameBufferOverlay.getTexName();
            } else {
                overlayTexture = NO_TEXTURE;
            }
            effect.onDraw(currentTexture, nextTexture, overlayTexture, glCubeBuffer, glTextureBuffer);
        } else {
            int texture = renderFrame.loadTexture(overlayBitmap);
            renderFrame.renderTexture(texture, outputWidth, outputHeight, glCubeBuffer, glTextureBuffer);
        }
    }

    private void setProgressListEffect(TransitionFilterGroup effect) {
        if (!progressFilterEffect.isEmpty()) {
            for (int i = 0; i < progressFilterEffect.size(); i++) {
                if (progressFilterEffect.get(0) >= 0) {
                    effect.setProcessEffectCustom(progressFilterEffect.get(i), i + 1);
                }
            }
        }
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

    private void updateFilterEffect(FilterManager filterManager) {
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
                            effect.setEffectFragmentShader(filterManager.getFilterString(animationEffect.getEffect()), filterManager.getFilter(animationEffect.getEffect(), context), currentBitmap);
                            progressEffect = setUpTimeAnimation(animationEffect, timeFilter);
                        } else {
                            effect.setEffectFragmentShader("filter_shader/filter_default.glsl", filterManager.getFilter(MagicFilterType.NONE, context), currentBitmap);
                            progressEffect = 0;
                        }

                    }
                    if (animationFilter != null) {
                        if (currentTime >= (timeFilter.getStart() + animationFilter.getTimeDelay())
                                && currentTime <= (timeFilter.getStart() + animationFilter.getTimeDelay() + animationFilter.getTimeAnimation())) {
                            effect.setFilterFragmentShader(filterManager.getFilterString(animationFilter.getEffect())
                                    , filterManager.getFilter(animationFilter.getEffect(), context));
                            progressFilter = animationFilter.getValueFilter();
                        } else {
                            effect.setFilterFragmentShader("filter_shader/filter_default.glsl", filterManager.getFilter(MagicFilterType.NONE, context));
                            progressFilter = 0;
                        }
                    }
                }
                break;
            }
        }
    }

    private void updateTransition(FilterManager filterManager) {
        if ((!timeTransitions.isEmpty()) && (currentTime >= timeTransitions.get(timeTransitions.size() - 1).getEnd())) {
            Scene s = scenes.get(scenes.size() - 1);
            TimeTransition timeTransition = timeTransitions.get(timeTransitions.size() - 1);
            getBitmap(s, timeTransition, true, 0);
            nextBitmap = null;
            effect.setTransitionFragmentShader(context, filterManager.getTransitionString(MagicFilterType.NONE));
            progressTransition = 0;
            if (isCustomTemplate && currentTime > totalTime) {
                currentBitmap = null;
            }
        } else {
            for (int i = 0; i < timeTransitions.size(); i++) {
                TimeTransition timeTransition = timeTransitions.get(i);
                if (timeTransition != null && currentTime >= timeTransition.getBegin() && currentTime < timeTransition.getEnd()) {
                    Scene scene = scenes.get(i);
                    getBitmap(scene, timeTransition, false, i);
                    AnimationType animationTransition = scene.getAnimationTransition();
                    if (animationTransition != null) {
                        effect.setTransitionFragmentShader(context, filterManager.getTransitionString(animationTransition.getEffect()));
                    }

                    if (currentTime < timeTransition.getStart()) {
                        progressTransition = 0;
                    } else {
                        progressTransition = (float) (currentTime - timeTransition.getStart())
                                / (float) (timeTransition.getEnd() - timeTransition.getStart());
                        if (animationTransition != null) {
                            progressTransition = InterpolatorUtils.INSTANCE.getInterpolator(animationTransition.getInterpolatorType(), progressTransition);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void updateFilterEffectToCustomTemplate(FilterManager filterManager) {
        for (int i = 0; i < animationTypes.size(); i++) {
            int timeDelay = animationTypes.get(i).getTimeDelay();
            int timeAnimation = animationTypes.get(i).getTimeAnimation();
            if (currentTime >= timeDelay && currentTime <= timeAnimation + timeDelay) {
                effect.setEffectFragmentShaderToCustom(filterManager.getFilterString(animationTypes.get(i).getEffect()),
                        filterManager.getFilter(animationTypes.get(i).getEffect(), context), i + 1);
                if (animationTypes.get(i).getValueFilter() != -1f) {
                    progressFilterEffect.set(i, animationTypes.get(i).getValueFilter());
                } else {
                    progressFilterEffect.set(i, setUpProgressAnimation(animationTypes.get(i)));
                }
            } else {
                effect.setEffectFragmentShaderToCustom("filter_shader/filter_default.glsl",
                        filterManager.getFilter(MagicFilterType.NONE, context), i + 1);
                progressFilterEffect.set(i, 0f);
            }

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

    private Bitmap decodeBitmapWithOptimize(String path, String pathMedia, boolean fit) {
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        if (bitmapQueued != null) {
            options.inBitmap = bitmapQueued;
        }
        Bitmap bitmap;
        try {
            bitmap = BitmapUtils.resizedBitmap(BitmapFactory.decodeFile(path, options), outputWidth, outputHeight, fit);
        } catch (Exception e) {
            bitmap = BitmapUtils.resizedBitmap(BitmapFactory.decodeFile(path), outputWidth, outputHeight, fit);
        }
        if (bitmapQueued == null || currentPath.isEmpty() || !currentPath.equals(pathMedia)) {
            bitmapQueued = BitmapFactory.decodeFile(path, options);
            currentPath = pathMedia;
        }
        return bitmap;
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

    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
    @NonNull
    private MediaFormat getMediaFormat() {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, outputWidth, outputHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        return format;
    }

    private boolean onFail() {
        if (listener != null) {
            listener.onFailure();
            return true;
        }
        return false;
    }

    private void encoderStart(MediaFormat format) {
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        inputSurface = new CodecInputSurface(encoder.createInputSurface());
        encoder.start();
    }

    private void prepareEncoder() throws IOException {
        bufferInfo = new MediaCodec.BufferInfo();
        MediaFormat format = getMediaFormat();
        encoder = MediaCodec.createEncoderByType(MIME_TYPE);
        try {
            encoderStart(format);
        } catch (Exception e) {
            outputWidth = Constant.VIDEO_WIDTH_LOW_1;
            outputHeight = Constant.VIDEO_HEIGHT_LOW_1;
            format = getMediaFormat();
            try {
                encoderStart(format);
            } catch (Exception ex) {
                if (onFail()) return;
            }
        }
        mStateCodec = StateCodec.MUXER_STATE_INITIALIZED;

        String outputFileString;
        if (withAudio) {
            tempFileNoAudio = new File(context.getCacheDir(), Calendar.getInstance().getTimeInMillis() + ".mp4");
            try {
                outputFileString = tempFileNoAudio.getCanonicalPath();
            } catch (IOException e) {
                return;
            }
        } else {
            try {
                outputFileString = new File(outputPath).getCanonicalPath();
            } catch (IOException e) {
                return;
            }
        }
        try {
            muxer = new MediaMuxer(outputFileString, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mStateMuxer = StateMuxer.MUXER_STATE_INITIALIZED;
        } catch (IOException ioe) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }

        trackIndex = -1;
        muxerStarted = false;
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    private void releaseEncoder() {
        if (encoder != null && mStateCodec == StateCodec.MUXER_STATE_INITIALIZED) {
            try {
                encoder.stop();
                encoder.release();
                encoder = null;
            } catch (IllegalStateException ignored) {
            }
            mStateCodec = StateCodec.MUXER_STATE_UNINITIALIZED;
        }
        if (inputSurface != null) {
            inputSurface.release();
            inputSurface = null;
        }
        if (muxer != null && muxerStarted && mStateMuxer == StateMuxer.MUXER_STATE_STARTED) {
            try {
                muxer.stop();
                muxer.release();
                muxer = null;
            } catch (IllegalStateException ignored) {
            }
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
        }
        if (frameBuffer != null) {
            frameBuffer.release();
        }
        if (frameBuffer1 != null) {
            frameBuffer1.release();
        }
        if (frameBufferOverlay != null) {
            frameBufferOverlay.release();
        }
    }

    /**
     * Extracts all pending data from the encoder.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (mStateCodec != StateCodec.MUXER_STATE_INITIALIZED) {
            return;
        }
        if (endOfStream) {
            if (!endStream) {
                try {
                    endStream = true;
                    encoder.signalEndOfInputStream();
                } catch (Exception ignored) {
                }
            }
        }

        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        while (mStateCodec == StateCodec.MUXER_STATE_INITIALIZED) {
            int encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = encoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (muxerStarted) {
                    mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
                    break;
                }
                MediaFormat newFormat = encoder.getOutputFormat();

                // now that we have the Magic Goodies, start the muxer
                trackIndex = muxer.addTrack(newFormat);
                if (mStateMuxer == StateMuxer.MUXER_STATE_INITIALIZED) {
                    muxer.start();
                    muxerStarted = true;
                    mStateMuxer = StateMuxer.MUXER_STATE_STARTED;
                }
            } else if (encoderStatus < 0) {
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    bufferInfo.size = 0;
                }

                if (bufferInfo.size != 0) {
                    if (!muxerStarted) {
                        mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
                        if (listener != null) {
                            listener.onFailure();
                        }
                        break;
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(bufferInfo.offset);
                    encodedData.limit(bufferInfo.offset + bufferInfo.size);

                    try {
                        muxer.writeSampleData(trackIndex, encodedData, bufferInfo);
                        mStateMuxer = StateMuxer.MUXER_STATE_STARTED;
                    } catch (Exception ignored) {
                        mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
                    }
                }

                encoder.releaseOutputBuffer(encoderStatus, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0 || endStream) {
                    break;      // out of while
                }
            }
        }
    }

    /**
     * Generates the presentation time for frame N, in nanoseconds.
     */
    private static long computePresentationTimeNsec(int frameIndex) {
        final long ONE_BILLION = 1000000000;
        return frameIndex * ONE_BILLION / FRAME_RATE;
    }

    /**
     * Holds state associated with a Surface used for MediaCodec encoder input.
     * <p>
     * The constructor takes a Surface obtained from MediaCodec.createInputSurface(), and uses that
     * to create an EGL window surface.  Calls to eglSwapBuffers() cause a frame of data to be sent
     * to the video encoder.
     * <p>
     * This object owns the Surface -- releasing this will release the Surface too.
     */
    public static class CodecInputSurface {
        private static final int EGL_RECORDABLE_ANDROID = 0x3142;

        private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
        private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

        private Surface mSurface;

        /**
         * Creates a CodecInputSurface from a Surface.
         */
        public CodecInputSurface(Surface surface) {
            if (surface == null) {
                throw new NullPointerException();
            }
            mSurface = surface;

            eglSetup();
        }

        /**
         * Prepares EGL.  We want a GLES 2.0 context and a surface that supports recording.
         */
        private void eglSetup() {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                throw new RuntimeException("unable to get EGL14 display");
            }
            int[] version = new int[2];
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                throw new RuntimeException("unable to initialize EGL14");
            }

            // Configure EGL for recording and OpenGL ES 2.0.
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL_RECORDABLE_ANDROID, 1,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                    numConfigs, 0);
            checkEglError("eglCreateContext RGB888+recordable ES2");

            // Configure context for OpenGL ES 2.0.
            int[] attrib_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
                    attrib_list, 0);
            checkEglError("eglCreateContext");

            // Create a window surface, and attach it to the Surface we received.
            int[] surfaceAttribs = {
                    EGL14.EGL_NONE
            };
            mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurface,
                    surfaceAttribs, 0);
            checkEglError("eglCreateWindowSurface");
        }

        /**
         * Discards all resources held by this class, notably the EGL context.  Also releases the
         * Surface that was passed to our constructor.
         */
        public void release() {
            if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
                EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(mEGLDisplay);
            }

            mSurface.release();

            mEGLDisplay = EGL14.EGL_NO_DISPLAY;
            mEGLContext = EGL14.EGL_NO_CONTEXT;
            mEGLSurface = EGL14.EGL_NO_SURFACE;

            mSurface = null;
        }

        /**
         * Makes our EGL context and surface current.
         */
        public void makeCurrent() {
            EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
            checkEglError("eglMakeCurrent");
        }

        /**
         * Calls eglSwapBuffers.  Use this to "publish" the current frame.
         */
        public boolean swapBuffers() {
            boolean result = EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
            checkEglError("eglSwapBuffers");
            return result;
        }

        /**
         * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
         */
        public void setPresentationTime(long nsecs) {
            EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nsecs);
            checkEglError("eglPresentationTimeANDROID");
        }

        /**
         * Checks for EGL errors.  Throws an exception if one is found.
         */
        private void checkEglError(String msg) {
            int error;
            if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
                throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
            }
        }
    }

    public interface OnBitmapToVideoEncoderListener {
        void onStartEncoding();

        void onReadyDrawFrame();

        void onProcess(int process);

        void onSuccess(String path);

        void onFailure();
    }
}