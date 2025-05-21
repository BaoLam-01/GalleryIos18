package com.base.capva.text.text_animate;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import com.base.capva.base.BaseView;
import com.base.capva.common.Common;
import com.base.capva.model.TextGroup;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;


public class AscendTextAnimate extends BaseTextAnimate {

    private final float mostCount = 1.5f;
    private float charTime = 300;
    private float h;
    private RectF rectF;
    private int timeAnimation;
    private Path clipPath;

    @Override
    public void prepare() {
        super.prepare();
        rectF = new RectF();
        setup();
        textView.invalidate();
        clipPath = new Path();
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
            h = textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top + 5;
        }
    }

    @Override
    protected void draw(Canvas canvas) {
        if (textEffectDrawWithLayout()) {
            textEffect.drawLayout(canvas, text.toString(), layout);
        }

        if (shapeStyle == ShapeStyle.NORMAL) {
            drawDefault(canvas);
        } else {
            drawTextOnCircle(canvas);
        }
    }

    private void drawTextOnCircle(Canvas canvas) {
        canvas.save();
        int index = 0;
        float radiusClip = radiusCircle - ((float) textView.getHeight() / (float) textView.getLineCount()) / 2 - textEffect.getPadding();
//        clipPath.reset();
//        clipPath.addCircle(textView.getWidth() / 2f, textView.getHeight() / 2f, radiusClip, Path.Direction.CW);
//        canvas.clipPath(clipPath, Region.Op.DIFFERENCE);

        float height = h + textEffect.getPadding();
//        setTextBoldAndItalic();
        for (int i = 0; i < textGroups.size(); i++) {
            canvas.save();

            TextGroup textGroup = textGroups.get(i);
            String lineText = textGroup.getText();

            float b = (height / charTime * (progress * timeAnimation - charTime * i / mostCount));
            if (b > height) {
                b = height;
            } else if (b < 0) {
                b = 0;
            }

            vOffset = height - b;

            if (!textEffectDrawWithLayout()) {
                if (vOffset < radiusClip) {
                    textPaint.setAlpha(transparent);
                    textEffect.setAlpha(transparent);
                    linePaint.setAlpha(transparent);
                } else {
                    textPaint.setAlpha(0);
                    textEffect.setAlpha(0);
                    linePaint.setAlpha(0);
                }
            }

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

        canvas.restore();
    }

    private void drawDefault(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < textGroups.size(); i++) {
            canvas.save();

            TextGroup textGroup = textGroups.get(i);

            rectF.left = -BaseView.paddingLeftRight - textEffect.getPadding() - getLeftMultiLevelList();
            rectF.top = textGroup.getLineTop() - textEffect.getPadding();
            rectF.right = textView.getWidth() + textEffect.getPadding();
            rectF.bottom = textGroup.getLineTop() + h + textEffect.getPadding();
            canvas.clipRect(rectF);

            float b = rectF.height() / charTime * (progress * timeAnimation - charTime * i / mostCount);
            if (b > rectF.height()) {
                b = rectF.height();
            } else if (b < 0) {
                b = 0;
            }

            String lineText = textGroup.getText();
            float left = textGroup.getLineLeft();
            float baseLine = textGroup.getLineBaseline() + rectF.height() - b;
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
        return new AscendTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.ASCEND.name();
    }

}
