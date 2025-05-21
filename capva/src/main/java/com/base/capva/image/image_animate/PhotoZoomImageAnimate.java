package com.base.capva.image.image_animate;

import android.graphics.Canvas;

import com.base.capva.utils.ImageAnimate;

public class PhotoZoomImageAnimate extends BaseImageAnimate {

    private final float maxScale = 1.2f;
    private final float minScale = 1f;

    @Override
    public void prepare() {
        super.prepare();
//        imageView.setAlpha(0f);
//        imageView.setScaleX(maxScale);
//        imageView.setScaleY(maxScale);
    }

    @Override
    protected void animate() {
        float alpha = (Math.min(progress, 0.2f) / 0.2f) * transparentDefault;
        float scale = maxScale - (maxScale - minScale) * progress;
        setProcess(alpha, scale);
    }

    private void setProcess(float alpha, float scale) {
        if (capVaImageView.isVideo() && capVaImageView.getVideoView() != null) {
            capVaImageView.getVideoView().setScaleX(scale);
            capVaImageView.getVideoView().setScaleY(scale);
        }
//        capVaImageView.getImageView().setScaleX(scale);
//        capVaImageView.getImageView().setScaleY(scale);
        capVaImageView.getImageView().setPhotoZoom(scale);

        capVaImageView.setTransparentNotUpdateAnimate(alpha);
//        capVaImageView.getContainer().setScaleX(scale);
//        capVaImageView.getContainer().setScaleY(scale);
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        setProcess(transparentDefault, 1f);
    }

    @Override
    protected void draw(Canvas canvas) {

    }

    @Override
    public int getDuration() {
        return 3000;
    }

    @Override
    public BaseImageAnimate duplicate() {
        return new PhotoZoomImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.PHOTO_ZOOM.name();
    }
}
