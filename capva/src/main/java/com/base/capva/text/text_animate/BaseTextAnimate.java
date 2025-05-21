package com.base.capva.text.text_animate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.text.Layout;
import android.util.Log;

import com.base.capva.callback.OnElementAnimationListener;
import com.base.capva.common.Common;
import com.base.capva.model.TextGroup;
import com.base.capva.text.CapVaTextView;
import com.base.capva.text.CustomTextView;
import com.base.capva.text.text_effect.BaseTextEffect;
import com.base.capva.utils.MethodUtils;
import com.base.capva.utils.MultiLevelList;
import com.base.capva.utils.ShapeStyle;

import java.util.ArrayList;

public abstract class BaseTextAnimate {

    protected BaseTextEffect textEffect;

    protected MultiLevelList multiLevelList = MultiLevelList.NONE;
    protected ShapeStyle shapeStyle = ShapeStyle.NORMAL;
    protected int transparent = 255;
    protected Paint textPaint;
    protected Paint linePaint;
    protected CapVaTextView capVaTextView;
    protected CustomTextView textView;
    public ValueAnimator valueAnimator;
    protected float progress = 1;
    protected int timeDelay;

    protected Layout layout;
    protected CharSequence text;
    protected float[] gaps;
    protected float sumGap = 0;
    protected ArrayList<TextGroup> textGroups = new ArrayList<>();

    protected boolean lineAfterEnter, afterEnter, afterEnter1, afterEnter2;
    protected int countEnter;
    protected Path circle;
    protected PathMeasure pathMeasure;
    protected float percentCircle = 0.5f;
    protected float radiusCircle = 0, length;
    protected float hOffset, vOffset;
    private boolean circleReverse = false;

    protected PathMeasure measure;
    protected Path circleUnderLine, underLinePath;

    protected Matrix matrix = new Matrix();
    protected Matrix defaultMatrix = new Matrix();
    private OnElementAnimationListener onElementAnimationListener;

    public void init(CapVaTextView capVaTextView, CustomTextView customTextView, OnElementAnimationListener onElementAnimationListener) {
        this.onElementAnimationListener = onElementAnimationListener;
        this.capVaTextView = capVaTextView;
        this.textView = customTextView;
        this.textPaint = textView.getPaint();
        this.circle = new Path();
        this.pathMeasure = new PathMeasure();
        this.linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.linePaint.setColor(textPaint.getColor());
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(MethodUtils.dpToPx(0.6f));
        measure = new PathMeasure();
        circleUnderLine = new Path();
        underLinePath = new Path();
    }

    public void setTextEffect(BaseTextEffect textEffect) {
        this.textEffect = textEffect;
        textView.invalidate();
    }

    public void prepare() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(valueAnimator -> {
                progress = (float) valueAnimator.getAnimatedValue();
                textView.invalidate();
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (onElementAnimationListener != null) {
                        onElementAnimationListener.onElementStopAnimation();
                    }
                }
            });
            valueAnimator.setInterpolator(null);
        }
        progress = 1;
        textPaint.setAlpha(transparent);
        text = textView.getText();
        layout = textView.getLayout();
        if (textEffect != null) {
            textEffect.setupShadow(textPaint);
        }
        calculateGaps();
        if (shapeStyle == ShapeStyle.CIRCLE && percentCircle != 0) {
            setPercentCircle(percentCircle);
        }
    }

    public void startAnimation() {
        progress = 0;
        textView.invalidate();

        if (valueAnimator != null) {
            valueAnimator.setStartDelay(timeDelay);
            valueAnimator.setDuration(getDuration());
            valueAnimator.start();
        }
    }

    public void stopAnimation() {
        if (valueAnimator != null) {
            valueAnimator.end();
        }
        progress = 1;
        textView.invalidate();
    }

    public void setFrameInTime(int time) {
        int timeInAnimation = time - timeDelay;
        if (timeInAnimation >= 0 && getDuration() != 0 && timeInAnimation <= getDuration()) {
            progress = timeInAnimation / (float) getDuration();
            progress = Math.min(progress, 1);
            textView.invalidate();
        } else if (timeInAnimation > getDuration()) {
            if (progress != 1) {
                progress = 1;
                textView.invalidate();
            }
        } else {
            if (progress != 0) {
                progress = 0;
                textView.invalidate();
            }
        }
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
//        valueAnimator.setStartDelay(timeDelay);
    }

    public void setMultiLevelList(MultiLevelList multiLevelList) {
        this.multiLevelList = multiLevelList;
        prepare();
        textView.invalidate();
    }

    private void setTextBoldAndItalic() {
        textPaint.setFakeBoldText(capVaTextView.isBold());
        textPaint.setTextSkewX(capVaTextView.isItalic() ? -0.25f : 0f);
    }

    public void onDraw(Canvas canvas) {
        if (textView == null || capVaTextView == null) {
            return;
        }
        if (textEffect.needSetup()) {
            textEffect.setupShadow(textPaint);
        }
        layout = textView.getLayout();
        text = layout.getText();

        lineAfterEnter = true;
        afterEnter = false;
        afterEnter1 = false;
        afterEnter2 = false;
        countEnter = 0;
        hOffset = 0;
        vOffset = 0;
        setTextBoldAndItalic();
        draw(canvas);

//        if (circle != null) {
//            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            paint.setColor(Color.RED);
//            paint.setStrokeWidth(5);
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawPath(circle, paint);
//        }
    }

    protected void drawUnderLine(Canvas canvas, float startX, float stopX, float baseLine) {
        if (capVaTextView.isUnderline()) {
            canvas.drawLine(startX, baseLine + MethodUtils.dpToPx(1.4f), stopX, baseLine + MethodUtils.dpToPx(1.4f),
                    linePaint);
        }
    }

    protected void drawUnderLineCircle(Canvas canvas, float gapIndex, float d) {
        if (capVaTextView.isUnderline()) {
            circleUnderLine.reset();
            underLinePath.reset();

            circleUnderLine = createCircle(measure, sumGap, textView.getWidth() / 2f, textView.getHeight() / 2f,
                    radiusCircle - MethodUtils.dpToPx(1.4f) + d);
            measure.setPath(circleUnderLine, true);
            float f = measure.getLength() / length;

            measure.getSegment(hOffset * f, hOffset * f + gapIndex, underLinePath, true);
            canvas.drawPath(underLinePath, linePaint);
        }
    }

    public void setShapeStyle(ShapeStyle shapeStyle) {
        this.shapeStyle = shapeStyle;
        if (this.shapeStyle == ShapeStyle.CIRCLE) {
            if (percentCircle != 0) {
                setPercentCircle(percentCircle);
            }
        } else {
            textView.invalidate();
        }
    }

    public ShapeStyle getShapeStyle() {
        return shapeStyle;
    }

    public CustomTextView getTextView() {
        return textView;
    }

    public CapVaTextView getCapVaTextView() {
        return capVaTextView;
    }

    public void setCircleReverse(boolean circleReverse) {
        this.circleReverse = circleReverse;
        if (percentCircle != 0) {
            setPercentCircle(percentCircle);
        }
    }

    public boolean isCircleReverse() {
        return circleReverse;
    }

    public void setPercentCircle(float percentCircle) {
        this.percentCircle = percentCircle;
        float length = sumGap / percentCircle;
        radiusCircle = length / 2f / (float) Math.PI;
        this.circle.reset();
        this.circle = createCircle(pathMeasure, sumGap, textView.getWidth() / 2f, textView.getHeight() / 2f, radiusCircle);
        this.pathMeasure.setPath(circle, true);
        this.length = pathMeasure.getLength();
        textView.invalidate();
    }

    public float getPercentCircle() {
        return percentCircle;
    }

    protected Path createCircle(PathMeasure pathMeasure, float sumGap, float x, float y, float radius) {
        if (circleReverse) {
            if (textView.getLineCount() > 0) {
//                radius += (float) (textView.getHeight() / textView.getLineCount());
//                radius += (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top);
                radius += (textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent) / 2;
            }
        }

        float f = 0.08f;
        Path path = new Path();
        path.moveTo(x - radius, y);
        quadTo(pathMeasure, sumGap, x, y, radius, f, path);
        return path;
    }

    private void quadTo(PathMeasure pathMeasure, float sumGap, float x, float y, float radius, float f, Path path) {
        float x1 = -(radius * f) + (x + radius);
        float y1 = -(radius * f) + (y + radius);

        if (circleReverse) {
            path.quadTo((radius * f) + (x - radius), y1, x, y + radius);
            path.quadTo(x1, y1, x + radius, y);
            path.quadTo(x1, (radius * f) + (y - radius), x, y - radius);
            path.quadTo((radius * f) + (x - radius), (radius * f) + (y - radius), x - radius, y);
        } else {
            path.quadTo((radius * f) + (x - radius), (radius * f) + (y - radius), x, y - radius);
            path.quadTo(x1, (radius * f) + (y - radius), x + radius, y);
            path.quadTo(x1, y1, x, y + radius);
            path.quadTo((radius * f) + (x - radius), y1, x - radius, y);
        }

//        path.quadTo((radius * f) + (x - radius), (radius * f) + (y - radius), x, y - radius);
//        path.quadTo(x1, (radius * f) + (y - radius), x + radius, y);
//        path.quadTo(x1, y1, x, y + radius);
//        path.quadTo((radius * f) + (x - radius), y1, x - radius, y);

        pathMeasure.setPath(path, true);
        if (pathMeasure.getLength() < sumGap) {
            quadTo(pathMeasure, sumGap, x, y, radius, f, path);
        }
    }

    public float getRadiusCircle() {
        return radiusCircle;
    }

    protected void calculateTextGroups(Layout layout, Paint paint) {
        textPaint.setFakeBoldText(false);
        textPaint.setTextSkewX(0f);
        countEnter = 0;
        afterEnter = true;
        String text = layout.getText().toString();
        textGroups = new ArrayList<>();
        for (int i = 0; i < layout.getLineCount(); i++) {
            float lineStart = layout.getLineStart(i);
            float lineEnd = layout.getLineEnd(i);
            float lineBaseline = layout.getLineBaseline(i);
            float lineLeft = layout.getLineLeft(i);
            float lineTop = layout.getLineTop(i);
            float lineBot = layout.getLineBottom(i);
            float ascent = layout.getLineAscent(i);
            float descent = layout.getLineDescent(i);
            int space = 0;
            String textLine = text.substring((int) lineStart, (int) lineEnd);
            if (!textLine.contains(" ")) {
                float gap = textPaint.measureText(textLine);
                TextGroup textGroup = new TextGroup(textLine, lineLeft, gap, lineBaseline, lineTop, lineBot, ascent, descent, i, false);
                addToGroup(textGroup);
            } else {
                for (int j = 0; j < textLine.length(); j++) {
                    if (String.valueOf(textLine.charAt(j)).equals(" ")) {
                        String t = textLine.substring(space, j);
                        if (!t.equals("")) {
                            String textBefore = textLine.substring(0, space);
                            float sLeft = lineLeft + paint.measureText(textBefore);
                            float gap = textPaint.measureText(t + " ");
                            TextGroup textGroup = new TextGroup(t + " ", sLeft, gap, lineBaseline, lineTop, lineBot, ascent, descent, i, false);
                            addToGroup(textGroup);
                        }
                        space = j + 1;

                        String textAfter = textLine.substring(space);
                        if (!textAfter.contains(" ") && !textAfter.equals("")) {
                            String textBeforeEnd = textLine.substring(0, space);
                            float sLeftEnd = lineLeft + paint.measureText(textBeforeEnd);
                            float gap = textPaint.measureText(textAfter);
                            TextGroup textGroupEnd = new TextGroup(textAfter, sLeftEnd, gap, lineBaseline, lineTop, lineBot, ascent, descent, i, false);
                            addToGroup(textGroupEnd);
                        }
                    }
                }
            }
        }
    }

    private void addToGroup(TextGroup textGroup) {
        if (afterEnter) {
            countEnter++;
            switch (multiLevelList) {
                default:
                case NONE:
                    break;
                case DOT:
                    float gap = textPaint.measureText(Common.DOT);
                    textGroups.add(new TextGroup(Common.DOT,
                            -gaps[0],
                            gap,
                            textGroup.getLineBaseline(),
                            textGroup.getLineTop(),
                            textGroup.getLineBot(),
                            textGroup.getAscent(),
                            textGroup.getDescent(),
                            textGroup.getLine(), true));
                    String t = textGroup.getText().substring(1);
                    float l = textGroup.getLineLeft() + gaps[0] / 2;
                    textGroup.setText(t);
                    textGroup.setLineLeft(l);
                    textGroup.setGap(textPaint.measureText(t));
                    break;
                case NUMBER:
                    float leftPre, left;
                    String text;
                    if (countEnter < 10) {
                        leftPre = (-gaps[0] - gaps[1]);
                        left = textGroup.getLineLeft() + (gaps[0] + gaps[1]) / 2;
                        text = textGroup.getText().substring(2);
                    } else {
                        leftPre = (-gaps[0] * 2 - gaps[1]);
                        left = textGroup.getLineLeft() + (gaps[0] * 2 + gaps[1]) / 2;
                        text = textGroup.getText().substring(3);
                    }
                    String t1 = countEnter + ".";
                    float gap1 = textPaint.measureText(t1);
                    textGroups.add(new TextGroup(t1,
                            leftPre,
                            gap1,
                            textGroup.getLineBaseline(),
                            textGroup.getLineTop(),
                            textGroup.getLineBot(),
                            textGroup.getAscent(),
                            textGroup.getDescent(),
                            textGroup.getLine(), true));
                    textGroup.setText(text);
                    textGroup.setLineLeft(left);
                    textGroup.setGap(textPaint.measureText(text));
                    break;
            }
        } else {
            switch (multiLevelList) {
                default:
                case NONE:
                    break;
                case DOT:
                    float l = textGroup.getLineLeft() - gaps[0] / 2;
                    textGroup.setLineLeft(l);
                    break;
                case NUMBER:
                    float left;
                    if (countEnter < 10) {
                        left = textGroup.getLineLeft() - (gaps[0] + gaps[1]) / 2;
                    } else {
                        left = textGroup.getLineLeft() - (gaps[0] * 2 + gaps[1]) / 2;
                    }
                    textGroup.setLineLeft(left);
                    break;
            }
        }
        textGroups.add(textGroup);
        afterEnter = textGroup.getText().contains("\n");
    }

    protected void calculateGaps() {
        if (layout != null) {
            CharSequence text = layout.getText();
            int textLength = text.length();
            gaps = new float[textLength];
            sumGap = 0;
            textPaint.setFakeBoldText(false);
            textPaint.setTextSkewX(0f);
            for (int i = 0; i < textLength; i++) {
                gaps[i] = textPaint.measureText(String.valueOf(text.charAt(i)));
                sumGap += gaps[i];
            }
        }
    }

    public void setTransparent(int transparent) {
        this.transparent = transparent;
        if (textPaint != null) {
            textPaint.setAlpha(transparent);
            textView.invalidate();
        }
    }

    public float getLeftMultiLevelList() {
        switch (multiLevelList) {
            default:
                return 0;
            case DOT:
                return gaps[0];
            case NUMBER:
                return gaps[0] * 2 + gaps[1];
        }
    }

    public void updateDefaultMatrix() {
        defaultMatrix.set(capVaTextView.getMatrix());
    }

    public int getTotalDuration() {
        return timeDelay + getDuration();
    }

    protected abstract void draw(Canvas canvas);

    public abstract int getDuration();

    protected abstract boolean textEffectDrawWithLayout();

    public abstract BaseTextAnimate duplicate();

    public abstract String getName();

//    protected abstract boolean drawCircleWith();
}
