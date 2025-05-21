package com.base.capva.text.text_effect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.text.Layout;
import android.util.Log;

import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextEffect;

public class BackgroundTextEffect extends BaseTextEffect {

    private Paint backgroundPaint;
    private String color = "#0491FF";

    /**
     * roundness 0-30
     */
    private float roundness = 15;

    /**
     * spread 0-30
     */
    private float spread = 15;

    /**
     * transparency 0 -100
     */
    private float transparency = 100;

    private Path path;
    private int line = 0;

    @Override
    protected void setupPaint(Paint paint) {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        if (path == null) {
            path = new Path();
        }
        setRoundness(roundness);
        setSpread(spread);
        setColor(color);
        setTransparency(transparency);
        line = 0;
    }

    public void setRoundness(float roundness) {
        this.roundness = Math.min(roundness, 30);
        backgroundPaint.setPathEffect(new CornerPathEffect(this.roundness));
    }

    public void setSpread(float spread) {
        this.spread = Math.min(spread, 30);
        backgroundPaint.setStrokeWidth(this.spread);
    }

    public void setColor(String color) {
        this.color = color;
        backgroundPaint.setColor(Color.parseColor(color));
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
        backgroundPaint.setAlpha((int) (transparency / 100f * 255));
    }

    @Override
    public void drawOnCircle(Canvas canvas, String text, Path circle, float hOffset, float vOffset) {
//        canvas.save();
//
//        float radiusClip = textAnimate.getRadiusCircle() - ((float) textAnimate.getTextView().getHeight() / (float) textAnimate.getTextView().getLineCount()) / 2;
//        path.reset();
//        path.addCircle(textAnimate.getTextView().getWidth() / 2f,
//                textAnimate.getTextView().getHeight() / 2f,
//                radiusClip,
//                Path.Direction.CW);
//        canvas.clipPath(path, Region.Op.DIFFERENCE);
//
//        float radius = textAnimate.getRadiusCircle() + ((float) textAnimate.getTextView().getHeight() / (float) textAnimate.getTextView().getLineCount()) / 2;
//        path.reset();
//        path.addCircle(textAnimate.getTextView().getWidth() / 2f,
//                textAnimate.getTextView().getHeight() / 2f,
//                radius,
//                Path.Direction.CW);
//        backgroundPaint.setAlpha(alpha);
//
//
//        canvas.drawPath(path, backgroundPaint);
//
//        canvas.restore();
    }

    @Override
    public void drawLayout(Canvas canvas, String text, Layout layout) {
        canvas.save();
        path.reset();
        backgroundPaint.setAlpha(alpha);
        if (textAnimate.getShapeStyle() == ShapeStyle.NORMAL) {
            line = 0;
            path.moveTo(0, 0);
            lineToRight(layout, 0);
            lineToLeft(layout, layout.getLineLeft(line) - textAnimate.getLeftMultiLevelList());
        } else {
            float radiusClip = textAnimate.getRadiusCircle() - ((float) textAnimate.getTextView().getHeight() / (float) textAnimate.getTextView().getLineCount()) / 2;
            path.addCircle(textAnimate.getTextView().getWidth() / 2f,
                    textAnimate.getTextView().getHeight() / 2f,
                    radiusClip,
                    Path.Direction.CW);
            canvas.clipPath(path, Region.Op.DIFFERENCE);

            float radius = textAnimate.getRadiusCircle() + ((float) textAnimate.getTextView().getHeight() / (float) textAnimate.getTextView().getLineCount()) / 2;
            path.reset();
            path.addCircle(textAnimate.getTextView().getWidth() / 2f,
                    textAnimate.getTextView().getHeight() / 2f,
                    radius,
                    Path.Direction.CW);
        }
        path.close();

        canvas.drawPath(path, backgroundPaint);
        canvas.restore();
    }

    private void lineToLeft(Layout layout, float xBefore) {
        float lineTop = layout.getLineTop(line);
        path.lineTo(xBefore, lineTop);
        if (line > 0) {
            line--;
            float lineLeft = layout.getLineLeft(line) - textAnimate.getLeftMultiLevelList();
            path.lineTo(lineLeft, lineTop);
            lineToLeft(layout, lineLeft);
        }
    }

    private void lineToRight(Layout layout, float yBefore) {
        float lineRight = layout.getLineRight(line);
        float lineBot = layout.getLineBottom(line);
        path.lineTo(lineRight, yBefore);
        path.lineTo(lineRight, lineBot);
        if (line < layout.getLineCount() - 1) {
            line++;
            lineToRight(layout, lineBot);
        } else {
            float lineLeft = layout.getLineLeft(line) - textAnimate.getLeftMultiLevelList();
            path.lineTo(lineLeft, lineBot);
        }
    }

    @Override
    public void drawText(Canvas canvas, String text, float left, float baseLine) {

    }

    @Override
    public int getPadding() {
        return (int) spread;
//        return 0;
    }

    @Override
    public BaseTextEffect duplicate(Paint paint) {
        BackgroundTextEffect backgroundTextEffect = new BackgroundTextEffect();
        backgroundTextEffect.setupShadow(paint);
        backgroundTextEffect.setColor(color);
        backgroundTextEffect.setRoundness(roundness);
        backgroundTextEffect.setSpread(spread);
        backgroundTextEffect.setTransparency(transparency);
        return backgroundTextEffect;
    }

    @Override
    public float getRoundness() {
        return roundness;
    }

    @Override
    public float getSpread() {
        return spread;
    }

    @Override
    public float getTransparency() {
        return transparency;
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
        return color;
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
        return 0;
    }

    @Override
    public String getName() {
        return TextEffect.BACKGROUND.name();
    }
}
