package com.base.capva.text.text_effect;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;

import com.base.capva.text.text_animate.BaseTextAnimate;

public abstract class BaseTextEffect {

    protected Paint shadowPaint;
    protected Paint textPaint;
    protected boolean isShowEffect = true;
    protected int alpha = 255;
    protected BaseTextAnimate textAnimate;
    public boolean needSetup() {
        return shadowPaint == null;
    }

    public void setShowEffect(boolean showEffect) {
        isShowEffect = showEffect;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setupShadow(Paint paint) {
        this.textPaint = paint;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        shadowPaint = new Paint(paint);
        shadowPaint.setAntiAlias(true);
        setupPaint(paint);
    }

    public float getOffsetTest(){
        return 0;
    }

    public void setTextAnimate(BaseTextAnimate textAnimate) {
        this.textAnimate = textAnimate;
    }

    protected abstract void setupPaint(Paint paint);

    public abstract void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset);

    public abstract void drawLayout(Canvas canvas, String text, Layout layout);

    public abstract void drawText(Canvas canvas, String text, float left, float baseLine);

    public abstract int getPadding();

    public abstract BaseTextEffect duplicate(Paint paint);

    public abstract float getRoundness();

    public abstract float getSpread();

    public abstract float getTransparency();

    public abstract float getOffset();

    public abstract float getDirection();

    public abstract String getColor();

    public abstract String[] getColorGlitch();

    public abstract float getThickness();

    public abstract float getIntensity();

    public abstract float getBlur();

    public abstract String getName();
}
