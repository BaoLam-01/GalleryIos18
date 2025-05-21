package com.base.capva.text.text_effect;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.util.Log;

import com.base.capva.utils.TextEffect;

public class NeonTextEffect extends BaseTextEffect {

    /**
     * intensity 0-100
     */
    private float intensity = 100;
    private Paint whitePaint;

    @Override
    protected void setupPaint(Paint paint) {
        if (whitePaint == null) {
            whitePaint = new Paint(paint);
            whitePaint.setColor(Color.WHITE);
            whitePaint.setAntiAlias(true);
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        shadowPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        setIntensity(intensity);
    }

    @Override
    public void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {
        canvas.save();
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawTextOnPath(text, circle, hOffset, vOffset, shadowPaint);
        canvas.restore();
    }

    public void drawOnCircleColorWhite(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {
        canvas.save();
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            whitePaint.setAlpha((int) a);
        } else {
            whitePaint.setAlpha(0);
        }
        canvas.drawTextOnPath(text, circle, hOffset, vOffset, whitePaint);
        canvas.restore();
    }

    @Override
    public void drawLayout(Canvas canvas, String text, Layout layout) {
        canvas.save();
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
        canvas.restore();
    }

    public void drawLayoutColorWhite(Canvas canvas, String text, Layout layout) {
        canvas.save();
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            whitePaint.setAlpha((int) a);
        } else {
            whitePaint.setAlpha(0);
        }
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft = layout.getLineLeft(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            canvas.drawText(lineText, lineLeft, lineBaseline, whitePaint);
        }
        canvas.restore();
    }

    @Override
    public void drawText(Canvas canvas, String text, float left, float baseLine) {
        canvas.save();
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawText(text, left, baseLine, shadowPaint);
        canvas.restore();
    }

    public void drawTextColorWhite(Canvas canvas, String text, float left, float baseLine) {
        canvas.save();
        if (isShowEffect) {
            float defaultAlpha = (int) (intensity / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            whitePaint.setAlpha((int) a);
        } else {
            whitePaint.setAlpha(0);
        }
        canvas.drawText(text, left, baseLine, whitePaint);
        canvas.restore();
    }

    @Override
    public int getPadding() {
        return 10;
    }


    @Override
    public BaseTextEffect duplicate(Paint paint) {
        NeonTextEffect neonTextEffect = new NeonTextEffect();
        neonTextEffect.setupShadow(paint);
        neonTextEffect.setIntensity(intensity);
        return neonTextEffect;
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
        whitePaint.setAlpha((int) (this.intensity / 100f * 255));
    }

    @Override
    public String getName() {
        return TextEffect.NEON.name();
    }
}
