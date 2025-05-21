package com.base.capva.text.text_animate;

import android.graphics.Canvas;

import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;

public class TumbleTextAnimate extends BaseTextAnimate {

    private float s;

    @Override
    public void prepare() {
        super.prepare();
        s = (capVaTextView.getTranslate()[0] + capVaTextView.getWidth() * capVaTextView.getScale()) * 2;
        defaultMatrix.set(capVaTextView.getMatrix());
//        matrix.set(defaultMatrix);
//        matrix.postTranslate(-s, 0);
//        capVaTextView.getMatrix().set(matrix);
//        capVaTextView.invalidateNotPrepare();
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.save();
        if (textEffectDrawWithLayout()) {
            textEffect.drawLayout(canvas, text.toString(), layout);
        }

        int alpha = (int) (transparent * progress);
        textEffect.setAlpha(alpha);
        textPaint.setAlpha(alpha);
        linePaint.setAlpha(alpha);

        float trans = -(1 - progress) * s;
        matrix.set(defaultMatrix);
        matrix.postTranslate(trans, 0);
        capVaTextView.getMatrix().set(matrix);
        capVaTextView.invalidateNotPrepare();
//        capVaTextView.setTranslationX(trans);

        float rotate = (1 - progress) * -120;
        canvas.rotate(rotate, textView.getWidth() / 2f, textView.getHeight() / 2f);

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
        canvas.restore();
    }

    @Override
    public void stopAnimation() {
        super.stopAnimation();
        capVaTextView.getMatrix().set(defaultMatrix);
        capVaTextView.invalidateNotPrepare();
    }

    private void drawTextOnCircle(Canvas canvas) {
        int index = 0;
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            for (int j = 0; j < lineText.length(); j++) {
                String t = String.valueOf(lineText.charAt(j));

                if (!textEffectDrawWithLayout()) {
                    textEffect.drawOnCircle(canvas, t, circle, hOffset, vOffset);
                }

                canvas.drawTextOnPath(t, circle, hOffset, vOffset, textPaint);
                drawUnderLineCircle(canvas, gaps[index], 0);

                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawOnCircleColorWhite(canvas, t, circle, hOffset, vOffset);
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
            float l;
            if (countEnter > 8) {
                l = (gaps[0] * 2 + gaps[1]);
            } else {
                l = (gaps[0] + gaps[1]);
            }
            float lineLeft = lineAfterEnter ? layout.getLineLeft(i) + l / 2 : layout.getLineLeft(i);
            float lineRight = lineAfterEnter ? layout.getLineRight(i) - l / 2 : layout.getLineRight(i);

            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            String pre;
            if (lineAfterEnter) {
                int index;
                if (countEnter > 8) {
                    index = 3;
                } else {
                    index = 2;
                }
                pre = lineText.substring(0, index);

                textEffect.drawText(canvas, pre, -l, lineBaseline);

                canvas.drawText(pre, -l, lineBaseline, textPaint);

                if (textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, pre, -l, lineBaseline);
                }

                lineText = lineText.substring(index);

            }

            textEffect.drawText(canvas, lineText, lineLeft, lineBaseline);

            canvas.drawText(lineText, lineLeft, lineBaseline, textPaint);
            drawUnderLine(canvas, lineLeft, lineRight, lineBaseline);

            if (textEffect instanceof NeonTextEffect) {
                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, lineText, lineLeft, lineBaseline);
            }
            lineAfterEnter = lineText.contains("\n");
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
            float lineRight = lineAfterEnter ? layout.getLineRight(i) - gaps[0] / 2 : layout.getLineRight(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            if (lineAfterEnter) {

                String dot = String.valueOf(lineText.charAt(0));

                textEffect.drawText(canvas, dot, -gaps[0], lineBaseline);
                canvas.drawText(dot, -gaps[0], lineBaseline, textPaint);
                if (textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, dot, -gaps[0], lineBaseline);
                }

                lineText = lineText.substring(1);

            }
            textEffect.drawText(canvas, lineText, lineLeft, lineBaseline);

            canvas.drawText(lineText, lineLeft, lineBaseline, textPaint);
            drawUnderLine(canvas, lineLeft, lineRight, lineBaseline);

            if (textEffect instanceof NeonTextEffect) {
                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, lineText, lineLeft, lineBaseline);
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
            float lineRight = layout.getLineRight(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            textEffect.drawText(canvas, lineText, lineLeft, lineBaseline);

            canvas.drawText(lineText, lineLeft, lineBaseline, textPaint);
            drawUnderLine(canvas, lineLeft, lineRight, lineBaseline);

            if (textEffect instanceof NeonTextEffect) {
                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, lineText, lineLeft, lineBaseline);
            }
        }
    }

    @Override
    public int getDuration() {
        return 400;
    }

    @Override
    protected boolean textEffectDrawWithLayout() {
        return textEffect instanceof BackgroundTextEffect;
    }

    @Override
    public BaseTextAnimate duplicate() {
        return new TumbleTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.TUMBLE.name();
    }
}
