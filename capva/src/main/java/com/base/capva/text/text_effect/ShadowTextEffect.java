package com.base.capva.text.text_effect;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.Layout;
import android.util.Log;
import android.view.View;

import com.base.capva.utils.MethodUtils;
import com.base.capva.utils.TextEffect;

public class ShadowTextEffect extends BaseTextEffect {

    private String colorShadow = "";
    private int color;

    /**
     * transparency 0 - 100
     */
    private float transparency = 100;

    /**
     * blur 0- 100
     */
    private float blur = 50;

    /**
     * offset 0 - 50
     */
    private float offset = 50;

    /**
     * direction 0 - 360
     */
    private float direction = 90;

    private float dx = 0, dy = 0;
    private Path path;
    private PathMeasure pathMeasure;

    @Override
    protected void setupPaint(Paint paint) {
        if (path == null) {
            path = new Path();
        }
        setColor(colorShadow);
        setBlur(blur);
        setOffset(offset);
        setDirection(direction);
        setTransparency(transparency);
    }

    @Override
    public void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {
        canvas.save();
        if (isShowEffect) {
            float defaultAlpha = (int) (transparency / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawTextOnPath(text, circle, hOffset + dx, vOffset + dy, shadowPaint);
        canvas.restore();
    }

    @Override
    public void drawLayout(Canvas canvas, String text, Layout layout) {
        canvas.save();
        canvas.translate(dx, dy);
        if (isShowEffect) {
            float defaultAlpha = (int) (transparency / 100f * 255f);
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

    @Override
    public void drawText(Canvas canvas, String text, float left, float baseLine) {
        canvas.save();
        if (isShowEffect) {
            float defaultAlpha = (int) (transparency / 100f * 255f);
            float a = defaultAlpha * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.translate(dx, dy);
        canvas.drawText(text, left, baseLine, shadowPaint);
        canvas.restore();
    }

    @Override
    public int getPadding() {
        return (int) (offset / 10f) + (int) (this.blur / 10);
    }

    @Override
    public BaseTextEffect duplicate(Paint paint) {
        ShadowTextEffect shadowTextEffect = new ShadowTextEffect();
        shadowTextEffect.setupShadow(paint);
        shadowTextEffect.setColor(colorShadow);
        shadowTextEffect.setTransparency(transparency);
        shadowTextEffect.setBlur(blur);
        shadowTextEffect.setOffset(offset);
        shadowTextEffect.setDirection(direction);
        return shadowTextEffect;
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
        return transparency;
    }

    @Override
    public float getOffset() {
        return offset;
    }


    @Override
    public float getDirection() {
        return direction;
    }

    @Override
    public String getColor() {
        if (colorShadow.isEmpty()) {
            return MethodUtils.convertColorToString(color);
        }
        return colorShadow;
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
        return 0;
    }

    @Override
    public float getBlur() {
        return blur;
    }

    public void setBlur(float blur) {
        this.blur = blur;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (blur == 0) {
                shadowPaint.setMaskFilter(null);
            } else {
                shadowPaint.setMaskFilter(new BlurMaskFilter(
                        this.blur / 10,
                        BlurMaskFilter.Blur.NORMAL));
            }
        }
    }

    public void setColor(String color) {
        this.colorShadow = color;
        if (color.isEmpty()) {
            this.color = textPaint.getColor();
        } else {
            this.color = Color.parseColor(colorShadow);
        }
        shadowPaint.setColor(this.color);
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
        shadowPaint.setAlpha((int) (transparency / 100 * 255));
    }

    public void setDirection(float direction) {
        this.direction = direction;
        calculateDxDy();
    }

    public void setOffset(float offset) {
        this.offset = offset;
        path.reset();
        path.addCircle(0, 0, this.offset / 10f, Path.Direction.CW);
        pathMeasure = new PathMeasure(path, true);
        calculateDxDy();
    }

    private void calculateDxDy() {
        float[] point = new float[2];
        pathMeasure.getPosTan(getDistance(), point, null);
        dx = point[0];
        dy = point[1];
    }

    private float getDistance() {
        return (float) (Math.PI * (offset / 10) * (direction) / 180f);
    }

    @Override
    public float getOffsetTest() {
        return offset;
    }

    @Override
    public String getName() {
        return TextEffect.SHADOW.name();
    }
}
