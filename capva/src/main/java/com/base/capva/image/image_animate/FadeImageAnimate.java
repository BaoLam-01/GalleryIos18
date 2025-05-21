package com.base.capva.image.image_animate;

import android.graphics.Canvas;

import com.base.capva.utils.ImageAnimate;

public class FadeImageAnimate extends BaseImageAnimate {

    @Override
    public void prepare() {
        super.prepare();
//        imageView.setAlpha(0f);
    }

    @Override
    protected void animate() {
        setFade(progress * transparentDefault);
    }

    private void setFade(float progress) {
//        if (capVaImageView.isVideo() && capVaImageView.getVideoView() != null) {
//            capVaImageView.getVideoView().setAlpha(progress);
//        }
//        imageView.setAlpha(progress);
        capVaImageView.setTransparentNotUpdateAnimate(progress);
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        setFade(transparentDefault);
    }

    @Override
    protected void draw(Canvas canvas) {

    }

    @Override
    public int getDuration() {
        return 400;
    }

    @Override
    public BaseImageAnimate duplicate() {
        return new FadeImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.FADE.name();
    }
}
