/**
 * Created by Matthew Stewart on 10/30/2017 10:46:59 AM
 */
package com.filter.transition;

import static com.filter.base.TextureRotationUtil.TEXTURE_NO_ROTATION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES32;
import android.os.Build;

import com.filter.base.GPUImageFilter;
import com.filter.base.GPUImageRenderer;
import com.filter.base.Rotation;
import com.filter.base.TextureRotationUtil;
import com.filter.transition.glCanvas.base.BaseGlCanvas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransitionFilterGroup extends GPUImageFilter {

    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private final FloatBuffer mGLTextureFlipBuffer;
    protected List<GPUImageFilter> mFilters;
    protected List<GPUImageFilter> mMergedFilters;
    private int[] mFrameBuffers;
    private int[] mFrameBufferTextures;


    public TransitionFilterGroup() {
        this(null);
    }

    public List<GPUImageFilter> getmFilters() {
        return mFilters;
    }

    public int sizeEffect() {
        return effects.size();
    }

    public void setListFilter(List<GPUImageFilter> listFilter) {
        mFilters = listFilter;
    }


    public TransitionFilterGroup(List<GPUImageFilter> filters) {
        mFilters = filters;
        if (mFilters == null) {
            mFilters = new ArrayList<GPUImageFilter>();
        } else {
            updateMergedFilters();
        }

        mGLCubeBuffer = ByteBuffer.allocateDirect(GPUImageRenderer.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(GPUImageRenderer.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);

        float[] flipTexture = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
        mGLTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureFlipBuffer.put(flipTexture).position(0);
    }

    public void addFilter(GPUImageFilter aFilter) {
        if (aFilter == null) {
            return;
        }
        mFilters.add(aFilter);
        updateMergedFilters();
    }


    public void addFilter(int p, GPUImageFilter aFilter) {
        if (aFilter == null) {
            return;
        }
        mFilters.remove(p);
        mFilters.add(p, aFilter);

        updateMergedFilters();
    }

    public void remove(int position) {
        if (mFilters != null && mFilters.size() > position) {
            mFilters.remove(position);
            updateMergedFilters();
        }
    }

    public void setFilter(int position, GLFilter glFilter) {
        if (mFilters != null && mFilters.size() > position) {
            mFilters.set(position, glFilter);
            mMergedFilters.set(position, glFilter);
//            updateMergedFilters();
        }
    }

    public void addFilter(GPUImageFilter filter, int position) {
        if (filter == null) {
            return;
        }
        if (mFilters != null) {
            mFilters.add(position, filter);
            effects.add("");
            updateMergedFilters();
        }
    }

    public void removeFilter(int pos) {
        if (mFilters != null && mFilters.size() > pos + 1) {
            mFilters.remove(pos + 1);
            effects.remove(pos);
            updateMergedFilters();
        }
    }


    public void replaceFilter(int pos) {
        if (mFilters != null) {
            effects.set(pos, "");
            updateMergedFilters();
        }
    }


    @Override
    public void onInit() {
        super.onInit();
        for (GPUImageFilter filter : mFilters) {
            filter.init();
        }

        needInit = false;
    }


    @Override
    public void onDestroy() {
        destroyFramebuffers();
        for (GPUImageFilter filter : mFilters) {
            filter.destroy();
        }
        super.onDestroy();
    }

    private void destroyFramebuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }


    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        if (mFrameBuffers != null) {
            destroyFramebuffers();
        }

        int size = mFilters.size();
        for (int i = 0; i < size; i++) {
            mFilters.get(i).onOutputSizeChanged(width, height);
        }

        if (mMergedFilters != null && mMergedFilters.size() > 0) {
            size = mMergedFilters.size();
            mFrameBuffers = new int[size - 1];
            mFrameBufferTextures = new int[size - 1];
            for (int i = 0; i < size - 1; i++) {
                GLES20.glGenFramebuffers(1, mFrameBuffers, i);
                GLES20.glGenTextures(1, mFrameBufferTextures, i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                            GLES20.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_BORDER);
                } else {
                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                            GLES20.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_BORDER);
                } else {
                    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
                }
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i], 0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
        }
    }

    public void setProgressTransition(float progress) {
        ((TransitionFilter) mMergedFilters.get(0)).setProgress(progress);
    }

    public void setProcessEffect(float progress) {
        ((GLFilter) mMergedFilters.get(2)).setProgress(progress);
    }

    public void setProcessFilter(float progress) {
        ((GLFilter) mMergedFilters.get(1)).setProgress(progress);
    }

    public void setProcessEffectCustom(float process, int pos) {
        if (mFilters.size() > 3) {
            ((GLFilter) mMergedFilters.get(pos)).setProgress(process);
        }
    }


    @SuppressLint("WrongCall")
    public void onDraw(int currentTexture, int nextTexture, int overlayTexture, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        runPendingOnDrawTasks();
        if (!isInitialized() || mFrameBuffers == null || mFrameBufferTextures == null) {
            return;
        }
        if (mMergedFilters != null) {
            int size = mMergedFilters.size();
            int previousTexture = currentTexture;
            for (int i = 0; i < size; i++) {
                try {
                    GPUImageFilter filter = mMergedFilters.get(i);
                    boolean isNotLast = i < size - 1;
                    if (isNotLast) {
                        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                        GLES20.glClearColor(0, 0, 0, 0);
                    }

                    if (i == 0) {
                        if (filter instanceof TransitionFilter) {
                            ((TransitionFilter) filter).onDraw(currentTexture, nextTexture, cubeBuffer, textureBuffer);
                        } else {
                            filter.onDraw(previousTexture, cubeBuffer, textureBuffer);
                        }
                    } else if (i == size - 1) {
                        filter.onDraw(previousTexture, mGLCubeBuffer, (size % 2 == 0) ? mGLTextureFlipBuffer : mGLTextureBuffer);
                    } else {
                        if (filter instanceof OverlayFilter) {
                            ((OverlayFilter) filter).onDraw(previousTexture, overlayTexture, mGLCubeBuffer, mGLTextureBuffer);
                        } else {
                            filter.onDraw(previousTexture, mGLCubeBuffer, mGLTextureBuffer);
                        }
                    }

                    if (isNotLast) {
                        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                        previousTexture = mFrameBufferTextures[i];
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private String raw = "";
    private String filter = "";
    private String effect = "";
    private boolean needInit;

    private final ArrayList<String> effects = new ArrayList<>();

    public void setEffects(int size) {
        for (int i = 0; i < size; i++) {
            effects.add("");
        }
    }

    public boolean isNeedInit() {
        return needInit;
    }

    public void setTransitionFragmentShader(Context context, String raw) {
        if (!Objects.equals(this.raw, raw)) {
            this.raw = raw;
            needInit = true;
            ((TransitionFilter) mMergedFilters.get(0)).setFragmentShader(context, raw);
        }
    }

    public void setEffectFragmentShader(String filter, GLFilter glFilter, Bitmap bm) {
        if (!Objects.equals(this.effect, filter)) {
            this.effect = filter;
            needInit = true;
            glFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            setFilter(2, glFilter);
        }
        if (glFilter instanceof BaseGlCanvas) {
            runOnDraw(() -> ((BaseGlCanvas) glFilter).setBitmap(bm));
        }
    }

    public void setEffectFragmentShaderToCustom(String filter, GLFilter glFilter, int pos) {
        if (!Objects.equals(effects.get(pos - 1), filter)) {
            effects.set(pos - 1, filter);
            needInit = true;
            glFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            setFilter(pos, glFilter);
        }
    }

    public void setFilterFragmentShader(String filter, GLFilter glFilter) {
        if (!Objects.equals(this.filter, filter)) {
            this.filter = filter;
            needInit = true;
            glFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            setFilter(1, glFilter);
        }
    }


    public GPUImageFilter getFilterWithPosition(int position) {
        return mFilters.get(position);
    }

    public List<GPUImageFilter> getListFIlter() {
        return mFilters;
    }

    public List<GPUImageFilter> getMergedFilters() {
        return mMergedFilters;
    }

    public int getSize() {
        if (mFilters != null) {
            return mFilters.size();
        }
        return 0;
    }

    public void updateMergedFilters() {
        if (mFilters == null) {
            return;
        }

        if (mMergedFilters == null) {
            mMergedFilters = new ArrayList<>();
        } else {
            mMergedFilters.clear();
        }

        List<GPUImageFilter> filters;
        for (GPUImageFilter filter : mFilters) {
            if (filter instanceof TransitionFilterGroup) {
                ((TransitionFilterGroup) filter).updateMergedFilters();
                filters = ((TransitionFilterGroup) filter).getMergedFilters();
                if (filters == null || filters.isEmpty())
                    continue;
                mMergedFilters.addAll(filters);
                continue;
            }
            mMergedFilters.add(filter);
        }
    }
}

