package com.base.capva.image.image_animate;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;

import com.base.capva.utils.ImageAnimate;

public class TumbleImageAnimate extends BaseImageAnimate {

    private float s;

    @Override
    public void prepare() {
        super.prepare();
        s = (capVaImageView.getTranslate()[0] + capVaImageView.getWidth() * capVaImageView.getScale()) * 2;
        defaultMatrix.set(capVaImageView.getMatrix());
    }

    @Override
    protected void animate() {
        float trans = -(1 - progress) * s;
        float rotate = (1 - progress) * -120;

        setAlphaRotate(rotate, trans, progress * transparentDefault);

//        matrix.set(defaultMatrix);
//        matrix.postTranslate(trans, 0);
//        capVaImageView.getMatrix().set(matrix);
//        capVaImageView.invalidate();
    }

    private void setAlphaRotate(float rotate, float trans, float alpha) {
//        if (capVaImageView.isVideo() && capVaImageView.getVideoView() != null) {
//            capVaImageView.getVideoView().setRotation(rotate);
//            capVaImageView.getVideoView().setAlpha(alpha);
//        }
//        imageView.setRotation(rotate);
//        imageView.setAlpha(alpha);

        capVaImageView.setTransparentNotUpdateAnimate(alpha);
        capVaImageView.getContainer().setTranslationX(trans);
        capVaImageView.getContainer().setRotation(rotate);
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        setAlphaRotate(0, 0, transparentDefault);
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
        return new TumbleImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.TUMBLE.name();
    }
}
