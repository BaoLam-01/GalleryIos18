package com.base.capva.text.text_animate;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import com.base.capva.base.BaseView;
import com.base.capva.common.Common;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.EchoTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.text.text_effect.ShadowTextEffect;
import com.base.capva.text.text_effect.SpliceTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;

public class RollTextAnimate extends BaseTextAnimate {
    private final float mostCount = 5f;
    private float charTime = 500;
    private float heightLine;
    private int timeAnimation;
    private RectF rectF;
    private Path clipPath;
    private int index;
    private float s;

    @Override
    public void prepare() {
        super.prepare();
        clipPath = new Path();
        rectF = new RectF();
        int n = text.length();
        n = n <= 0 ? 1 : n;
        timeAnimation = (int) ((charTime * (mostCount + n - 1)) / mostCount);
        if (timeAnimation > Common.MIN_DURATION_VIDEO) {
            charTime = Math.round((Common.MIN_DURATION_VIDEO * mostCount) / (mostCount + n - 1)) - 2;
            timeAnimation = (int) ((charTime * (mostCount + n - 1)) / mostCount);
        }
        heightLine = (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top);
        textView.invalidate();
    }

    @Override
    protected void draw(Canvas canvas) {
        if (gaps == null) {
            return;
        }

        if (textEffectDrawWithLayout()) {
            textEffect.drawLayout(canvas, text.toString(), layout);
        }

        index = 0;
        s = heightLine + textEffect.getPadding() * 2;

        if (shapeStyle == ShapeStyle.CIRCLE) {
            drawTextOnCircle(canvas);
        } else {
            switch (multiLevelList) {
                default:
                case NONE:
                    drawDefault(canvas);
                    break;
                case DOT:
                    drawWithMultiLevelDot(canvas);
                    break;
                case NUMBER:
                    drawWithMultiLevelNumber(canvas);
                    break;
            }
        }
    }

    private void drawTextOnCircle(Canvas canvas) {
        canvas.save();
        clipPath.reset();
        float radiusClip = radiusCircle - (s) / 2;
        clipPath.addCircle(textView.getWidth() / 2f,
                textView.getHeight() / 2f,
                radiusClip,
                Path.Direction.CW);
        canvas.clipPath(clipPath, Region.Op.DIFFERENCE);

        float radius = radiusCircle + s / 2 + 10;
        clipPath.reset();
        clipPath.addCircle(textView.getWidth() / 2f,
                textView.getHeight() / 2f,
                radius,
                Path.Direction.CW);
        canvas.clipPath(clipPath);

//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            for (int c = 0; c < lineText.length(); c++) {
                float p = getProgress(progress, index);
                if (p <= 0.5f) {
                    float p1 = p / 0.5f;
                    vOffset = -s + 2 * s * p1 - 5 * (1 - p1);
                    if (vOffset > radiusClip) {
                        textPaint.setAlpha(0);
                        if (textEffect instanceof EchoTextEffect || textEffect instanceof ShadowTextEffect || textEffect instanceof SpliceTextEffect) {
                            textEffect.setAlpha(0);
                        }
                    } else {
                        textPaint.setAlpha(transparent);
                        if (textEffect instanceof EchoTextEffect || textEffect instanceof ShadowTextEffect || textEffect instanceof SpliceTextEffect) {
                            textEffect.setAlpha(transparent);
                        }
                    }
                } else {
                    float p1 = (p - 0.5f) / 0.5f;
                    vOffset = -s + s * p1;
                    textPaint.setAlpha(transparent);
                    if (textEffect instanceof EchoTextEffect || textEffect instanceof ShadowTextEffect || textEffect instanceof SpliceTextEffect) {
                        textEffect.setAlpha(transparent);
                    }
                }
                String text = String.valueOf(lineText.charAt(c));

                if (!textEffectDrawWithLayout()) {
                    textEffect.drawOnCircle(canvas, text, circle, hOffset, vOffset);
                }

                canvas.drawTextOnPath(text, circle, hOffset, vOffset, textPaint);
                drawUnderLineCircle(canvas, gaps[index], -vOffset);

                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawOnCircleColorWhite(canvas, text, circle, hOffset, vOffset);
                }

                hOffset += gaps[index];
                index++;
            }
        }
        canvas.restore();
    }

    private void drawWithMultiLevelNumber(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            canvas.save();
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
//            float lineLeft = layout.getLineLeft(i);

            float l = gaps[0];
            float l1 = countEnter < 9 ? gaps[1] : gaps[0];//(dem + 1) >= text.length() ? 0 : gaps[dem + 1];
            float l2 = countEnter < 9 ? 0 : gaps[1];//(countEnter < 9) || (dem + 2) >= text.length() ? 0 : gaps[dem + 2];

            float lineLeft;
            if (lineAfterEnter) {
                lineLeft = layout.getLineLeft(i) + (l + l1 + l2) / 2;
            } else {
                lineLeft = layout.getLineLeft(i);
            }

            float lineTop = layout.getLineTop(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            lineAfterEnter = lineText.contains("\n");

            rectF.left = -BaseView.paddingLeftRight - getLeftMultiLevelList();
            rectF.top = lineTop - textEffect.getPadding();
            rectF.right = textView.getWidth() + BaseView.paddingLeftRight;
            rectF.bottom = lineTop + heightLine + 5 + textEffect.getPadding();
            canvas.clipRect(rectF);

            for (int c = 0; c < lineText.length(); c++) {
                float p = getProgress(progress, index);
                float y;
                if (p <= 0.5f) {
                    float p1 = p / 0.5f;
                    y = lineBaseline - s + 2 * s * p1 - 5 * (1 - p1);
                } else {
                    float p1 = (p - 0.5f) / 0.5f;
                    y = lineBaseline - s + s * p1;
                }

                String text = String.valueOf(lineText.charAt(c));

                if (index >= 2) {
                    afterEnter1 = String.valueOf(this.text.charAt(index - 2)).equals("\n");
                }
                if (!afterEnter1 && index >= 3 && countEnter > 8) {
                    afterEnter2 = String.valueOf(this.text.charAt(index - 3)).equals("\n");
                }

                if (afterEnter || index == 0 || afterEnter1 || index == 1 || afterEnter2) {
                    float left;
                    if (afterEnter || index == 0) {
                        left = -(l + l1 + l2);
                        afterEnter = false;
                    } else if (afterEnter1 || index == 1) {
                        left = -(l1 + l2);
                        afterEnter1 = false;
                    } else {
                        left = -l2;
                        afterEnter2 = false;
                    }

                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, left, y);
                    }

                    canvas.drawText(text, left, y, textPaint);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, left, y);
                    }
                } else {
                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, lineLeft, y);
                    }

                    canvas.drawText(text, lineLeft, y, textPaint);
                    drawUnderLine(canvas, lineLeft, lineLeft + gaps[index], y);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, y);
                    }

                    lineLeft += gaps[index];
                }
                afterEnter = text.equals("\n");
                index++;
            }
            if (lineAfterEnter) {
                countEnter++;
            }
            canvas.restore();
        }
    }

    private void drawWithMultiLevelDot(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            canvas.save();
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
//            float lineLeft = layout.getLineLeft(i);
            float lineLeft = lineAfterEnter ? layout.getLineLeft(i) + gaps[0] / 2 : layout.getLineLeft(i);
            float lineTop = layout.getLineTop(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            rectF.left = -BaseView.paddingLeftRight;
            rectF.top = lineTop - textEffect.getPadding();
            rectF.right = textView.getWidth() + BaseView.paddingLeftRight;
            rectF.bottom = lineTop + heightLine + 5 + textEffect.getPadding();
            canvas.clipRect(rectF);

            for (int c = 0; c < lineText.length(); c++) {
                float p = getProgress(progress, index);
                float y;
                if (p <= 0.5f) {
                    float p1 = p / 0.5f;
                    y = lineBaseline - s + 2 * s * p1 - 5 * (1 - p1);
                } else {
                    float p1 = (p - 0.5f) / 0.5f;
                    y = lineBaseline - s + s * p1;
                }

                String text = String.valueOf(lineText.charAt(c));

                if ((afterEnter || index == 0)) {
                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, -gaps[0], y);
                    }

                    canvas.drawText(text, -gaps[0], y, textPaint);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, -gaps[0], y);
                    }
                } else {
                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, lineLeft, y);
                    }

                    canvas.drawText(text, lineLeft, y, textPaint);
                    drawUnderLine(canvas, lineLeft, lineLeft + gaps[index], y);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, y);
                    }
                    lineLeft += gaps[index];
                }
                afterEnter = text.equals("\n");
                index++;
            }
            lineAfterEnter = lineText.contains("\n");
            canvas.restore();
        }
    }

    private void drawDefault(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            canvas.save();
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft = layout.getLineLeft(i);
            float lineTop = layout.getLineTop(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            rectF.left = -BaseView.paddingLeftRight;
            rectF.top = lineTop - textEffect.getPadding();
            rectF.right = textView.getWidth() + BaseView.paddingLeftRight;
            rectF.bottom = lineTop + heightLine + 5 + textEffect.getPadding();
            canvas.clipRect(rectF);

            for (int c = 0; c < lineText.length(); c++) {
                float p = getProgress(progress, index);
                float y;
                if (p <= 0.5f) {
                    float p1 = p / 0.5f;
                    y = lineBaseline - s + 2 * s * p1 - 5 * (1 - p1);
                } else {
                    float p1 = (p - 0.5f) / 0.5f;
                    y = lineBaseline - s + s * p1;
                }

                String text = String.valueOf(lineText.charAt(c));

                if (!textEffectDrawWithLayout()) {
                    textEffect.drawText(canvas, text, lineLeft, y);
                }

                canvas.drawText(text, lineLeft, y, textPaint);
                drawUnderLine(canvas, lineLeft, lineLeft + gaps[index], y);

                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, y);
                }

                lineLeft += gaps[index];
                index++;
            }
            canvas.restore();
        }
    }

    private float getProgress(float progress, int index) {
        float h = (1 / charTime * ((progress) * timeAnimation - charTime * index / mostCount));
        if (h > 1) {
            h = 1;
        } else if (h < 0) {
            h = 0;
        }
        return h;
    }

    @Override
    public int getDuration() {
        return timeAnimation;
    }

    @Override
    protected boolean textEffectDrawWithLayout() {
        return textEffect instanceof BackgroundTextEffect
                /*|| textEffect instanceof HollowTextEffect*/;
    }

    @Override
    public BaseTextAnimate duplicate() {
        return new RollTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.ROLL.name();
    }
}
