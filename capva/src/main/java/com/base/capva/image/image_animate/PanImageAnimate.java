package com.base.capva.image.image_animate;

import android.graphics.Canvas;

import com.base.capva.utils.ImageAnimate;

public class PanImageAnimate extends BaseImageAnimate {

    private final float translate = 50;

    @Override
    public void prepare() {
        super.prepare();
        defaultMatrix.set(capVaImageView.getMatrix());

//        imageView.setAlpha(0f);
//        matrix.set(defaultMatrix);
//        matrix.postTranslate(-translate, 0);
//        capVaImageView.getMatrix().set(matrix);
//        capVaImageView.invalidate();
    }

    @Override
    protected void animate() {
        float trans = -(1 - progress) * translate;
        float alpha = (Math.min(progress, 0.5f) / 0.5f) * transparentDefault;

        setPan(alpha, trans);
    }

    private void setPan(float alpha, float trans) {
//        if (capVaImageView.isVideo() && capVaImageView.getVideoView() != null) {
//            capVaImageView.getVideoView().setAlpha(progress);
//        }
//        imageView.setAlpha(alpha);

//        matrix.set(defaultMatrix);
//        matrix.postTranslate(trans, 0);
//        capVaImageView.getMatrix().set(matrix);
//        capVaImageView.invalidate();

        capVaImageView.setTransparentNotUpdateAnimate(alpha);
        capVaImageView.getContainer().setTranslationX(trans);
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        setPan(transparentDefault, 0);

//        imageView.setAlpha(1f);
//        capVaImageView.getMatrix().set(defaultMatrix);
//        capVaImageView.invalidate();
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
        return new PanImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.PAN.name();
    }
}
