package com.base.capva.text.text_animate;

import android.graphics.Canvas;
import android.graphics.PathMeasure;

import com.base.capva.common.Common;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;

public class SkateTextAnimate extends BaseTextAnimate {
    private final float mostCount = 7f;
    private float charTime = 400;
    private int timeAnimation;
    private int maxRotate = 45;
    private int showRotate = 30;
    private int maxTranslate = 10;
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
        PathMeasure measure = new PathMeasure();
        createCircle(measure,
                0,
                textView.getWidth() / 2f,
                textView.getHeight() / 2f,
                radiusCircle + (layout.getLineBaseline(0) - layout.getLineTop(0)));

        float f = measure.getLength() / pathMeasure.getLength();
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            for (int c = 0; c < lineText.length(); c++) {
                canvas.save();
                float rotate = getRotate(progress, index) - maxRotate;
                float translate = maxTranslate - getTranslate(progress, index);
                if (rotate > -showRotate) {
                    textPaint.setAlpha(transparent);
                    textEffect.setAlpha(transparent);
                } else {
                    textPaint.setAlpha(0);
                    textEffect.setAlpha(textEffectDrawWithLayout() ? transparent : 0);
                }

                float px = hOffset + translate + gaps[index] / 2;

                float[] p = new float[2];
                measure.getPosTan(px * f, p, null);

                canvas.rotate(rotate, p[0], p[1]);

                String text = String.valueOf(lineText.charAt(c));

                if (!textEffectDrawWithLayout()) {
                    textEffect.drawOnCircle(canvas, text, circle, hOffset, vOffset);
                }

                canvas.drawTextOnPath(text, circle, hOffset, vOffset, textPaint);
                drawUnderLineCircle(canvas, gaps[index], 0);

                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawOnCircleColorWhite(canvas, text, circle, hOffset, vOffset);
                }

                hOffset += gaps[index];
                index++;
                canvas.restore();
            }
        }
    }

    private void drawWithMultiLevelNumber(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineTop = layout.getLineTop(i);

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
            for (int c = 0; c < lineText.length(); c++) {
                canvas.save();
                float rotate = getRotate(progress, index) - maxRotate;
                float translate = maxTranslate - getTranslate(progress, index);
                if (rotate > -showRotate) {
                    textPaint.setAlpha(transparent);
                    textEffect.setAlpha(transparent);
                } else {
                    textPaint.setAlpha(0);
                    textEffect.setAlpha(textEffectDrawWithLayout() ? transparent : 0);
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
                    canvas.rotate(rotate, left + translate + gaps[index] / 2, lineTop);

                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, left, lineBaseline);
                    }

                    canvas.drawText(text, left, lineBaseline, textPaint);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, left, lineBaseline);
                    }
                } else {
                    canvas.rotate(rotate, lineLeft + translate + gaps[index] / 2, lineTop);

                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, lineLeft, lineBaseline);
                    }
                    canvas.drawText(text, lineLeft, lineBaseline, textPaint);
                    drawUnderLine(canvas, lineLeft, lineLeft + gaps[index], lineBaseline);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, lineBaseline);
                    }
                    lineLeft += gaps[index];
                }
                index++;
                afterEnter = text.equals("\n");
                canvas.restore();
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
            float lineTop = layout.getLineTop(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            for (int c = 0; c < lineText.length(); c++) {
                canvas.save();
                float rotate = getRotate(progress, index) - maxRotate;
                float translate = maxTranslate - getTranslate(progress, index);
                if (rotate > -showRotate) {
                    textPaint.setAlpha(transparent);
                    textEffect.setAlpha(transparent);
                    linePaint.setAlpha(transparent);
                } else {
                    textPaint.setAlpha(0);
                    linePaint.setAlpha(0);
                    textEffect.setAlpha(textEffectDrawWithLayout() ? transparent : 0);
                }


                String text = String.valueOf(lineText.charAt(c));

                if ((afterEnter || index == 0)) {
                    canvas.rotate(rotate, -gaps[0] + translate + gaps[index] / 2, lineTop);

                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, -gaps[0], lineBaseline);
                    }

                    canvas.drawText(text, -gaps[0], lineBaseline, textPaint);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, -gaps[0], lineBaseline);
                    }
                } else {
                    canvas.rotate(rotate, lineLeft + translate + gaps[index] / 2, lineTop);

                    if (!textEffectDrawWithLayout()) {
                        textEffect.drawText(canvas, text, lineLeft + translate, lineBaseline);
                    }

                    canvas.drawText(text, lineLeft + translate, lineBaseline, textPaint);
                    drawUnderLine(canvas, lineLeft + translate, lineLeft + translate + gaps[index], lineBaseline);

                    if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                        ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft + translate, lineBaseline);
                    }

                    lineLeft += gaps[index];
                }

                index++;
                afterEnter = text.equals("\n");
                canvas.restore();
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
            float lineTop = layout.getLineTop(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            for (int c = 0; c < lineText.length(); c++) {
                canvas.save();
                float rotate = getRotate(progress, index) - maxRotate;
                float translate = maxTranslate - getTranslate(progress, index);
                if (rotate > -showRotate) {
                    textPaint.setAlpha(transparent);
                    textEffect.setAlpha(transparent);
                    linePaint.setAlpha(transparent);
                } else {
                    textPaint.setAlpha(0);
                    linePaint.setAlpha(0);
                    textEffect.setAlpha(textEffectDrawWithLayout() ? transparent : 0);
                }

                canvas.rotate(rotate, lineLeft + translate + gaps[index] / 2, lineTop);

                String text = String.valueOf(lineText.charAt(c));

                if (!textEffectDrawWithLayout()) {
                    textEffect.drawText(canvas, text, lineLeft + translate, lineBaseline);
                }

                canvas.drawText(text, lineLeft + translate, lineBaseline, textPaint);
                drawUnderLine(canvas, lineLeft + translate, lineLeft + translate + gaps[index], lineBaseline);

                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft + translate, lineBaseline);
                }

                lineLeft += gaps[index];
                index++;
                canvas.restore();
            }
        }
    }

    private float getRotate(float progress, int index) {
        float r = (maxRotate / charTime * ((progress) * timeAnimation - charTime * index / mostCount));
        if (r > maxRotate) {
            r = maxRotate;
        } else if (r < 0) {
            r = 0;
        }
        return r;
    }

    private float getTranslate(float progress, int index) {
        float r = (maxTranslate / charTime * ((progress) * timeAnimation - charTime * index / mostCount));
        if (r > maxTranslate) {
            r = maxTranslate;
        } else if (r < 0) {
            r = 0;
        }
        return r;
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
        return new SkateTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.SKATE.name();
    }
}
