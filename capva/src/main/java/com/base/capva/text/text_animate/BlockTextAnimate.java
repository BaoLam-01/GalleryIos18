package com.base.capva.text.text_animate;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.Interpolator;

import com.base.capva.base.BaseView;
import com.base.capva.callback.OnElementAnimationListener;
import com.base.capva.text.CapVaTextView;
import com.base.capva.text.CustomTextView;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;

public class BlockTextAnimate extends BaseTextAnimate {

    private Paint paint;
    private RectF rectBlock;
    private RectF rectClip;
    private ValueAnimator leftAnimator, rightAnimator;
    private float leftProgress = 1, rightProgress = 1;
    private float s;

    @Override
    public void init(CapVaTextView capVaTextView, CustomTextView customTextView, OnElementAnimationListener onElementAnimationListener) {
        super.init(capVaTextView, customTextView, onElementAnimationListener);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textPaint.getColor());
        paint.setStyle(Paint.Style.FILL);
        rectBlock = new RectF();
        rectClip = new RectF();
    }

    @Override
    public void prepare() {
        super.prepare();
        s = textView.getWidth() + BaseView.paddingLeftRight * 2;
        paint.setAlpha(transparent);
        leftProgress = 1;
        rightProgress = 1;
        textView.invalidate();
    }

    @Override
    public void startAnimation() {
        leftProgress = 0;
        rightProgress = 0;
        textView.invalidate();

        rightAnimator = ValueAnimator.ofFloat(0, 1);
        rightAnimator.setDuration(getDuration() / 2);
        rightAnimator.setStartDelay(timeDelay);
        rightAnimator.addUpdateListener(valueAnimator -> {
            rightProgress = (float) valueAnimator.getAnimatedValue();
            textView.invalidate();
        });
        rightAnimator.setInterpolator((Interpolator) input -> (float) ((float) 1 - Math.pow(1 - input, 1)));
        rightAnimator.start();

        leftAnimator = ValueAnimator.ofFloat(0, 1);
        leftAnimator.setDuration(getDuration() / 2);
        leftAnimator.setStartDelay(timeDelay + getDuration() / 2);
        leftAnimator.addUpdateListener(valueAnimator -> {
            leftProgress = (float) valueAnimator.getAnimatedValue();
            textView.invalidate();
        });
        leftAnimator.setInterpolator((Interpolator) input -> (float) ((float) 1 - Math.pow(1 - input, 1)));
        leftAnimator.start();
    }

    @Override
    public void stopAnimation() {
        if (leftAnimator != null) {
            leftAnimator.end();
        }
        if (rightAnimator != null) {
            rightAnimator.end();
        }
        leftProgress = 1;
        rightProgress = 1;
        textView.invalidate();
    }

    @Override
    public void setFrameInTime(int time) {
        if (time == 0) {
            leftProgress = 0;
            rightProgress = 0;
            textView.invalidate();
        }
        int timeInAnimation = time - timeDelay;
        if (timeInAnimation >= 0 && getDuration() != 0 && timeInAnimation <= getDuration()) {
            if (timeInAnimation <= getDuration() / 2) {
                float value = timeInAnimation / (getDuration() / 2f);
                rightProgress = (float) ((float) 1 - Math.pow(1 - value, 1));
                leftProgress = 0;
            } else {
                float value = (timeInAnimation - getDuration() / 2f) / (getDuration() / 2f);
                leftProgress = (float) ((float) 1 - Math.pow(1 - value, 1));
                rightProgress = 1;
            }
            textView.invalidate();
        } else if (timeInAnimation > getDuration() && (leftProgress != 1f || rightProgress != 1f)) {
            leftProgress = 1f;
            rightProgress = 1f;
            textView.invalidate();
        }
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.save();

        if (textEffectDrawWithLayout()) {
            textEffect.drawLayout(canvas, text.toString(), layout);
        }

        if (shapeStyle == ShapeStyle.CIRCLE) {
            float centerX = textView.getWidth() / 2f;
            float centerY = textView.getHeight() / 2f;
            float radius = capVaTextView.getRadiusWithHeightText();
            rectBlock.left = leftProgress < 0.5f ? (centerX - radius * (0.5f - leftProgress) / 0.5f) : (centerX + radius * (leftProgress - 0.5f) / 0.5f);
            rectBlock.top = centerY - radius;
            rectBlock.right = rightProgress < 0.5f ? (centerX - radius * (0.5f - rightProgress) / 0.5f) : (centerX + radius * (rightProgress - 0.5f) / 0.5f);
            rectBlock.bottom = centerY + radius;

            rectClip.left = centerX - radius;
            rectClip.top = rectBlock.top;
            rectClip.right = rectBlock.left;
            rectClip.bottom = rectBlock.bottom;

        } else {
            rectBlock.left = leftProgress * s - BaseView.paddingLeftRight - (1 - leftProgress) * getLeftMultiLevelList();
            rectBlock.top = 0;
            rectBlock.right = rightProgress * s - BaseView.paddingLeftRight;
            rectBlock.bottom = textView.getHeight();

            rectClip.left = -BaseView.paddingLeftRight - textEffect.getPadding() - getLeftMultiLevelList();
            rectClip.top = rectBlock.top - textEffect.getPadding();
            rectClip.right = rectBlock.left;
            rectClip.bottom = rectBlock.bottom + textEffect.getPadding();
        }

        canvas.clipRect(rectClip);

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
        canvas.drawRect(rectBlock, paint);
    }

    private void drawTextOnCircle(Canvas canvas) {
        int index = 0;
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();

            for (int j = 0; j < lineText.length(); j++) {
                String text = String.valueOf(lineText.charAt(j));
                if (!textEffectDrawWithLayout()) {
                    textEffect.drawOnCircle(canvas, text, circle, hOffset, vOffset);
                }
                canvas.drawTextOnPath(text, circle, hOffset, vOffset, textPaint);
                drawUnderLineCircle(canvas, gaps[index], vOffset);
                if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                    ((NeonTextEffect) textEffect).drawOnCircleColorWhite(canvas, text, circle, hOffset, vOffset);
                }
                hOffset += gaps[index];
                index++;
//                if (hOffset > length) {
//                    hOffset = hOffset - length;
//                }
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

//            setTextBoldAndItalic();
            canvas.drawText(lineText, lineLeft, lineBaseline, textPaint);
            drawUnderLine(canvas, lineLeft, lineRight, lineBaseline);

            if (textEffect instanceof NeonTextEffect) {
                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, lineText, lineLeft, lineBaseline);
            }
            lineAfterEnter = lineText.contains("\n");
        }
    }

    private void drawDefault(Canvas canvas) {
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft = layout.getLineLeft(i);
            float lineRight = layout.getLineRight(i);
            float lineBaseline = layout.getLineBaseline(i);
            String lineText = text.subSequence(lineStart, lineEnd).toString();
            textEffect.drawText(canvas, lineText, lineLeft, lineBaseline);

//            setTextBoldAndItalic();
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
        return new BlockTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.BLOCK.name();
    }
}
