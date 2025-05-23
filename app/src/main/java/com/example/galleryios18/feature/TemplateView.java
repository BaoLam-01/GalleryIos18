package com.example.galleryios18.feature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.capva.base.BaseView;
import com.base.capva.callback.OnInitTemplateListener;
import com.base.capva.callback.OnLoadThumbListener;
import com.base.capva.callback.OnPreloadFrameListener;
import com.base.capva.common.Common;
import com.base.capva.edit_text.CapVaEditText;
import com.base.capva.image.CapVaImageView;
import com.base.capva.image.image_animate.BaseImageAnimate;
import com.base.capva.image.image_animate.BreatheImageAnimate;
import com.base.capva.image.image_animate.FadeImageAnimate;
import com.base.capva.image.image_animate.NoneImageAnimate;
import com.base.capva.image.image_animate.PanImageAnimate;
import com.base.capva.image.image_animate.PhotoFlowImageAnimate;
import com.base.capva.image.image_animate.PhotoRiseImageAnimate;
import com.base.capva.image.image_animate.PhotoZoomImageAnimate;
import com.base.capva.image.image_animate.RiseImageAnimate;
import com.base.capva.image.image_animate.TumbleImageAnimate;
import com.base.capva.model.AdjustFilter;
import com.base.capva.text.CapVaTextView;
import com.base.capva.text.text_animate.AscendTextAnimate;
import com.base.capva.text.text_animate.BaseTextAnimate;
import com.base.capva.text.text_animate.BlockTextAnimate;
import com.base.capva.text.text_animate.BounceTextAnimate;
import com.base.capva.text.text_animate.BreatheTextAnimate;
import com.base.capva.text.text_animate.BurstTextAnimate;
import com.base.capva.text.text_animate.FadeTextAnimate;
import com.base.capva.text.text_animate.NoneTextAnimate;
import com.base.capva.text.text_animate.PanTextAnimate;
import com.base.capva.text.text_animate.RiseTextAnimate;
import com.base.capva.text.text_animate.RollTextAnimate;
import com.base.capva.text.text_animate.ShiftTextAnimate;
import com.base.capva.text.text_animate.SkateTextAnimate;
import com.base.capva.text.text_animate.TumbleTextAnimate;
import com.base.capva.text.text_animate.TypeWriterTextAnimate;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.BaseTextEffect;
import com.base.capva.text.text_effect.EchoTextEffect;
import com.base.capva.text.text_effect.GlitchTextEffect;
import com.base.capva.text.text_effect.HollowTextEffect;
import com.base.capva.text.text_effect.LiftTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.text.text_effect.NoneTextEffect;
import com.base.capva.text.text_effect.ShadowTextEffect;
import com.base.capva.text.text_effect.SpliceTextEffect;
import com.base.capva.utils.CapVaGravity;
import com.base.capva.utils.ImageAnimate;
import com.base.capva.utils.MethodUtils;
import com.base.capva.utils.TextAnimate;
import com.base.capva.utils.TextEffect;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.galleryios18.data.models.template_item.Adjust;
import com.example.galleryios18.data.models.template_item.Background;
import com.example.galleryios18.data.models.template_item.Crop;
import com.example.galleryios18.data.models.template_item.Effect;
import com.example.galleryios18.data.models.template_item.Filter;
import com.example.galleryios18.data.models.template_item.Flip;
import com.example.galleryios18.data.models.template_item.Format;
import com.example.galleryios18.data.models.template_item.Item;
import com.example.galleryios18.data.models.template_item.Options;
import com.example.galleryios18.data.models.template_item.Shape;
import com.example.galleryios18.data.models.template_item.Spacing;
import com.example.galleryios18.data.models.template_item.Src;
import com.example.galleryios18.data.models.template_item.Style;
import com.example.galleryios18.data.models.template_item.Template;
import com.example.galleryios18.data.models.template_item.TemplateItem;
import com.filter.helper.MagicFilterType;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

public class TemplateView extends RelativeLayout {
    private OnPreloadFrameListener onPreloadFrameListener;
    private OnInitTemplateListener onInitTemplateListener;

    private RequestManager glideManager;

    private final ArrayList<BaseView> views = new ArrayList<>();
    private int countItemNeedPreload = 0;
    private CapVaEditText capVaEditText;
    private CapVaImageView background;

    private Template template;

    private String backgroundAnimate = "";
    private int duration = 5000;
    private boolean isSave = false;

    private int itemCount;
    private int count;
    private final Handler handlerSetTimeDelay = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (itemCount > 0) {
                count++;
                if (count == itemCount) {
                    onInitSuccess();
                }
            } else {
                onInitSuccess();
            }
            super.handleMessage(msg);
        }
    };

    private void onInitSuccess() {
        if (onInitTemplateListener != null) {
            capVaEditText.bringToFront();
            if (!template.getStrJson().equals("")) {
                onInitTemplateListener.onInitSuccess();
            }
            count = 0;
        }
    }

    public TemplateView(Context context) {
        super(context);
        init();
    }

    public TemplateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TemplateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        glideManager = Glide.with(getContext());
        setClipToPadding(false);
        setClipChildren(false);
    }

    public CapVaImageView getBackgroundTemplate() {
        return background;
    }


    public void setOnPreloadFrameListener(OnPreloadFrameListener onPreloadFrameListener) {
        this.onPreloadFrameListener = onPreloadFrameListener;
    }

    public void setOnInitTemplateListener(OnInitTemplateListener onInitTemplateListener) {
        this.onInitTemplateListener = onInitTemplateListener;
    }

    public void setupTemplate(Item item, boolean isSave) {
        itemCount = 0;
        count = 0;
        this.isSave = isSave;
        views.clear();
        removeAllViews();
        setBackgroundColor(Color.RED);
//        this.template = template;
//        setBackgroundAnimate(template.getTemplateItem().getBackground().getAnimate());
//        TemplateItem templateItem = template.getTemplateItem();
//
//        setDuration(templateItem.getBackground().getDuration());
//
//        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        background = new CapVaImageView(getContext(), false, true);
//        addView(background, params);
//
//        background.post(() -> {
//            if (!isAttachedToWindow()) {
//                return;
//            }
//            Background bg = templateItem.getBackground();
//            this.duration = bg.getDuration();
//            Src src = bg.getSrc();
//            if (src.getType().equals(Common.TYPE_IMAGE)) {
//                String uri = src.getSrc();
//                if (uri.startsWith(template.getNameCategory().split(" ")[0])) {
//                    uri = getContext().getFilesDir() + Common.PATH_FOLDER_UNZIP_THEME + File.separator + src.getSrc();
//                }
//                String finalUri = uri;
//                loadThumb(uri, new OnLoadThumbListener() {
//                    @Override
//                    public void onSuccess(Bitmap bitmap) {
//                        background.setMedia(bitmap, finalUri, 0);
//                        Crop crop = bg.getCrop();
//                        if (!crop.isDefault()) {
//                            background.setImageRect(crop.getRect());
//                        }
//                        AdjustFilter adjustFilter = getAdjustFilter(bg.getFilter(), bg.getAdjust());
//                        background.setAdjustFilter(adjustFilter);
//                        background.setFlip(bg.getFlip().getH(), bg.getFlip().getV());
//                        background.setTransparent(bg.getTransparency());
//                    }
//
//                    @Override
//                    public void onFailure() {
//
//                    }
//                });
//            } else if (src.getType().equals(Common.TYPE_COLOR)) {
//                background.setBackgroundColor(src.getSrc());
//            }
//        });
        post(new Runnable() {
            @Override
            public void run() {
                addImage(item);
            }
        });

//        if (itemCount == 0 && !template.getStrJson().equals("")) {
//            handlerSetTimeDelay.sendEmptyMessage(0);
//        }
        if (onPreloadFrameListener != null && countItemNeedPreload == 0) {
            onPreloadFrameListener.onPreloadSuccess();
        }
//
//        capVaEditText = new CapVaEditText(getContext());
//        capVaEditText.setVisibility(INVISIBLE);
//        addView(capVaEditText, params);
    }


    private BaseTextEffect getTextEffect(Style style, Paint paint) {
        Options options = style.getOptions();
        switch (TextEffect.valueOf(style.getType())) {
            default:
            case NONE:
                return new NoneTextEffect();
            case ECHO:
                EchoTextEffect echoTextEffect = new EchoTextEffect();
                echoTextEffect.setupShadow(paint);
                echoTextEffect.setOffset(options.getOffset());
                echoTextEffect.setDirection(options.getDirection());
                echoTextEffect.setColor(options.getColor());
                return echoTextEffect;
            case LIFT:
                LiftTextEffect liftTextEffect = new LiftTextEffect();
                liftTextEffect.setupShadow(paint);
                liftTextEffect.setIntensity(options.getIntensity());
                return liftTextEffect;
            case NEON:
                NeonTextEffect neonTextEffect = new NeonTextEffect();
                neonTextEffect.setupShadow(paint);
                neonTextEffect.setIntensity(options.getIntensity());
                return neonTextEffect;
            case GLITCH:
                GlitchTextEffect glitchTextEffect = new GlitchTextEffect();
                glitchTextEffect.setupShadow(paint);
                glitchTextEffect.setOffset(options.getOffset());
                glitchTextEffect.setDirection(options.getDirection());
                if (options.getColorGlitch() == -1) {
                    glitchTextEffect.setColorGlitch(new String[]{"#00FFF0", "#FE04F4"});
                } else {
                    glitchTextEffect.setColorGlitch(new String[]{"#00FFF0", "#FF0F00"});
                }
                return glitchTextEffect;
            case HOLLOW:
                HollowTextEffect hollowTextEffect = new HollowTextEffect();
                hollowTextEffect.setupShadow(paint);
                hollowTextEffect.setThickness(options.getThickness());
                return hollowTextEffect;
            case SHADOW:
                ShadowTextEffect shadowTextEffect = new ShadowTextEffect();
                shadowTextEffect.setupShadow(paint);
                shadowTextEffect.setOffset(options.getOffset());
                shadowTextEffect.setDirection(options.getDirection());
                shadowTextEffect.setBlur(options.getBlur());
                shadowTextEffect.setTransparency(options.getTransparent());
                shadowTextEffect.setColor(options.getColor());
                return shadowTextEffect;
            case SPLICE:
                SpliceTextEffect spliceTextEffect = new SpliceTextEffect();
                spliceTextEffect.setupShadow(paint);
                spliceTextEffect.setThickness(options.getThickness());
                spliceTextEffect.setOffset(options.getOffset());
                spliceTextEffect.setDirection(options.getDirection());
                spliceTextEffect.setColor(options.getColor());
                return spliceTextEffect;
            case BACKGROUND:
                BackgroundTextEffect backgroundTextEffect = new BackgroundTextEffect();
                backgroundTextEffect.setupShadow(paint);
                backgroundTextEffect.setRoundness(options.getRoundness());
                backgroundTextEffect.setSpread(options.getSpread());
                backgroundTextEffect.setTransparency(options.getTransparent());
                backgroundTextEffect.setColor(options.getColor());
                return backgroundTextEffect;
        }
    }

    private BaseTextAnimate getTextAnimate(String animate) {
        switch (TextAnimate.valueOf(animate)) {
            default:
            case NONE:
                return new NoneTextAnimate();
            case BREATHE:
                return new BreatheTextAnimate();
            case TUMBLE:
                return new TumbleTextAnimate();
            case RISE:
                return new RiseTextAnimate();
            case FADE:
                return new FadeTextAnimate();
            case PAN:
                return new PanTextAnimate();
            case ROLL:
                return new RollTextAnimate();
            case BLOCK:
                return new BlockTextAnimate();
            case BURST:
                return new BurstTextAnimate();
            case SHIFT:
                return new ShiftTextAnimate();
            case SKATE:
                return new SkateTextAnimate();
            case ASCEND:
                return new AscendTextAnimate();
            case BOUNCE:
                return new BounceTextAnimate();
            case TYPE_WRITE:
                return new TypeWriterTextAnimate();
        }
    }

    private void addImage(Item item) {
        CapVaImageView capVaImageView = new CapVaImageView(getContext(), isSave);
        capVaImageView.setVisibility(INVISIBLE);
        int widthImage = (int) (1f * getWidth());
        int heightImage = (int) (1f * getHeight());
        LayoutParams paramsImage = new LayoutParams(widthImage, heightImage);
        paramsImage.bottomMargin = -heightImage;
        paramsImage.rightMargin = -widthImage;
        addView(capVaImageView, paramsImage);
        capVaImageView.post(() -> {
            if (!isAttachedToWindow()) {
                return;
            }
            String uri = item.getSrc();
            Timber.e("LamPro | addImage - uri: " + uri);
//            if (uri.startsWith(template.getNameCategory().split(" ")[0])) {
//                uri = getContext().getFilesDir() + Common.PATH_FOLDER_UNZIP_THEME + File.separator + item.getSrc();
//            }
            String finalUri = uri;
            loadThumb(uri, new OnLoadThumbListener() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    Log.d("chungvv", "onSuccess: "+bitmap);
                    capVaImageView.setMedia(bitmap, finalUri, item.getVideoTimeEnd() - item.getVideoTimeStart());
                    capVaImageView.setTimeVideo(item.getVideoTimeStart(), item.getVideoTimeEnd());

                    capVaImageView.setAnimate(getImageAnimate(item.getAnimate()));
                    capVaImageView.setFolderFrame(item.getFolderFrame(), isSave);
                    capVaImageView.invalidate();
                    capVaImageView.setVisibility(VISIBLE);
                    Timber.e("LamPro | onSuccess - ");
                }

                @Override
                public void onFailure() {
                    Timber.e("LamPro | onFailure - ");
                }
            });
        });

        views.add(capVaImageView);
    }

    private BaseImageAnimate getImageAnimate(String animate) {
        switch (ImageAnimate.valueOf(animate)) {
            case NONE:
                return new NoneImageAnimate();
            case PAN:
                return new PanImageAnimate();
            case FADE:
                return new FadeImageAnimate();
            case RISE:
                return new RiseImageAnimate();
            case TUMBLE:
                return new TumbleImageAnimate();
            case BREATHE:
                return new BreatheImageAnimate();
            case PHOTO_FLOW:
                return new PhotoFlowImageAnimate();
            case PHOTO_RISE:
                return new PhotoRiseImageAnimate();
            case PHOTO_ZOOM:
                return new PhotoZoomImageAnimate();
        }
        return new NoneImageAnimate();
    }

    private int getGravityItem(String gravity) {
        switch (gravity) {
            default:
            case Common.GRAVITY_START:
                return CapVaGravity.START;
            case Common.GRAVITY_CENTER:
                return CapVaGravity.CENTER;
            case Common.GRAVITY_END:
                return CapVaGravity.END;
        }
    }

    private AdjustFilter getAdjustFilter(Filter filter, Adjust adjust) {
        AdjustFilter adjustFilter = new AdjustFilter();
        adjustFilter.setType(MagicFilterType.valueOf(filter.getType()));
        adjustFilter.setIntensity(filter.getIntensity());
        adjustFilter.setBrightness(adjust.getBrightness());
        adjustFilter.setContrast(adjust.getContrast());
        adjustFilter.setExposure(adjust.getExposure());
        adjustFilter.setBrilliance(adjust.getBrilliance());
        adjustFilter.setHighlight(adjust.getHighlight());
        adjustFilter.setShadow(adjust.getShadow());
        adjustFilter.setSharpness(adjust.getSharpness());
        adjustFilter.setTint(adjust.getTint());
        adjustFilter.setBlackPoint(adjust.getBlackPoint());
        adjustFilter.setVibrance(adjust.getVibrance());
        adjustFilter.setSaturation(adjust.getSaturation());
        return adjustFilter;
    }

    private void loadThumb(String uri, OnLoadThumbListener onLoadThumbListener) {
        glideManager.asBitmap().load(uri).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Bitmap bitmap = com.example.galleryios18.utils.MethodUtils.scaleBitmap(resource);
                onLoadThumbListener.onSuccess(bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                onLoadThumbListener.onFailure();
            }
        });
    }


    public CapVaImageView getBackgroudView() {
        return background;
    }

    public void prepareSave() {
        for (int i = 0; i < views.size(); i++) {
            views.get(i).prepare();
        }
        durationPageSave = getDurationPage();
    }

    private int durationPageSave = 0;

    public int getDurationPageSave() {
        return durationPageSave;
    }

    public void setFrameInTime(int time) {
        for (int i = 0; i < views.size(); i++) {
            views.get(i).setFrameInTime(time);
        }
    }

    public void stopAnimation() {
        stopAnimationAllView();
    }

    private void stopAnimationAllView() {
        for (int i = 0; i < views.size(); i++) {
            BaseView baseView = views.get(i);
            baseView.stopAnimation();
        }
    }

    public Template getTemplate() {
        return template;
    }

    public Template getTemplateSave() {
        Template saveTemplate = new Template();
        saveTemplate.setIdTemplate(template.getIdTemplate());
        saveTemplate.setIdCategory(template.getIdCategory());
        saveTemplate.setNameCategory(template.getNameCategory());
        saveTemplate.setTemplateName(template.getTemplateName());
        //todo: save Thumb
        saveTemplate.setThumb("");

        saveTemplate.setRatio(template.getRatio());

        TemplateItem templateItemSave = new TemplateItem();
        templateItemSave.setWidth(template.getTemplateItem().getWidth());
        templateItemSave.setHeight(template.getTemplateItem().getHeight());

        if (getChildCount() > 0) {
            //todo: save background
            CapVaImageView backgroundView = (CapVaImageView) getChildAt(0);
            Background background = new Background();
            Src src = new Src();
            if (!backgroundView.getUri().equals("")) {
                src.setType(Common.TYPE_IMAGE);
                src.setSrc(backgroundView.getUri());
            } else {
                src.setType(Common.TYPE_COLOR);
                src.setSrc(MethodUtils.convertColorToString(backgroundView.getColorBackground()));
            }
            background.setSrc(src);

//            background.setDuration(template.getTemplateItem().getBackground().getDuration());
            background.setDuration(this.duration);

            background.setFilter(getFilterFromView(backgroundView));

            background.setAdjust(getAdjustFromView(backgroundView));

            background.setCrop(getCropFromView(backgroundView));

            background.setFlip(getFlipFromView(backgroundView));

            background.setTransparency(backgroundView.getTransparent());

            background.setAnimate(getBackgroundAnimate());

            templateItemSave.setBackground(background);

            ArrayList<Item> items = new ArrayList<>();
            for (int i = 1; i < getChildCount() - 1; i++) {
                View view = getChildAt(i);
                if (view instanceof CapVaImageView) {
                    Item item = new Item();
                    CapVaImageView capVaImageView = (CapVaImageView) view;

                    item.setWidth((float) capVaImageView.getWidth() / (float) getWidth());
                    item.setHeight((float) capVaImageView.getHeight() / (float) getHeight());

                    item.setVideo(capVaImageView.isVideo());

                    item.setSrc(capVaImageView.getUri());

                    item.setVideoTimeStart(capVaImageView.getTimeStart());

                    item.setVideoTimeEnd(capVaImageView.getTimeEnd());

                    if (capVaImageView.getAnimate() != null)
                        item.setAnimate(capVaImageView.getAnimate().getName());
                    item.setFolderFrame(capVaImageView.getFolderFrame());

                    items.add(item);
                } else if (view instanceof CapVaTextView) {
                    Item item = new Item();
                    CapVaTextView capVaTextView = (CapVaTextView) view;
                    item.setWidth((float) capVaTextView.getWidth());
                    item.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    item.setAnimate(capVaTextView.getAnimate().getName());

                    items.add(item);
                }
            }
            templateItemSave.setItem(items);

            Gson gson = new Gson();
            String json = gson.toJson(templateItemSave);
            saveTemplate.setStrJson(json);
        }
        return saveTemplate;
    }

    @NonNull
    private Effect getEffectFromView(CapVaTextView capVaTextView) {
        Effect effect = new Effect();
        Style style = new Style();
        String styleName = capVaTextView.getTextEffect().getName();
        style.setType(styleName);
        Options options = new Options();
        options.setOffset(capVaTextView.getTextEffect().getOffset());
        options.setDirection(capVaTextView.getTextEffect().getDirection());
        options.setBlur(capVaTextView.getTextEffect().getBlur());
        options.setTransparent(capVaTextView.getTextEffect().getTransparency());
        options.setColor(capVaTextView.getTextEffect().getColor());
        options.setIntensity(capVaTextView.getTextEffect().getIntensity());
        options.setThickness(capVaTextView.getTextEffect().getThickness());
        String[] colorGlitch = capVaTextView.getTextEffect().getColorGlitch();
        if (colorGlitch.length <= 0 || colorGlitch[1].equals("#FE04F4")) {
            options.setColorGlitch(-1);
        } else {
            options.setColorGlitch(0);
        }
        options.setRoundness(capVaTextView.getTextEffect().getRoundness());
        options.setSpread(capVaTextView.getTextEffect().getSpread());
        style.setOptions(options);
        effect.setStyle(style);

        Shape shape = new Shape();
        shape.setType(capVaTextView.getShapeStyle().name());
        shape.setCurve(capVaTextView.getAnimate().getPercentCircle());
        shape.setReverse(capVaTextView.getAnimate().isCircleReverse());
        effect.setShape(shape);
        return effect;
    }

    @NonNull
    private Spacing getSpacingFromView(CapVaTextView capVaTextView) {
        Spacing spacing = new Spacing();
        spacing.setLetter(capVaTextView.getLetterSpacing());
        spacing.setLine(capVaTextView.getLineSpacing());
        return spacing;
    }

    @NonNull
    private Format getFormatFromView(CapVaTextView capVaTextView) {
        Format format = new Format();
        format.setBold(capVaTextView.isBold());
        format.setItalic(capVaTextView.isItalic());
        format.setUnderLine(capVaTextView.isUnderline());
        format.setAllCap(capVaTextView.isAllCap());
        format.setGravity(getGravityFromView(capVaTextView.getGravity()));
        format.setMultilevelList(capVaTextView.getMultiLevelList().name());
        return format;
    }

    private String getGravityFromView(int gravity) {
        switch (gravity) {
            default:
            case CapVaGravity.START:
                return "START";
            case CapVaGravity.CENTER:
                return "CENTER";
            case CapVaGravity.END:
                return "END";
        }
    }

    @NonNull
    private Flip getFlipFromView(CapVaImageView backgroundView) {
        Flip flip = new Flip();
        flip.setH(backgroundView.isFlipH());
        flip.setV(backgroundView.isFlipV());
        return flip;
    }

    @NonNull
    private Crop getCropFromView(CapVaImageView backgroundView) {
        Crop crop = new Crop();
        crop.setLeft(backgroundView.getImageRect().left);
        crop.setTop(backgroundView.getImageRect().top);
        crop.setRight(backgroundView.getImageRect().right);
        crop.setBot(backgroundView.getImageRect().bottom);
        return crop;
    }

    @NonNull
    private Adjust getAdjustFromView(CapVaImageView backgroundView) {
        Adjust adjust = new Adjust();
        adjust.setBrightness(backgroundView.getAdjustFilter().getBrightness());
        adjust.setContrast(backgroundView.getAdjustFilter().getContrast());
        adjust.setExposure(backgroundView.getAdjustFilter().getExposure());
        adjust.setBrilliance(backgroundView.getAdjustFilter().getBrilliance());
        adjust.setHighlight(backgroundView.getAdjustFilter().getHighlight());
        adjust.setShadow(backgroundView.getAdjustFilter().getShadow());
        adjust.setSaturation(backgroundView.getAdjustFilter().getSaturation());
        adjust.setSharpness(backgroundView.getAdjustFilter().getSharpness());
        adjust.setTint(backgroundView.getAdjustFilter().getTint());
        adjust.setBlackPoint(backgroundView.getAdjustFilter().getBlackPoint());
        adjust.setVibrance(backgroundView.getAdjustFilter().getVibrance());
        return adjust;
    }

    @NonNull
    private Filter getFilterFromView(CapVaImageView backgroundView) {
        Filter filter = new Filter();
        filter.setType(backgroundView.getAdjustFilter().getType().toString());
        filter.setIntensity(backgroundView.getAdjustFilter().getIntensity());
        return filter;
    }

    public void setBackgroundColor(String color) {
        background.setBackgroundColor(color);
    }

    public void setBackgroundImage(String uri) {
        background.setUri(uri);
        background.setDuration(0);
        background.setBackgroundColor("#ffffff");
        loadThumb(uri, new OnLoadThumbListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                background.replaceMedia(bitmap, uri, 0);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private int delayPage = 0;
    private int durationPage = -1;

    public int getDelayPage() {
        return delayPage;
    }

    public int setTimeDelay(int delay) {
        stopAnimationAllView();

        delayPage = delay;
        int timeDelay = delay;
        for (int i = 1; i < getChildCount() - 1; i++) {
            BaseView baseView = (BaseView) getChildAt(i);
            baseView.prepare();
            if (baseView instanceof CapVaImageView) {
                BaseImageAnimate baseImageAnimate = ((CapVaImageView) baseView).getAnimate();
                if (baseImageAnimate != null) {
                    baseImageAnimate.setTimeDelay(timeDelay);
                    if (!(baseImageAnimate instanceof NoneImageAnimate)) {
                        timeDelay += Common.TIME_DELAY_ELEMENT;
                    }
                }
            } else if (baseView instanceof CapVaTextView) {
                BaseTextAnimate baseTextAnimate = ((CapVaTextView) baseView).getAnimate();
                if (baseTextAnimate != null) {
                    baseTextAnimate.setTimeDelay(timeDelay);
                    if (!(baseTextAnimate instanceof NoneTextAnimate)) {
                        timeDelay += Common.TIME_DELAY_ELEMENT;
                    }
                }
            }
        }

        int d = 0;
        for (int i = 1; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof CapVaImageView) {
                int tVideo = ((CapVaImageView) v).getTimeEnd() - ((CapVaImageView) v).getTimeStart() + ((CapVaImageView) v).getAnimate().getTimeDelay();
                int tAnimation = ((CapVaImageView) v).getAnimate().getTotalDuration();
                int t = Math.max(tVideo, tAnimation);
                if (t > d) {
                    d = t;
                }
            } else if (v instanceof CapVaTextView) {
                int t = ((CapVaTextView) v).getAnimate().getTotalDuration();
                if (t > d) {
                    d = t;
                }
            }
        }
        startAnimationAfterSetAnimateBackground();
        return d;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public int getDurationPageAnimation() {
        stopAnimationAllView();

        int duration = 0;
        for (int i = 1; i < getChildCount(); i++) {
            View baseView = getChildAt(i);
            if (baseView instanceof CapVaImageView) {
                ((CapVaImageView) baseView).prepare();
                int totalDurationAnimation = ((CapVaImageView) baseView).getAnimate().getTotalDuration();
                if (totalDurationAnimation > 0) {
                    totalDurationAnimation -= delayPage;
                }
                int durationVideo = ((CapVaImageView) baseView).getTimeEnd() - ((CapVaImageView) baseView).getTimeStart() + ((CapVaImageView) baseView).getAnimate().getTimeDelay();
                if (durationVideo > 0) {
                    durationVideo -= delayPage;
                }
                int t = Math.max(durationVideo, totalDurationAnimation);
                if (t > duration) {
                    duration = t;
                }
            } else if (baseView instanceof CapVaTextView) {
                ((CapVaTextView) baseView).prepare();
                int totalDuration = ((CapVaTextView) baseView).getAnimate().getTotalDuration();
                if (totalDuration > 0) {
                    totalDuration -= delayPage;
                }
                if (totalDuration > duration) {
                    duration = totalDuration;
                }
            }
        }
//        durationPage = Math.max(duration, Common.DEFAULT_DURATION_PAGE);
        return Math.max(duration, Common.DEFAULT_DURATION_PAGE);
    }

    public int getDurationPage() {
        stopAnimationAllView();

        int duration = 0;
        for (int i = 1; i < getChildCount(); i++) {
            View baseView = getChildAt(i);
            if (baseView instanceof CapVaImageView) {
                ((CapVaImageView) baseView).prepare();
                int totalDurationAnimation = ((CapVaImageView) baseView).getAnimate().getTotalDuration();
                if (totalDurationAnimation > 0) {
                    totalDurationAnimation -= delayPage;
                }
                int durationVideo = ((CapVaImageView) baseView).getTimeEnd() - ((CapVaImageView) baseView).getTimeStart() + ((CapVaImageView) baseView).getAnimate().getTimeDelay();
                if (durationVideo > 0) {
                    durationVideo -= delayPage;
                }
                int t = Math.max(durationVideo, totalDurationAnimation);
                if (t > duration) {
                    duration = t;
                }
            } else if (baseView instanceof CapVaTextView) {
                ((CapVaTextView) baseView).prepare();
                int totalDuration = ((CapVaTextView) baseView).getAnimate().getTotalDuration();
                if (totalDuration > 0) {
                    totalDuration -= delayPage;
                }
                if (totalDuration > duration) {
                    duration = totalDuration;
                }
            }
        }
//        durationPage = Math.max(duration, Common.DEFAULT_DURATION_PAGE);
        durationPage = Math.max(duration, this.duration);
        return durationPage;
    }

    public String getBackgroundAnimate() {
        return backgroundAnimate;
    }

    public void setBackgroundAnimate(String backgroundAnimate) {
        this.backgroundAnimate = backgroundAnimate;
    }

    public void setBackgroundAnimate(String backgroundAnimate, String textAnimate, String imageAnimate) {
        this.backgroundAnimate = backgroundAnimate;
        for (int i = 1; i < getChildCount(); i++) {
            View baseView = getChildAt(i);
            if (baseView instanceof BaseView) {
                ((BaseView) baseView).stopAnimation();
            }
        }
        //todo: set background animate
        int timeDelay = 0;
        for (int i = 1; i < getChildCount(); i++) {
            View baseView = getChildAt(i);
            if (baseView instanceof CapVaImageView) {
                BaseImageAnimate baseImageAnimate = getImageAnimate(imageAnimate);
                ((CapVaImageView) baseView).setAnimate(baseImageAnimate);
                if (!(baseImageAnimate instanceof NoneImageAnimate)) {
                    ((CapVaImageView) baseView).prepare();
                    ((CapVaImageView) baseView).getAnimate().setTimeDelay(timeDelay);
                    timeDelay += Common.TIME_DELAY_ELEMENT;//((CapVaImageView) baseView).getAnimate().getDuration();
                }
            } else if (baseView instanceof CapVaTextView) {
                BaseTextAnimate baseTextAnimate = getTextAnimate(textAnimate);
                ((CapVaTextView) baseView).setAnimate(baseTextAnimate);
                if (!(baseTextAnimate instanceof NoneTextAnimate)) {
                    ((CapVaTextView) baseView).prepare();
                    ((CapVaTextView) baseView).getAnimate().setTimeDelay(timeDelay);
                    timeDelay += Common.TIME_DELAY_ELEMENT;//((CapVaTextView) baseView).getAnimate().getDuration();
                }
                ((CapVaTextView) baseView).setTransparent(((CapVaTextView) baseView).getTransparent());
            }
        }
        isSetBackgroundAnimate = true;
    }

    private boolean isSetBackgroundAnimate = false;

    private void startAnimationAfterSetAnimateBackground() {
        if (isSetBackgroundAnimate) {
            isSetBackgroundAnimate = false;
            for (int i = 1; i < getChildCount(); i++) {
                View baseView = getChildAt(i);
                if (baseView instanceof BaseView) {
                    ((BaseView) baseView).prepare();
                    ((BaseView) baseView).startAnimation();
                }
            }
        }
    }

}
