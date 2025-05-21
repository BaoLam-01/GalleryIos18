package com.base.capva.image.image_animate;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import com.base.capva.utils.ImageAnimate;

public class BreatheImageAnimate extends BaseImageAnimate {

    private final float maxScale = 1f;
    private final float minScale = 0.8f;

    @Override
    public void prepare() {
        super.prepare();
        centerPoint = capVaImageView.getMappedCenterPoint();
        defaultMatrix.set(capVaImageView.getMatrix());

//        imageView.setAlpha(0f);
//        matrix.set(defaultMatrix);
//        matrix.postScale(minScale, minScale, centerPoint.x, centerPoint.y);
//
//        capVaImageView.getMatrix().set(matrix);
//        capVaImageView.invalidate();
    }

    @Override
    protected void animate() {
        float alpha = (Math.min(progress, 0.2f) / 0.2f) * transparentDefault;
        float scale = minScale + (maxScale - minScale) * progress;
        setBreathe(alpha, scale);
    }

    private void setBreathe(float alpha, float scale) {
//        if (capVaImageView.isVideo() && capVaImageView.getVideoView() != null) {
//            capVaImageView.getVideoView().setAlpha(progress);
//        }
//        imageView.setAlpha(alpha);

//        matrix.set(defaultMatrix);
//        matrix.postScale(scale, scale, centerPoint.x, centerPoint.y);
//        capVaImageView.getMatrix().set(matrix);
//        capVaImageView.invalidate();

        capVaImageView.setTransparentNotUpdateAnimate(alpha);
        capVaImageView.getContainer().setScaleX(scale);
        capVaImageView.getContainer().setScaleY(scale);
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        setBreathe(transparentDefault, 1f);
//        imageView.setAlpha(1f);
//        capVaImageView.getMatrix().set(defaultMatrix);
//        capVaImageView.invalidate();
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
        return new BreatheImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.BREATHE.name();
    }
}
