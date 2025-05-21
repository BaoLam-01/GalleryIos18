package com.base.capva.text.text_effect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.text.Layout;

import com.base.capva.utils.TextEffect;

public class GlitchTextEffect extends BaseTextEffect {

    private String[] colorGlitch = new String[]{"#00FFF0", "#FE04F4"}; //

    /**
     * offset 0 - 50
     */
    private float offset = 50;

    /**
     * direction 0 - 360
     */
    private float direction = 90;

    private float dx = 0, dy = 0;
    private float dx1 = 0, dy1 = 0;

    private Path path, path1;
    private PathMeasure pathMeasure, pathMeasure1;

    @Override
    protected void setupPaint(Paint paint) {
        if (path == null) {
            path = new Path();
        }
        if (path1 == null) {
            path1 = new Path();
        }
        setColorGlitch(colorGlitch);
        setOffset(offset);
        setDirection(direction);
    }

    @Override
    public void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {
        canvas.save();

        shadowPaint.setColor(Color.parseColor(colorGlitch[0]));
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawTextOnPath(text, circle, hOffset + dx, vOffset + dy, shadowPaint);

        canvas.restore();

        canvas.save();
        shadowPaint.setColor(Color.parseColor(colorGlitch[1]));
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawTextOnPath(text, circle, hOffset + dx1, vOffset + dy1, shadowPaint);
        canvas.restore();
    }

    @Override
    public void drawLayout(Canvas canvas, String text, Layout layout) {
        canvas.save();
        canvas.translate(dx, dy);
        shadowPaint.setColor(Color.parseColor(colorGlitch[0]));
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
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

        canvas.save();
        canvas.translate(dx1, dy1);
        shadowPaint.setColor(Color.parseColor(colorGlitch[1]));
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
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
        canvas.translate(dx, dy);
        shadowPaint.setColor(Color.parseColor(colorGlitch[0]));
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawText(text, left, baseLine, shadowPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(dx1, dy1);
        shadowPaint.setColor(Color.parseColor(colorGlitch[1]));
        if (isShowEffect) {
            float a = 255 * (alpha / 255f);
            shadowPaint.setAlpha((int) a);
        } else {
            shadowPaint.setAlpha(0);
        }
        canvas.drawText(text, left, baseLine, shadowPaint);
        canvas.restore();
    }

    @Override
    public int getPadding() {
        return (int) (offset / 10);
    }

    @Override
    public BaseTextEffect duplicate(Paint paint) {
        GlitchTextEffect glitchTextEffect = new GlitchTextEffect();
        glitchTextEffect.setupShadow(paint);
        glitchTextEffect.setColorGlitch(colorGlitch);
        glitchTextEffect.setDirection(direction);
        glitchTextEffect.setOffset(offset);
        return glitchTextEffect;
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
        return "";
    }

    @Override
    public String[] getColorGlitch() {
        return colorGlitch;
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
        return 0;
    }

    public void setColorGlitch(String[] colorGlitch) {
        this.colorGlitch = colorGlitch;
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

        path1.reset();
        path1.addCircle(0, 0, this.offset / 10f, Path.Direction.CW);
        pathMeasure1 = new PathMeasure(path1, true);
        calculateDxDy();
    }

    private void calculateDxDy() {
        float[] point = new float[2];
        pathMeasure.getPosTan(getDistance(), point, null);
        dx = point[0];
        dy = point[1];

        float[] point1 = new float[2];
        pathMeasure1.getPosTan(getDistance1(), point1, null);
        dx1 = point1[0];
        dy1 = point1[1];
    }

    private float getDistance() {
        return (float) (Math.PI * (offset / 10) * (direction) / 180f);
    }

    private float getDistance1() {
        float direction = this.direction + 180;
        if (direction > 360) {
            direction = direction - 360;
        }
        return (float) (Math.PI * (offset / 10) * (direction) / 180f);
    }

    @Override
    public String getName() {
        return TextEffect.GLITCH.name();
    }
}
