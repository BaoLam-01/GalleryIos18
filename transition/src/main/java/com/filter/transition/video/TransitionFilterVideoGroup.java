/**
 * Created by Matthew Stewart on 10/30/2017 10:46:59 AM
 */
package com.filter.transition.video;

import static com.filter.base.TextureRotationUtil.TEXTURE_NO_ROTATION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.filter.base.GPUImageRenderer;
import com.filter.base.IDrawer;
import com.filter.base.Rotation;
import com.filter.base.TextureRotationUtil;
import com.filter.transition.GLFilter;
import com.filter.transition.TransitionFilter;
import com.filter.transition.base.BaseGLFilterVideo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class TransitionFilterVideoGroup implements IDrawer {

    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private final FloatBuffer mGLTextureFlipBuffer;
    protected List<IDrawer> mFilters;
    protected List<IDrawer> mMergedFilters;
    private int[] mFrameBuffers;
    private int[] mFrameBufferTextures;
    private int mOutputWidth;
    private int mOutputHeight;

    public TransitionFilterVideoGroup() {
        this(null);
    }

    public List<IDrawer> getmFilters() {
        return mFilters;
    }

    public void setListFilter(List<IDrawer> listFilter) {
        mFilters = listFilter;
    }


    public TransitionFilterVideoGroup(List<IDrawer> filters) {
        mFilters = filters;
        if (mFilters == null) {
            mFilters = new ArrayList<IDrawer>();
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

    public void addFilter(IDrawer aFilter) {
        if (aFilter == null) {
            return;
        }
        mFilters.add(aFilter);
        updateMergedFilters();
    }

    private int widthVideo = 0;
    private int heightVideo = 0;

    public void setSize(int w, int h) {
        if (widthVideo != w && heightVideo != h) {
            widthVideo = w;
            heightVideo = h;
            for (IDrawer draw : mMergedFilters) {
                draw.setVideoSize(w, h);
            }
        }
    }

    public void setmFilters(List<IDrawer> mFilters) {
        this.mFilters = mFilters;
        updateMergedFilters();
    }

    public void addFilter(int p, IDrawer aFilter) {
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

    public void setFilter(int position, IDrawer glFilter) {
        if (mFilters != null && mFilters.size() > position) {
            mFilters.set(position, glFilter);
            mMergedFilters.set(position, glFilter);
//            updateMergedFilters();
        }
    }

    public void addFilter(IDrawer filter, int position) {
        if (filter == null) {
            return;
        }
        if (mFilters != null) {
            mFilters.add(position, filter);
            updateMergedFilters();
        }
    }


    public void onDestroy() {
        destroyFramebuffers();
        for (IDrawer filter : mFilters) {
            filter.release();
        }
//        super.onDestroy();
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

    public void onOutputSizeChanged(final int width, final int height) {
        if (mFrameBuffers != null) {
            destroyFramebuffers();
        }
        mOutputWidth = width;
        mOutputHeight = height;
        if (mMergedFilters != null && mMergedFilters.size() > 0) {
            int size = mMergedFilters.size();
            mFrameBuffers = new int[size - 1];
            mFrameBufferTextures = new int[size - 1];
            for (int i = 0; i < size - 1; i++) {
                IDrawer iDrawer = mMergedFilters.get(i);
                iDrawer.setWorldSize(width, height);

                GLES20.glGenFramebuffers(1, mFrameBuffers, i);
                GLES20.glGenTextures(1, mFrameBufferTextures, i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i], 0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
        }
    }

    public void setProgressTransition(float progress) {
        ((GLTransitionVideo) mMergedFilters.get(0)).setProgress(progress);
    }

    public void setTypeTransition(int typeTransition) {
        ((GLTransitionVideo) mMergedFilters.get(0)).setTypeTransition(typeTransition);
    }

    public void setProcessEffect(float progress) {
        ((GLFilterVideo) mMergedFilters.get(1)).setProgress(progress);
    }

    public void setProcessFilter(float progress) {
        ((BaseGLFilterVideo) mMergedFilters.get(2)).setProgress(progress);
    }

    public void setProcessEffectImage(float progress) {
//        ((GLFilterVideo) mMergedFilters.get(3)).setProgress(progress);
    }


    @SuppressLint("WrongCall")
    public void onDraw(int currentTexture, int nextTexture, int overlayTexture, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        if (mFrameBuffers == null || mFrameBufferTextures == null) {
            return;
        }
        if (mMergedFilters != null) {
            int size = mMergedFilters.size();
            int previousTexture = currentTexture;
            for (int i = 0; i < size; i++) {
                try {
                    IDrawer filter = mMergedFilters.get(i);
                    boolean isNotLast = i < size - 1;
                    if (isNotLast) {
                        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                        GLES20.glClearColor(0, 0, 0, 0);
                    }

                    if (i == 0) {
                        if (filter instanceof GLTransitionVideo) {
                            ((GLTransitionVideo) filter).draw(cubeBuffer, textureBuffer, currentTexture, nextTexture);
                        } else if (filter instanceof GLFilterVideo) {
                            ((GLFilterVideo) filter).draw(cubeBuffer, textureBuffer, previousTexture);
                        }
                    } else if (i == size - 1) {
                        if (filter instanceof GLFilterVideo) {
                            ((GLFilterVideo) filter).draw(cubeBuffer, (size % 2 == 0) ? mGLTextureFlipBuffer : mGLTextureBuffer, currentTexture);
                        }

                    } else {
                        if (filter instanceof GLFilterOverlayVideo) {
                            ((GLFilterOverlayVideo) filter).draw(cubeBuffer, textureBuffer, previousTexture, overlayTexture);
                        }
                        else if (filter instanceof JSToneCurvedVideo) {
                            ((JSToneCurvedVideo) filter).draw(cubeBuffer, textureBuffer, previousTexture);
                        } else if (filter instanceof GLFilterVideo) {
                            ((GLFilterVideo) filter).draw(cubeBuffer, textureBuffer, previousTexture);
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
    private String effectImage = "";
    private String Effect = "";
    private boolean needInit;

    public boolean isNeedInit() {
        return needInit;
    }

    public void setTransitionFragmentShader(String raw) {
        if (!Objects.equals(this.raw, raw)) {
            this.raw = raw;
            needInit = true;
            ((GLTransitionVideo)mMergedFilters.get(0)).setShaderFragment(raw);
        }
    }

    public void setEffectFragmentShader(String filter) {
        if (!Objects.equals(this.Effect, filter)) {
            this.Effect = filter;
            needInit = true;
            ((GLFilterVideo) mMergedFilters.get(1)).setShaderFragment(filter);
        }

    }

    public void setFilterFragmentShader(String filter, BaseGLFilterVideo glFilter) {
        if (!Objects.equals(this.filter, filter)) {
            this.filter = filter;
            needInit = true;
            glFilter.setWorldSize(mOutputWidth, mOutputHeight);
            glFilter.setVideoSize(widthVideo, heightVideo);
            setFilter(2, glFilter);
        }

    }

    public void setEffectImageFragmentShader(Context context, String filter) {
        if (!Objects.equals(this.effectImage, filter)) {
            this.effectImage = filter;
            needInit = true;
//            ((GLFilter) mMergedFilters.get(3)).setFragmentShader(context, filter);
        }
    }


    public IDrawer getFilterWithPosition(int position) {
        return mFilters.get(position);
    }

    public List<IDrawer> getListFilter() {
        return mFilters;
    }

    public List<IDrawer> getMergedFilters() {
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

        List<IDrawer> filters;
        for (IDrawer filter : mFilters) {
            if (filter instanceof TransitionFilterVideoGroup) {
                ((TransitionFilterVideoGroup) filter).updateMergedFilters();
                filters = ((TransitionFilterVideoGroup) filter).getMergedFilters();
                if (filters == null || filters.isEmpty())
                    continue;
                mMergedFilters.addAll(filters);
                continue;
            }
            mMergedFilters.add(filter);
        }
    }

    @Override
    public void setVideoSize(int videoW, int videoH) {

    }

    @Override
    public void setWorldSize(int worldW, int worldH) {

    }

    @Override
    public void release() {

    }

    @Override
    public void getSurfaceTexture(@NonNull Function1<? super SurfaceTexture, Unit> cb) {
//        IDrawer.super.getSurfaceTexture(cb);
    }
}

