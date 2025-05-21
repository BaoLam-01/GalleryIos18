package com.base.capva.text.text_animate;

import android.graphics.Canvas;

import com.base.capva.common.Common;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;

public class BounceTextAnimate extends BaseTextAnimate {
    private final float mostCount = 7f;
    private float charTime = 500;
    private float s;
    private int timeAnimation;
    private int index;

    @Override
    public void prepare() {
        super.prepare();
        int n = text.length();
        n = n <= 0 ? 1 : n;
        timeAnimation = (int) ((charTime * (mostCount + n - 1)) / mostCount);
        if (timeAnimation > Common.MIN_DURATION_VIDEO) {
            charTime = Math.round((Common.MIN_DURATION_VIDEO * mostCount) / (mostCount + n - 1)) - 2;
            timeAnimation = (int) ((charTime * (mostCount + n - 1)) / mostCount);
        }
        s = (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top) / 7;
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
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            float hMax = getH(1f, i);
            float yMax = lineBaseline - (2 * s - 2 * hMax);

            float hMin = getH(0f, i);
            float yMin = lineBaseline - (2 * s - 2 * hMin);

            float hShow = hMax * 0.2f;
            float yShow = lineBaseline - (2 * s - 2 * hShow);

            for (int c = 0; c < lineText.length(); c++) {
                float h = getH(progress, index);

                float y = lineBaseline - (2 * s - 2 * h);

                float x = (y - yMin) / (yMax - yMin);
                float dy = getInterpolation(x);
                String text = String.valueOf(lineText.charAt(c));

                boolean showEffect;
                if (y > yShow) {
                    textPaint.setAlpha(transparent);
                    linePaint.setAlpha(transparent);
                    showEffect = true;
                } else {
                    textPaint.setAlpha(0);
                    linePaint.setAlpha(0);
                    showEffect = false;
                }

                float d = (10 * s - 10 * hMax * dy);

                if (!textEffectDrawWithLayout()) {
                    textEffect.setShowEffect(showEffect);
                    textEffect.drawOnCircle(canvas, text, circle, hOffset, vOffset - d);
                }
                canvas.drawTextOnPath(text, circle, hOffset, vOffset - d, textPaint);
                drawUnderLineCircle(canvas, gaps[index], d);

                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawOnCircleColorWhite(canvas, text, circle, hOffset, vOffset - d);
                }

                hOffset += gaps[index];
                index++;
            }
        }
    }

    private void drawWithMultiLevelNumber(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float l = gaps[0];
            float l1 = countEnter < 9 ? gaps[1] : gaps[0];//(dem + 1) >= text.length() ? 0 : gaps[dem + 1];
            float l2 = countEnter < 9 ? 0 : gaps[1];//(countEnter < 9) || (dem + 2) >= text.length() ? 0 : gaps[dem + 2];

            float lineLeft;
            if (lineAfterEnter) {
                lineLeft = layout.getLineLeft(i) + (l + l1 + l2) / 2;
            } else {
                lineLeft = layout.getLineLeft(i);
            }
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            lineAfterEnter = lineText.contains("\n");

            float hMax = getH(1f, i);
            float yMax = lineBaseline - (2 * s - 2 * hMax);

            float hMin = getH(0f, i);
            float yMin = lineBaseline - (2 * s - 2 * hMin);

            float hShow = hMax * 0.2f;
            float yShow = lineBaseline - (2 * s - 2 * hShow);

            for (int c = 0; c < lineText.length(); c++) {
                float h = getH(progress, index);

                float y = lineBaseline - (2 * s - 2 * h);

                float x = (y - yMin) / (yMax - yMin);
                float dy = getInterpolation(x);
                float baseLine = lineBaseline - (10 * s - 10 * hMax * dy);
                String text = String.valueOf(lineText.charAt(c));

                boolean showEffect;
                if (y > yShow) {
                    textPaint.setAlpha(transparent);
                    linePaint.setAlpha(transparent);
                    showEffect = true;
                } else {
                    textPaint.setAlpha(0);
                    linePaint.setAlpha(0);
                    showEffect = false;
                }

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
                        textEffect.setShowEffect(showEffect);
                        textEffect.drawText(canvas, text, left, baseLine);
                    }

                    canvas.drawText(text, left, baseLine, textPaint);
                    if (left >= lineLeft) {
                        drawUnderLine(canvas, left, left + gaps[index], baseLine);
                    }

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, left, baseLine);
                    }
                } else {
                    if (!textEffectDrawWithLayout()) {
                        textEffect.setShowEffect(showEffect);
                        textEffect.drawText(canvas, text, lineLeft, baseLine);
                    }
                    canvas.drawText(text, lineLeft, baseLine, textPaint);
                    drawUnderLine(canvas, lineLeft, lineLeft + gaps[index], baseLine);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, baseLine);
                    }

                    lineLeft += gaps[index];
                }
                afterEnter = text.equals("\n");
                index++;
            }
            if (lineAfterEnter) {
                countEnter++;
            }
        }
    }

    private void drawWithMultiLevelDot(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft = lineAfterEnter ? layout.getLineLeft(i) + gaps[0] / 2 : layout.getLineLeft(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            float hMax = getH(1f, i);
            float yMax = lineBaseline - (2 * s - 2 * hMax);

            float hMin = getH(0f, i);
            float yMin = lineBaseline - (2 * s - 2 * hMin);

            float hShow = hMax * 0.2f;
            float yShow = lineBaseline - (2 * s - 2 * hShow);

            for (int c = 0; c < lineText.length(); c++) {
                float h = getH(progress, index);

                float y = lineBaseline - (2 * s - 2 * h);

                float x = (y - yMin) / (yMax - yMin);
                float dy = getInterpolation(x);
                float baseLine = lineBaseline - (10 * s - 10 * hMax * dy);
                String text = String.valueOf(lineText.charAt(c));

                boolean showEffect;
                if (y > yShow) {
                    textPaint.setAlpha(transparent);
                    linePaint.setAlpha(transparent);
                    showEffect = true;
                } else {
                    textPaint.setAlpha(0);
                    linePaint.setAlpha(0);
                    showEffect = false;
                }

                if ((afterEnter || index == 0)) {
                    if (!textEffectDrawWithLayout()) {
                        textEffect.setShowEffect(showEffect);
                        textEffect.drawText(canvas, text, -gaps[0], baseLine);
                    }
                    canvas.drawText(text, -gaps[0], baseLine, textPaint);
//                    drawUnderLine(canvas, -gaps[0], -gaps[0] + gaps[index], baseLine);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, -gaps[0], baseLine);
                    }
                } else {
                    if (!textEffectDrawWithLayout()) {
                        textEffect.setShowEffect(showEffect);
                        textEffect.drawText(canvas, text, lineLeft, baseLine);
                    }
                    canvas.drawText(text, lineLeft, baseLine, textPaint);
                    drawUnderLine(canvas, lineLeft, lineLeft + gaps[index], baseLine);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, baseLine);
                    }

                    lineLeft += gaps[index];
                }
                afterEnter = text.equals("\n");
                index++;
            }
            lineAfterEnter = lineText.contains("\n");
        }
    }

    private void drawDefault(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft = layout.getLineLeft(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            float hMax = getH(1f, i);
            float yMax = lineBaseline - (2 * s - 2 * hMax);

            float hMin = getH(0f, i);
            float yMin = lineBaseline - (2 * s - 2 * hMin);

            float hShow = hMax * 0.2f;
            float yShow = lineBaseline - (2 * s - 2 * hShow);

            for (int c = 0; c < lineText.length(); c++) {
                float h = getH(progress, index);

                float y = lineBaseline - (2 * s - 2 * h);

                float x = (y - yMin) / (yMax - yMin);
                float dy = getInterpolation(x);
                float baseLine = lineBaseline - (10 * s - 10 * hMax * dy);
                String text = String.valueOf(lineText.charAt(c));

                boolean showEffect;
                if (y > yShow) {
                    textPaint.setAlpha(transparent);
                    linePaint.setAlpha(transparent);
                    showEffect = true;
                } else {
                    textPaint.setAlpha(0);
                    linePaint.setAlpha(0);
                    showEffect = false;
                }

                if (!textEffectDrawWithLayout()) {
                    textEffect.setShowEffect(showEffect);
                    textEffect.drawText(canvas, text, lineLeft, baseLine);
                }

                canvas.drawText(text, lineLeft, baseLine, textPaint);
                drawUnderLine(canvas, lineLeft, lineLeft + gaps[index], baseLine);

                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, baseLine);
                }

                lineLeft += gaps[index];
                index++;
            }
        }
    }

    private float getInterpolation(float elapsedTimeRate) {
        return (float) (--elapsedTimeRate * elapsedTimeRate * ((1.7 + 1f) * elapsedTimeRate + 1.7) + 1f);
    }

    private float getH(float progress, int index) {
        float h = (s / charTime * ((progress) * timeAnimation - charTime * index / mostCount));
        if (h > s) {
            h = s;
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
                /* || textEffect instanceof HollowTextEffect*/;
    }

    @Override
    public BaseTextAnimate duplicate() {
        return new BounceTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.BOUNCE.name();
    }
}
