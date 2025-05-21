package com.base.capva.text.text_effect;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.util.Log;

import com.base.capva.utils.TextEffect;

public class HollowTextEffect extends BaseTextEffect {

    /**
     * thickness 10-200
     */
    private float thickness = 100;
    private Paint paint;

    @Override
    protected void setupPaint(Paint paint) {
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        setThickness(thickness);
    }

    @Override
    public void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {

    }

    @Override
    public void drawLayout(Canvas canvas, String text, Layout layout) {

    }

    @Override
    public void drawText(Canvas canvas, String text, float left, float baseLine) {

    }

    @Override
    public int getPadding() {
        return 0;
    }

    @Override
    public BaseTextEffect duplicate(Paint paint) {
        HollowTextEffect hollowTextEffect = new HollowTextEffect();
        hollowTextEffect.setupShadow(paint);
        hollowTextEffect.setThickness(thickness);
        return hollowTextEffect;
    }

    @Override
    public float getRoundness() {
        return 0;
    }

    @Override
    public float getSpread() {
        return 0;
    }

    @Override
    public float getTransparency() {
        return 0;
    }

    @Override
    public float getOffset() {
        return 0;
    }

    @Override
    public float getDirection() {
        return 0;
    }

    @Override
    public String getColor() {
        return "";
    }

    @Override
    public String[] getColorGlitch() {
        return new String[0];
    }

    @Override
    public float getThickness() {
        return thickness;
    }

    @Override
    public float getIntensity() {
        return 0;
    }

    @Override
    public float getBlur() {
        return 0;
    }

    public void setThickness(float thickness) {
        this.thickness = Math.max(thickness, 10);
        paint.setStrokeWidth(this.thickness / 100);
    }

    @Override
    public String getName() {
        return TextEffect.HOLLOW.name();
    }
}
