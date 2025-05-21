package com.base.capva.image.image_animate;

import android.graphics.Canvas;

import com.base.capva.utils.ImageAnimate;

public class NoneImageAnimate extends BaseImageAnimate {


    @Override
    protected void animate() {

    }

    @Override
    protected void draw(Canvas canvas) {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public BaseImageAnimate duplicate() {
        return new NoneImageAnimate();
    }

    @Override
    public String getName() {
        return ImageAnimate.NONE.name();
    }
}
