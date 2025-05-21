package com.base.capva.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.capva.R;
import com.base.capva.base.BaseView;
import com.base.capva.callback.OnElementAnimationListener;
import com.base.capva.common.Common;
import com.base.capva.image.image_animate.BaseImageAnimate;
import com.base.capva.model.AdjustFilter;
import com.base.capva.utils.ActionTouch;
import com.base.capva.utils.AdjustEnum;
import com.base.capva.utils.MatrixUtils;
import com.base.capva.utils.MethodUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.filter.advanced.GPUImageFilterGroup;
import com.filter.advanced.JSNormalFilter;
import com.filter.advanced.JSToneCurved;
import com.filter.advanced.adjust.GPUImageBrightnessFilter;
import com.filter.advanced.adjust.GPUImageContrastFilter;
import com.filter.advanced.adjust.GPUImageExposureFilter;
import com.filter.advanced.adjust.GPUImageGammaFilter;
import com.filter.advanced.adjust.GPUImageHighlightBlackPointFilter;
import com.filter.advanced.adjust.GPUImageHighlightShadowFilter;
import com.filter.advanced.adjust.GPUImageSaturationFilter;
import com.filter.advanced.adjust.GPUImageSharpenFilter;
import com.filter.advanced.adjust.GPUImageVibranceFilter;
import com.filter.advanced.adjust.GPUImageWhiteBalanceFilter;
import com.filter.base.GPUImage;
import com.filter.base.GPUImageFilter;
import com.filter.helper.FilterManager;
import com.filter.helper.MagicFilterType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CapVaImageView extends BaseView {

    private BaseImageAnimate animate;
    private CornerImageView imageView;

    private AdjustFilter adjustFilter;
    private String strBorder = "";
    private Bitmap bmOriginal, bitmap;
    private Bitmap bitmapQueued;
    private BitmapFactory.Options options;
    private String folderFrame = "";
    private String uri = "";
    private int durationVideo;
    private boolean isVideo;
    private SurfaceTexture surfaceTexture;
    private int widthVideoView, heightViewView;
    private int timeStart = 0, timeEnd = Common.MIN_DURATION_VIDEO;
    private TextureView videoView;
    private MediaPlayer mediaPlayer;

    private RectF imageRect, imageRectCenterCrop;
    private RectF viewRect;
    private final Matrix imageScaleMatrix = new Matrix();
    private final Matrix videoScaleMatrix = new Matrix();
    private ActionTouch actionTouch = ActionTouch.NONE;
    private boolean flipH, flipV;
    private ImageView border;
    private GPUImage gpuImage;
    private GPUImageFilterGroup filter;
    private int colorBackground;

    private String clipRound = "";
    private float cornerClip = 0f;
    private final Path clipPath = new Path();
    private final RectF clipRect = new RectF();

    private CustomFrameLayout container;

    public CapVaImageView(Context context, boolean isSave) {
        super(context, isSave);
    }

    public CapVaImageView(Context context, boolean isSave, boolean isBackground) {
        super(context, isSave, isBackground);
    }

    public CapVaImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CapVaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container = new CustomFrameLayout(getContext(), isSave);
        addView(container, params);

        imageView = new CornerImageView(getContext(), isBackground, isSave);
//        imageView = new RoundedImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        container.addView(imageView, params);

        imageRect = new RectF();
        imageRectCenterCrop = new RectF();
        viewRect = new RectF();
        adjustFilter = new AdjustFilter();
    }

    public TextureView getVideoView() {
        return videoView;
    }

    public CustomFrameLayout getContainer() {
        return container;
    }

    public void setBackground(boolean background) {
        isBackground = background;
    }

    public void setBackgroundColor(String color) {
        colorBackground = Color.parseColor(color);
        imageView.setBackgroundColor(colorBackground);
        imageView.setImageBitmap(null);
        bmOriginal = null;
        bitmap = null;
        uri = "";
    }

    public void setBorder(String borderFile) {
        if (borderFile == null)
            return;
        if (!borderFile.equals("")) {
            strBorder = borderFile;
            borderFile = getContext().getFilesDir() + Common.PATH_FOLDER_UNZIP_THEME + File.separator + borderFile;
        }
        if (border == null) {
            border = new ImageView(getContext());
            border.setScaleType(ImageView.ScaleType.FIT_XY);
            LayoutParams paramsBorder = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(border, paramsBorder);
            border.bringToFront();
        }
        Glide.with(getContext()).load(borderFile).into(border);
        clipViewWithBorder();
    }

    private void clipViewWithBorder() {
        if (!strBorder.equals("")) {
            if (strBorder.contains("border_iphone")) {
                Glide.with(getContext()).load(R.drawable.border_iphone).into(border);
                imageView.setClipBounds(new Rect((int) MethodUtils.dpToPx(3), 0, getWidth() - (int) MethodUtils.dpToPx(3), getHeight()));
                if (videoView != null) {
                    videoView.setClipBounds(new Rect((int) MethodUtils.dpToPx(3), 0, getWidth() - (int) MethodUtils.dpToPx(3), getHeight()));
                }
            } else if (strBorder.contains("picture_frames")) {
                imageView.setClipBounds(new Rect((int) MethodUtils.dpToPx(10), (int) MethodUtils.dpToPx(3), getWidth() - (int) MethodUtils.dpToPx(5), getHeight() - (int) MethodUtils.dpToPx(10)));
                if (videoView != null) {
                    videoView.setClipBounds(new Rect((int) MethodUtils.dpToPx(10), (int) MethodUtils.dpToPx(3), getWidth() - (int) MethodUtils.dpToPx(5), getHeight() - (int) MethodUtils.dpToPx(10)));
                }
            } else if (strBorder.contains("border_paper")) {
                if (videoView != null) {
                    videoView.bringToFront();
                }
                imageView.bringToFront();
            } else if (strBorder.contains("circle_white")) {
                Glide.with(getContext()).load(R.drawable.circle_white).into(border);
            } else if (strBorder.contains("defective rectangle bot")) {
                container.setClipRound("defective rectangle bot");
//                imageView.setClipRound("defective rectangle bot");
                setClip("defective rectangle bot");
            } else if (strBorder.contains("defective rectangle top")) {
                container.setClipRound("defective rectangle top");
//                imageView.setClipRound("defective rectangle top");
                setClip("defective rectangle top");
            } else {
                imageView.setClipBounds(new Rect((int) MethodUtils.dpToPx(1), (int) MethodUtils.dpToPx(1), getWidth() - (int) MethodUtils.dpToPx(1), getHeight() - (int) MethodUtils.dpToPx(1)));
                if (videoView != null) {
                    videoView.setClipBounds(new Rect((int) MethodUtils.dpToPx(1), (int) MethodUtils.dpToPx(1), getWidth() - (int) MethodUtils.dpToPx(1), getHeight() - (int) MethodUtils.dpToPx(1)));
                }
            }
        }
    }

    public String getBorder() {
        return strBorder;
    }

    public int getColorBackground() {
        return colorBackground;
    }

    public String getUri() {
        return uri;
    }

    public BaseImageAnimate getAnimate() {
        return animate;
    }

    public AdjustFilter getAdjustFilter() {
        return adjustFilter;
    }

    public void setAdjustFilter(AdjustFilter adjustFilter) {
        this.adjustFilter = adjustFilter;
        //todo: set filter
        setupFilter();
        adjust(AdjustEnum.BRIGHTNESS, adjustFilter.getBrightness());
        adjust(AdjustEnum.CONTRAST, adjustFilter.getContrast());
        adjust(AdjustEnum.EXPOSURE, adjustFilter.getExposure());
        adjust(AdjustEnum.BRILLIANCE, adjustFilter.getBrilliance());
        adjust(AdjustEnum.HIGHLIGHT, adjustFilter.getHighlight());
        adjust(AdjustEnum.SHADOW, adjustFilter.getShadow());
        adjust(AdjustEnum.SATURATION, adjustFilter.getSaturation());
        adjust(AdjustEnum.SHARPNESS, adjustFilter.getSharpness());
        adjust(AdjustEnum.TINT, adjustFilter.getTint());
        adjust(AdjustEnum.BLACK_POINT, adjustFilter.getBlackPoint());
        adjust(AdjustEnum.VIBRANCE, adjustFilter.getVibrance());
        requestRender();
    }

    public int getTimeStart() {
        return timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public void setTimeVideo(int timeStart, int timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    private void setupVideoView() {
        if (videoView != null && container != null) {
            container.removeView(videoView);
        }

//        if (videoView == null) {
        videoView = new TextureView(getContext());
//        videoView.setAlpha(getTransparent());
        clipViewWithBorder();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(videoView, params);
        if (border != null) {
            border.bringToFront();
        }
        videoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                surfaceTexture = surface;
                widthVideoView = width;
                heightViewView = height;
                mediaPlayerSetDataSource();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                surfaceTexture = surface;
                widthVideoView = width;
                heightViewView = height;
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                surfaceTexture = surface;
            }
        });
//        } else {
//            mediaPlayerSetDataSource();
//        }
    }

    private void mediaPlayerSetDataSource() {
        try {
//            String path = "android.resource://" + getContext().getPackageName() + "/" + R.raw.video_test1;
//            Uri uri = Uri.parse(path);
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setVolume(0, 0);
            Surface s = new Surface(surfaceTexture);
            mediaPlayer.setSurface(s);
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
//                videoView.setVisibility(INVISIBLE);
                updateMatrixVideo();
                mediaPlayer.start();
                mediaPlayer.pause();
                imageView.setVisibility(VISIBLE);
                videoView.setVisibility(INVISIBLE);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMatrixVideo() {
//        float[] v = new float[9];
//
//        imageScaleMatrix.getValues(v);

        float[] v = MatrixUtils.getValuesFromMatrix(imageScaleMatrix);
        float imageScaleX = v[Matrix.MSCALE_X];
        float imageSkewY = v[Matrix.MSKEW_Y];
        float imageScale = (float) Math.sqrt(imageScaleX * imageScaleX + imageSkewY * imageSkewY);

        float imageTransX = v[Matrix.MTRANS_X];
        float imageTransY = v[Matrix.MTRANS_Y];

        float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();
        float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
        float scaleX = videoRatio / screenRatio;

        videoScaleMatrix.reset();
        if (scaleX >= 1f) {
            videoScaleMatrix.postScale(scaleX, 1, videoView.getWidth() / 2f, videoView.getHeight() / 2f);
        } else {
            videoScaleMatrix.postScale(1, 1f / scaleX, videoView.getWidth() / 2f, videoView.getHeight() / 2f);
        }

        videoScaleMatrix.getValues(v);
        float scaleX1 = v[Matrix.MSCALE_X];

        float s = mediaPlayer.getVideoWidth() / (scaleX1 * widthVideoView);
        videoScaleMatrix.postScale(s, s, videoView.getWidth() / 2f, videoView.getHeight() / 2f);
        videoScaleMatrix.postScale(imageScale, imageScale, videoView.getWidth() / 2f, videoView.getHeight() / 2f);

        videoScaleMatrix.getValues(v);
        float tx1 = v[Matrix.MTRANS_X];
        float ty1 = v[Matrix.MTRANS_Y];

        videoScaleMatrix.postTranslate(-tx1, -ty1);
        videoScaleMatrix.postTranslate(imageTransX, imageTransY);

        videoScaleMatrix.postScale(flipH ? -1 : 1, flipV ? -1 : 1, videoView.getWidth() / 2f, videoView.getHeight() / 2f);
        videoView.setTransform(videoScaleMatrix);
    }

    public void setupFilter() {
        if (gpuImage == null) {
            initFilter();

            gpuImage = new GPUImage(getContext());
            gpuImage.deleteImage();
            gpuImage.setImage(bmOriginal);
            gpuImage.setFilter(filter);
        }
    }

    private void initFilter() {
        filter = new GPUImageFilterGroup();
        GPUImageFilter gpuImageFilter = FilterManager.getInstance().getFilter(adjustFilter.getType());
        if (!(gpuImageFilter instanceof JSNormalFilter)) {
            ((JSToneCurved) (gpuImageFilter)).setIntensity(adjustFilter.getIntensity());
        }
        filter.addFilter(gpuImageFilter);
    }

    public void setFilter(MagicFilterType type) {
        setupFilter();
        adjustFilter.setType(type);
        adjustFilter.setIntensity(1);

        filter.setFilterIndex(0, FilterManager.getInstance().getFilter(adjustFilter.getType()));
        requestRender();
    }

    public void setIntensity(float intensity) {
        adjustFilter.setIntensity(intensity);
        GPUImageFilter f = filter.getFilterWithPosition(0);
        if (!(f instanceof JSNormalFilter)) {
            ((JSToneCurved) (filter.getFilterWithPosition(0))).setIntensity(intensity);
        }
        requestRender();
    }

    public void adjustFilter(AdjustEnum adjustEnum, float f) {
        setupFilter();
        adjust(adjustEnum, f);
        requestRender();
    }

    private void adjust(AdjustEnum adjustEnum, float f) {
        int position = MethodUtils.getPositionFilter(adjustEnum, filter.getmFilters());
        switch (adjustEnum) {
            case BRIGHTNESS:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_BRIGHTNESS) {
                        GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter(f);
                        filter.addFilter(brightnessFilter);
                    }
                } else {
                    ((GPUImageBrightnessFilter) (filter.getFilterWithPosition(position))).setBrightness(f);
                }
                adjustFilter.setBrightness(f);
                break;
            case CONTRAST:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_CONTRAST) {
                        GPUImageContrastFilter contrastFilter = new GPUImageContrastFilter(f);
                        filter.addFilter(contrastFilter);
                    }
                } else {
                    ((GPUImageContrastFilter) (filter.getFilterWithPosition(position))).setContrast(f);
                }
                adjustFilter.setContrast(f);
                break;
            case EXPOSURE:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_EXPOSURE) {
                        GPUImageExposureFilter exposureFilter = new GPUImageExposureFilter(f);
                        filter.addFilter(exposureFilter);
                    }
                } else {
                    ((GPUImageExposureFilter) (filter.getFilterWithPosition(position))).setExposure(f);
                }
                adjustFilter.setExposure(f);
                break;
            case BRILLIANCE:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_BRILLIANCE) {
                        GPUImageGammaFilter gammaFilter = new GPUImageGammaFilter(f);
                        filter.addFilter(gammaFilter);
                    }
                } else {
                    ((GPUImageGammaFilter) (filter.getFilterWithPosition(position))).setGamma(f);
                }
                adjustFilter.setBrilliance(f);
                break;
            case HIGHLIGHT:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_HIGHLIGHT) {
                        GPUImageHighlightShadowFilter highlightShadowFilter = new GPUImageHighlightShadowFilter();
                        highlightShadowFilter.setHighlights(f);
                        filter.addFilter(highlightShadowFilter);
                    }
                } else {
                    ((GPUImageHighlightShadowFilter) (filter.getFilterWithPosition(position))).setHighlights(f);
                }
                adjustFilter.setHighlight(f);
                break;
            case SHADOW:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_SHADOW) {
                        GPUImageHighlightShadowFilter highlightShadowFilter = new GPUImageHighlightShadowFilter();
                        highlightShadowFilter.setShadows(f);
                        filter.addFilter(highlightShadowFilter);
                    }
                } else {
                    ((GPUImageHighlightShadowFilter) (filter.getFilterWithPosition(position))).setShadows(f);
                }
                adjustFilter.setShadow(f);
                break;
            case SATURATION:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_SATURATION) {
                        GPUImageSaturationFilter saturationFilter = new GPUImageSaturationFilter();
                        saturationFilter.setSaturation(f);
                        filter.addFilter(saturationFilter);
                    }
                } else {
                    ((GPUImageSaturationFilter) (filter.getFilterWithPosition(position))).setSaturation(f);
                }
                adjustFilter.setSaturation(f);
                break;
            case SHARPNESS:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_SHARPNESS) {
                        GPUImageSharpenFilter sharpenFilter = new GPUImageSharpenFilter();
                        sharpenFilter.setSharpness(f);
                        filter.addFilter(sharpenFilter);
                    }
                } else {
                    ((GPUImageSharpenFilter) (filter.getFilterWithPosition(position))).setSharpness(f);
                }
                adjustFilter.setSharpness(f);
                break;
            case TINT:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_TINT) {
                        GPUImageWhiteBalanceFilter tint = new GPUImageWhiteBalanceFilter();
                        tint.setTint(f);
                        filter.addFilter(tint);
                    }
                } else {
                    ((GPUImageWhiteBalanceFilter) (filter.getFilterWithPosition(position))).setTint(f);
                }
                adjustFilter.setTint(f);
                break;
            case BLACK_POINT:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_BLACK_POINT) {
                        GPUImageHighlightBlackPointFilter blackPointFilter = new GPUImageHighlightBlackPointFilter();
                        blackPointFilter.setBlackPoint(f);
                        filter.addFilter(blackPointFilter);
                    }
                } else {
                    ((GPUImageHighlightBlackPointFilter) (filter.getFilterWithPosition(position))).setBlackPoint(f);
                }
                adjustFilter.setBlackPoint(f);
                break;
            case VIBRANCE:
                if (position == -1) {
                    if (f != AdjustFilter.DEFAULT_VIBRANCE) {
                        GPUImageVibranceFilter vibranceFilter = new GPUImageVibranceFilter();
                        vibranceFilter.setVibrance(f);
                        filter.addFilter(vibranceFilter);
                    }
                } else {
                    ((GPUImageVibranceFilter) (filter.getFilterWithPosition(position))).setVibrance(f);
                }
                adjustFilter.setVibrance(f);
                break;
        }
    }

    private void requestRender() {
        gpuImage.setFilter(filter);
        gpuImage.requestRender();
        setBitmapFilter(gpuImage.getBitmapWithFilterApplied());
    }

    public Bitmap getBitmap() {
        return bmOriginal;
    }

    public RectF getImageRect() {
        return imageRect;
    }

    public RectF getImageRectCenterCrop() {
        return imageRectCenterCrop;
    }

    public void setImageRect(RectF imageRect) {
        this.imageRect = imageRect;
        updateMatrix();
    }

    public RectF getViewRect() {
        return viewRect;
    }

    public CornerImageView getImageView() {
        return imageView;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDuration(int duration) {
        this.durationVideo = duration;
        isVideo = durationVideo > 0;
        imageView.setVideo(isVideo);
        if (isVideo) {
            adjustFilter = new AdjustFilter();
            initFilter();
        }
        setTimeVideo();
    }

    public void setMedia(Bitmap bitmap, String uri, int duration/*boolean isVideo*/) {
        this.durationVideo = duration;
        setTimeVideo();
        this.uri = uri;
        this.isVideo = duration > 0;
        this.imageView.setVideo(isVideo);
        this.bmOriginal = bitmap;
        this.bitmap = bitmap;
        setImageMatrix();
        imageView.setImageBitmap(this.bitmap);
        if (isVideo && !isSave) {
            setupVideoView();
        }
    }

    public void replaceMedia(Bitmap bitmap, String uri, int duration) {
        setupFilter();
        this.durationVideo = duration;
        setTimeVideo();
        this.uri = uri;
        this.isVideo = duration > 0;
        this.imageView.setVideo(isVideo);
        this.bmOriginal = bitmap;
        setImageMatrix();
        gpuImage.deleteImage();
        gpuImage.setImage(bmOriginal);
        gpuImage.setFilter(filter);
        setBitmapFilter(gpuImage.getBitmapWithFilterApplied());
        if (isVideo) {
            setupVideoView();
        }
    }

    private void setTimeVideo() {
        if (durationVideo > Common.MIN_DURATION_VIDEO) {
            this.timeStart = 0;
            this.timeEnd = Common.MIN_DURATION_VIDEO;
        } else {
            this.timeStart = 0;
            this.timeEnd = durationVideo;
        }
    }

    private void setBitmapFilter(Bitmap bitmap) {
        this.bitmap = bitmap;
        imageView.setImageBitmap(this.bitmap);
    }

    private void setImageMatrix() {
        if (imageView.getWidth() > 0) {
            float f1 = (float) bmOriginal.getWidth() / imageView.getWidth();
            float f2 = (float) bmOriginal.getHeight() / imageView.getHeight();
            if (f1 > f2) {
                int widthRect = (int) bmOriginal.getHeight() * imageView.getWidth() / imageView.getHeight();
                float left = Math.abs(widthRect - bmOriginal.getWidth()) / 2f;
                imageRect.left = left;
                imageRect.top = 0;
                imageRect.right = left + widthRect;
                imageRect.bottom = bmOriginal.getHeight();
            } else {
                int heightRect = bmOriginal.getWidth() * imageView.getHeight() / imageView.getWidth();
                float top = Math.abs(heightRect - bmOriginal.getHeight()) / 2f;

                imageRect.left = 0;
                imageRect.top = top;
                imageRect.right = bmOriginal.getWidth();
                imageRect.bottom = top + heightRect;
            }
            imageRectCenterCrop = new RectF(imageRect);
            updateMatrix();
        }
    }

    public void setActionTouch(ActionTouch actionTouch) {
        this.actionTouch = actionTouch;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int heightRect, widthRect;
        float leftRect, topRect, rightRect, botRect;
        switch (actionTouch) {
            case SIZE_TOP:
                heightRect = (int) imageRect.width() * imageView.getHeight() / imageView.getWidth();
                topRect = imageRect.bottom - heightRect;
                if (topRect >= 0) {
                    imageRect.top = topRect;
                } else {
                    imageRect.top = 0;
                    float h = imageRect.height();
                    widthRect = (int) h * imageView.getWidth() / imageView.getHeight();
                    leftRect = imageRect.centerX() - widthRect / 2f;
                    imageRect.left = leftRect;
                    imageRect.right = leftRect + widthRect;
                }
                updateMatrix();
                break;

            case SIZE_BOT:
                heightRect = (int) imageRect.width() * imageView.getHeight() / imageView.getWidth();
                botRect = imageRect.top + heightRect;
                if (botRect <= bmOriginal.getHeight()) {
                    imageRect.bottom = botRect;
                } else {
                    imageRect.bottom = bmOriginal.getHeight();
                    float h = imageRect.height();
                    widthRect = (int) h * imageView.getWidth() / imageView.getHeight();
                    leftRect = imageRect.centerX() - widthRect / 2f;
                    imageRect.left = leftRect;
                    imageRect.right = leftRect + widthRect;
                }
                updateMatrix();
                break;

            case SIZE_RIGHT:
                widthRect = (int) imageRect.height() * imageView.getWidth() / imageView.getHeight();
                rightRect = imageRect.left + widthRect;
                if (rightRect <= bmOriginal.getWidth()) {
                    imageRect.right = rightRect;
                } else {
                    imageRect.right = bmOriginal.getWidth();
                    float w = imageRect.width();
                    heightRect = (int) w * imageView.getHeight() / imageView.getWidth();
                    topRect = imageRect.centerY() - heightRect / 2f;
                    imageRect.top = topRect;
                    imageRect.bottom = topRect + heightRect;
                }
                updateMatrix();
                break;

            case SIZE_LEFT:
                widthRect = (int) imageRect.height() * imageView.getWidth() / imageView.getHeight();
                leftRect = imageRect.right - widthRect;
                if (leftRect >= 0) {
                    imageRect.left = leftRect;
                } else {
                    imageRect.left = 0;
                    float w = imageRect.width();
                    heightRect = (int) w * imageView.getHeight() / imageView.getWidth();
                    topRect = imageRect.centerY() - heightRect / 2f;
                    imageRect.top = topRect;
                    imageRect.bottom = topRect + heightRect;
                }
                updateMatrix();
                break;

            case NONE:
            default:
                break;
        }
    }

    public void cropRect(RectF crop) {
        imageRect.set(crop);
        updateMatrix();
    }

    private void updateMatrix() {
        viewRect.left = 0;
        viewRect.top = 0;
        viewRect.right = imageView.getWidth();
        viewRect.bottom = imageView.getHeight();
        imageScaleMatrix.setRectToRect(imageRect, viewRect, Matrix.ScaleToFit.CENTER);
        imageView.setImageMatrix(imageScaleMatrix);
        imageView.requestLayout();
    }

    public void setAnimate(BaseImageAnimate animate) {
        this.animate = animate;
        this.animate.init(CapVaImageView.this, onCapVaImageListener);
    }

    @Override
    public void prepare() {
        if (animate != null) {
            animate.prepare();
        }
    }

    @Override
    public void startAnimation() {
        if (animate != null) {
            animate.startAnimation();
        }
        startMedia();
    }

    @Override
    public void stopAnimation() {
        if (animate != null) {
            animate.stopAnimation();
        }
        stopMedia();
    }

    private final Handler handlerPlayMedia = new Handler(Looper.getMainLooper());
    private final Runnable runnablePlayMedia = new Runnable() {
        @Override
        public void run() {
            if (!isAttachedToWindow()) {
                return;
            }
            seekTo(timeStart);
        }
    };

    public void startMediaInTimeSelected() {
        handlerPlayMedia.removeCallbacks(runnablePlayMedia);
        startMedia();
        handlerPlayMedia.postDelayed(runnablePlayMedia, timeEnd - timeStart);
    }

    public void startMedia() {
        if (mediaPlayer != null && videoView != null && isVideo) {
            mediaPlayer.seekTo(timeStart);
            mediaPlayer.start();
            videoView.setVisibility(VISIBLE);
            imageView.setVisibility(INVISIBLE);
            if (onCapVaImageListener != null) {
                onCapVaImageListener.onMediaPlaying(true);
            }
        }
    }

    public void stopMedia() {
        handlerPlayMedia.removeCallbacks(runnablePlayMedia);
        if (mediaPlayer != null && videoView != null && isVideo) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(timeStart);
            }
            videoView.setVisibility(INVISIBLE);
            imageView.setVisibility(VISIBLE);
            if (onCapVaImageListener != null) {
                onCapVaImageListener.onMediaPlaying(false);
            }
        }
    }

    public void seekTo(int time) {
        handlerPlayMedia.removeCallbacks(runnablePlayMedia);
        if (mediaPlayer != null && videoView != null && isVideo) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            mediaPlayer.seekTo(time);
            videoView.setVisibility(VISIBLE);
            imageView.setVisibility(INVISIBLE);
            if (onCapVaImageListener != null) {
                onCapVaImageListener.onMediaPlaying(false);
            }
        }
    }

    @Override
    public void setFrameInTime(int time) {
        if (animate != null) {
            animate.setFrameInTime(time);
            if (isVideo && frames.size() > 0) {

                int timeInAnimation = time - animate.getTimeDelay();
                if (timeInAnimation >= 0) {
                    int count = (int) ((float) timeInAnimation / (float) (timeEnd - timeStart) * frames.size());
                    if (count >= frames.size()) {
                        return;
                    }
                    Bitmap bitmap = decodeBitmapWithOptimize(frames.get(count));
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,bmOriginal.getWidth(),bmOriginal.getHeight(),true));
//                    Glide.with(getContext()).asBitmap().load(frames.get(count))
//                            .override(bmOriginal.getWidth(), bmOriginal.getHeight())
//                            .into(new CustomTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                    Log.e("HaiPd", "onResourceReady: "+count );
//                                    imageView.setImageBitmap(resource);
//                                }
//
//                                @Override
//                                public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                                }
//                            });
//                    count++;
                }
            }
        }
    }

    public Bitmap decodeBitmapWithOptimize(String path) {
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        if (bitmapQueued != null) {
            options.inBitmap = bitmapQueued;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmapQueued == null) {
            bitmapQueued = bitmap;
        }
        return bitmap;
    }

    @Override
    public void setTimeDelay(int timeDelay) {
        if (animate != null) {
            animate.setTimeDelay(timeDelay);
        }
    }

    public void setClip(String type) {
        this.clipRound = type;
        updateClipView();
    }

    public String getClip() {
        return clipRound;
    }

    public void setCornerClip(float cornerClip) {
//        imageView.setCornerClip(cornerClip);
        this.cornerClip = cornerClip;
        updateClipView();
    }

    private void updateClipView() {
        imageView.setClip(cornerClip, clipRound);
        container.setClip(cornerClip, clipRound);
//        imageView.setCornerRadius(cornerClip);
    }

    public float getCornerClip() {
        return cornerClip;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        if (animate != null) {
            animate.onDraw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    private void clip(Canvas canvas) {
        if (clipRound.equals("CIRCLE")) {
            clipPath.reset();
            float radius = Math.min(getWidth(), getHeight()) * getScale() / 2f;
            clipPath.addCircle(getMappedCenterPoint().x, getMappedCenterPoint().y, radius, Path.Direction.CW);
            canvas.clipPath(clipPath);
        } else if (clipRound.equals("OVAL")) {
            clipPath.reset();
            float[] src = new float[]{MethodUtils.dpToPx(2), MethodUtils.dpToPx(2), getWidth(), getHeight()};
            float[] point = getMappedPoints(src);
            clipPath.addOval(point[0], point[1], point[2], point[3], Path.Direction.CW);
            canvas.clipPath(clipPath);
        } else if (cornerClip != 0) {
            clipPath.reset();
            float[] src = new float[]{
                    0, cornerClip,
                    0, 0,
                    cornerClip, 0,
                    getWidth() - cornerClip, 0,
                    getWidth(), 0,
                    getWidth(), cornerClip,
                    getWidth(), getHeight() - cornerClip,
                    getWidth(), getHeight(),
                    getWidth() - cornerClip, getHeight(),
                    cornerClip, getHeight(),
                    0, getHeight(),
                    0, getHeight() - cornerClip
            };

            float[] point = getMappedPoints(src);
            clipPath.moveTo(point[0], point[1]);
            clipPath.cubicTo(point[0], point[1], point[2], point[3], point[4], point[5]);
            clipPath.lineTo(point[6], point[7]);
            clipPath.cubicTo(point[6], point[7], point[8], point[9], point[10], point[11]);
            clipPath.lineTo(point[12], point[13]);
            clipPath.cubicTo(point[12], point[13], point[14], point[15], point[16], point[17]);
            clipPath.lineTo(point[18], point[19]);
            clipPath.cubicTo(point[18], point[19], point[20], point[21], point[22], point[23]);
            clipPath.close();

            canvas.clipPath(clipPath);
        }
    }

    public void setTransparentNotUpdateAnimate(float transparent) {
        container.setAlpha(transparent);
    }

    public void setTransparent(float transparent) {
//        imageView.setAlpha(transparent);
//        if (videoView != null) {
//            videoView.setAlpha(transparent);
//        }
        container.setAlpha(transparent);
        if (animate != null) {
            animate.setTransparentDefault(transparent);
        }

    }

    public float getTransparent() {
        return container.getAlpha();
    }

    public void setFlip(boolean h, boolean v) {
        this.flipH = h;
        this.flipV = v;
        bmOriginal = createFlippedBitmap(bmOriginal, flipH, flipV);
    }

    public boolean isFlipH() {
        return flipH;
    }

    public boolean isFlipV() {
        return flipV;
    }

    public void flipH() {
        flipH = !flipH;
//        scaleMatrix.postScale(-1, 1, imageView.getWidth() / 2f, imageView.getHeight() / 2f);
//        imageView.setImageMatrix(scaleMatrix);
        bmOriginal = createFlippedBitmap(bmOriginal, true, false);
        imageView.setImageBitmap(bmOriginal);
    }

    public void flipV() {
        flipV = !flipV;
//        scaleMatrix.postScale(1, -1, imageView.getWidth() / 2f, imageView.getHeight() / 2f);
//        imageView.setImageMatrix(scaleMatrix);
        bmOriginal = createFlippedBitmap(bmOriginal, false, true);
        imageView.setImageBitmap(bmOriginal);
    }

    private Bitmap createFlippedBitmap(Bitmap source, boolean h, boolean v) {
        Matrix matrix = new Matrix();
        matrix.postScale(h ? -1 : 1, v ? -1 : 1, source.getWidth() / 2f, source.getHeight() / 2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private ArrayList<String> frames = new ArrayList<>();
    private int countFrame = 0;
    private int countFrameLoaded = 0;

    public void setFolderFrame(String path, boolean needPreLoad) {
        this.folderFrame = path;
        frames.clear();
        File file = new File(path);
        File[] listFile = file.listFiles();
        if (listFile != null && listFile.length > 0) {
            countFrame = listFile.length;
            countFrameLoaded = 0;
            for (int i = 0; i < listFile.length; i++) {
                String nameFile;

                if (i < 9) {
                    nameFile = "frame_000" + (i + 1) + ".jpeg";
                } else if (i < 99) {
                    nameFile = "frame_00" + (i + 1) + ".jpeg";
                } else if (i < 999) {
                    nameFile = "frame_0" + (i + 1) + ".jpeg";
                } else {
                    nameFile = "frame_" + (i + 1) + ".jpeg";
                }
                String pathFrame = folderFrame + "/" + nameFile;
                frames.add(pathFrame);
                if (needPreLoad) {
                    Glide.with(getContext()).load(pathFrame).override(bmOriginal.getWidth(), bmOriginal.getHeight()).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            countFrameLoaded++;
                            if (countFrameLoaded == countFrame && onCapVaImageListener != null) {
                                onCapVaImageListener.onPreloadFrameSuccess();
                            }

                            return false;
                        }
                    }).preload();
                }
            }
        } else {
            onCapVaImageListener.onPreloadFrameSuccess();
        }
    }

    public String getFolderFrame() {
        return folderFrame;
    }

    private OnCapVaImageListener onCapVaImageListener;

    public void setOnCapVaImageListener(OnCapVaImageListener capVaImageListener) {
        this.onCapVaImageListener = capVaImageListener;
    }

    public interface OnCapVaImageListener extends OnElementAnimationListener {
        void onMediaPlaying(boolean isPlay);

        void onPreloadFrameSuccess();
    }
}
