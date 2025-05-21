package com.base.capva.text.text_animate;

import android.graphics.Canvas;

import com.base.capva.common.Common;
import com.base.capva.model.TextGroup;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;

public class ShiftTextAnimate extends BaseTextAnimate {

    private final float mostCount = 6f;
    private float charTime = 400;
    private float h, hShow;
    private int timeAnimation;

    @Override
    public void prepare() {
        super.prepare();
        setup();
        textView.invalidate();
    }

    private void setup() {
        if (layout != null) {
            calculateTextGroups(layout, textPaint);
            int n = textGroups.size();
            n = n <= 0 ? 1 : n;
            timeAnimation = (int) ((charTime * (mostCount + n - 1)) / mostCount);
            if (timeAnimation > Common.MIN_DURATION_VIDEO) {
                charTime = Math.round((Common.MIN_DURATION_VIDEO * mostCount) / (mostCount + n - 1)) - 2;
                timeAnimation = (int) ((charTime * (mostCount + n - 1)) / mostCount);
            }
            h = (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top) / 2;
            hShow = h * 0.2f;
        }
    }

    @Override
    protected void draw(Canvas canvas) {
        if (textEffectDrawWithLayout()) {
            textEffect.drawLayout(canvas, text.toString(), layout);
        }

        if (shapeStyle == ShapeStyle.CIRCLE) {
            drawTextOnCircle(canvas);
        } else {
            drawDefault(canvas);
        }

    }

    private void drawTextOnCircle(Canvas canvas) {
        int index = 0;
//        setTextBoldAndItalic();
        for (int i = 0; i < textGroups.size(); i++) {
            canvas.save();

            TextGroup textGroup = textGroups.get(i);

            float b = (int) (h / charTime * (progress * timeAnimation - charTime * i / mostCount));
            if (b > h) {
                b = h;
            } else if (b < 0) {
                b = 0;
            }

            if (b > hShow) {
                textPaint.setAlpha(transparent);
                textEffect.setAlpha(transparent);
                linePaint.setAlpha(transparent);
            } else {
                textPaint.setAlpha(0);
                linePaint.setAlpha(0);
                textEffect.setAlpha(textEffectDrawWithLayout() ? transparent : 0);
            }

            String lineText = textGroup.getText();
//            float left = textGroup.getLineLeft();
//            float baseLine = textGroup.getLineBaseline() - h + b;

            vOffset = b - h;
//            if (!lineText.contains("\n") && i != textGroups.size() - 1) {
//                lineText += " ";
//            }
            for (int j = 0; j < lineText.length(); j++) {
                String text = String.valueOf(lineText.charAt(j));

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

            canvas.restore();
        }
    }

    private void drawDefault(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < textGroups.size(); i++) {
            canvas.save();

            TextGroup textGroup = textGroups.get(i);

            float b = (h / charTime * (progress * timeAnimation - charTime * i / mostCount));
            if (b > h) {
                b = h;
            } else if (b < 0) {
                b = 0;
            }

            if (b > hShow) {
                textPaint.setAlpha(transparent);
                textEffect.setAlpha(transparent);
                linePaint.setAlpha(transparent);
            } else {
                textPaint.setAlpha(0);
                linePaint.setAlpha(0);
                textEffect.setAlpha(textEffectDrawWithLayout() ? transparent : 0);
            }

            String lineText = textGroup.getText();
            float left = textGroup.getLineLeft();
            float baseLine = textGroup.getLineBaseline() - h + b;


            if (!textEffectDrawWithLayout()) {
                textEffect.drawText(canvas, lineText, left, baseLine);
            }

            canvas.drawText(lineText, left, baseLine, textPaint);
            if (!textGroup.isMultiLeveList()) {
                drawUnderLine(canvas, left, left + textGroup.getGap(), baseLine);
            }

            if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, lineText, left, baseLine);
            }

            canvas.restore();
        }
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
        return new ShiftTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.SHIFT.name();
    }
}
