package com.base.capva.image.image_animate;

import android.graphics.Canvas;

import com.base.capva.utils.ImageAnimate;

public class PhotoFlowImageAnimate extends BaseImageAnimate {

    @Override
    public void prepare() {
        super.prepare();
//        imageView.setAlpha(0f);
//        imageView.setScaleX(1.1f);
//        imageView.setScaleY(1.1f);
    }

    @Override
    protected void animate() {
        float alpha = (Math.min(progress, 0.2f) / 0.2f) * transparentDefault;
        float transY = progress * (capVaImageView.getHeight() * 0.05f);
        setPhotoFlow(alpha, transY, 1.1f);
    }

    private void setPhotoFlow(float alpha, float transY, float scale) {
        if (capVaImageView.isVideo() && capVaImageView.getVideoView() != null) {
            capVaImageView.getVideoView().setTranslationY(transY);
            capVaImageView.getVideoView().setScaleX(scale);
            capVaImageView.getVideoView().setScaleY(scale);
        }
//        capVaImageView.getImageView().setTranslationY(transY);
//        capVaImageView.getImageView().setScaleX(scale);
//        capVaImageView.getImageView().setScaleY(scale);
        capVaImageView.getImageView().setPhotoFlow(scale, transY);

        capVaImageView.setTransparentNotUpdateAnimate(alpha);
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        setPhotoFlow(transparentDefault, 0, 1f);
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
        return new PhotoFlowImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.PHOTO_FLOW.name();
    }
}
