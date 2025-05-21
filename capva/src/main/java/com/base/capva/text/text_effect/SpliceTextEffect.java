package com.base.capva.text.text_effect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.text.Layout;

import com.base.capva.utils.MethodUtils;
import com.base.capva.utils.TextEffect;

public class SpliceTextEffect extends BaseTextEffect {

    private String colorShadow = "";
    private int color;

    /**
     * offset 0 - 50
     */
    private float offset = 50;

    /**
     * direction 0 - 360
     */
    private float direction = 90;

    /**
     * thickness 10-200
     */
    private float thickness = 100;


    private float dx = 0, dy = 0;
    private Path path;
    private PathMeasure pathMeasure;
    private Paint paint;

    @Override
    protected void setupPaint(Paint paint) {
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        setThickness(thickness);
        if (path == null) {
            path = new Path();
        }
        setColor(colorShadow);
        setOffset(offset);
        setDirection(direction);
    }

    @Override
    public void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {
        canvas.save();
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
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
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        
        canvas.translate(dx, dy);
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
            float a = 255 * (alpha / 255f);
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
        return (int) (offset / 10);
    }

    @Override
    public BaseTextEffect duplicate(Paint paint) {
        SpliceTextEffect spliceTextEffect = new SpliceTextEffect();
        spliceTextEffect.setupShadow(paint);
        spliceTextEffect.setColor(colorShadow);
        spliceTextEffect.setThickness(thickness);
        spliceTextEffect.setDirection(direction);
        spliceTextEffect.setOffset(offset);
        return spliceTextEffect;
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

    public void setColor(String color) {
        this.colorShadow = color;
        if(colorShadow.isEmpty()){
            this.color = textPaint.getColor();
        } else {
            this.color = Color.parseColor(colorShadow);
        }
        shadowPaint.setColor(this.color);
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
    public String getName() {
        return TextEffect.SPLICE.name();
    }
}
