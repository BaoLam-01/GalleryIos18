package com.base.capva.text.text_effect;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;

import com.base.capva.utils.TextEffect;

public class LiftTextEffect extends BaseTextEffect {

    private final int color = Color.parseColor("#B3000000");
    private float intensity = 100;

    @Override
    protected void setupPaint(Paint paint) {
        shadowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        shadowPaint.setColor(color);
        setIntensity(intensity);
    }

    @Override
    public void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawTextOnPath(text, circle, hOffset, vOffset, shadowPaint);
    }

    @Override
    public void drawLayout(Canvas canvas, String text, Layout layout) {
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft = layout.getLineLeft(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            canvas.drawText(lineText, lineLeft, lineBaseline, shadowPaint);
        }
    }

    @Override
    public void drawText(Canvas canvas, String text, float left, float baseLine) {
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawText(text, left, baseLine, shadowPaint);
    }

    @Override
    public int getPadding() {
        return 10;
    }

    @Override
    public BaseTextEffect duplicate(Paint paint) {
        LiftTextEffect liftTextEffect = new LiftTextEffect();
        liftTextEffect.setupShadow(paint);
        liftTextEffect.setIntensity(intensity);
        return liftTextEffect;
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
        return 0;
    }

    @Override
    public float getIntensity() {
        return intensity;
    }

    @Override
    public float getBlur() {
        return 0;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        shadowPaint.setAlpha((int) (intensity / 100f * 255f));
    }

    @Override
    public String getName() {
        return TextEffect.LIFT.name();
    }
}
