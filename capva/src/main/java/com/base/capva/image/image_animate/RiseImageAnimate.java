package com.base.capva.image.image_animate;

import android.graphics.Canvas;
import android.graphics.Matrix;

import com.base.capva.utils.ImageAnimate;

public class RiseImageAnimate extends BaseImageAnimate {

    private final float translate = 50;

    @Override
    public void prepare() {
        super.prepare();
//        defaultMatrix.set(capVaImageView.getMatrix());

//        imageView.setAlpha(0f);
//        matrix.set(defaultMatrix);
//        matrix.postTranslate(0, -translate);
//        capVaImageView.getMatrix().set(matrix);
//        capVaImageView.invalidate();
    }

    @Override
    protected void animate() {
        float trans = (1 - progress) * translate;
        float alpha = (Math.min(progress, 0.5f) / 0.5f) * transparentDefault;
        setAlpha(alpha);

//        matrix.set(defaultMatrix);
//        matrix.postTranslate(0, trans);
//        capVaImageView.getMatrix().set(matrix);
//        capVaImageView.invalidate();

        capVaImageView.getContainer().setTranslationY(trans);
    }

    private void setAlpha(float alpha) {
//        if (capVaImageView.isVideo() && capVaImageView.getVideoView() != null) {
//            capVaImageView.getVideoView().setAlpha(alpha);
//        }
//        imageView.setAlpha(alpha);
        capVaImageView.setTransparentNotUpdateAnimate(alpha);
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        setAlpha(transparentDefault);
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
        return new RiseImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.RISE.name();
    }
}
