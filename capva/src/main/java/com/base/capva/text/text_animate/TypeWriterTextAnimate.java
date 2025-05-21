package com.base.capva.text.text_animate;

import android.graphics.Canvas;
import android.os.Handler;

import com.base.capva.common.Common;
import com.base.capva.text.text_effect.BackgroundTextEffect;
import com.base.capva.text.text_effect.HollowTextEffect;
import com.base.capva.text.text_effect.NeonTextEffect;
import com.base.capva.utils.ShapeStyle;
import com.base.capva.utils.TextAnimate;

public class TypeWriterTextAnimate extends BaseTextAnimate {

    private int delayType = 100;
    private int currentLength = -1;
    private int length;
    private int gapIndex, dem;
    private int timeAnimation;

    private Handler handlerType = new Handler();
    private Runnable runnableType = new Runnable() {
        @Override
        public void run() {
            currentLength++;
            textView.invalidate();
            handlerType.removeCallbacks(runnableType);
            handlerType.postDelayed(runnableType, delayType);
        }
    };

    @Override
    public void prepare() {
        super.prepare();
        dem = 0;
        length = text.length();
        currentLength = length;
        calculateGaps();

        timeAnimation = length * delayType;
        if (timeAnimation > Common.MIN_DURATION_VIDEO) {
            delayType = Common.MIN_DURATION_VIDEO / length;
            timeAnimation = length * delayType;
        }
        textView.invalidate();
    }

    @Override
    public void startAnimation() {
        currentLength = -1;
        textView.invalidate();
        handlerType.removeCallbacks(runnableType);
        handlerType.postDelayed(runnableType, timeDelay);
    }

    @Override
    public void stopAnimation() {
        handlerType.removeCallbacks(runnableType);
        currentLength = length;
        textView.invalidate();
    }

    @Override
    protected void draw(Canvas canvas) {
        if (textEffectDrawWithLayout()) {
            textEffect.drawLayout(canvas, text.toString(), layout);
        }

        dem = 0;
        gapIndex = 0;
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
            if (lineStart <= text.length() && lineEnd <= text.length()) {
                String lineText = text.subSequence(lineStart, lineEnd).toString();
                for (int c = 0; c < lineText.length(); c++) {
                    if (dem < currentLength) {
                        String t = String.valueOf(lineText.charAt(c));

                        if (!textEffectDrawWithLayout()) {
                            textEffect.drawOnCircle(canvas, t, circle, hOffset, vOffset);
                        }

                        canvas.drawTextOnPath(t, circle, hOffset, vOffset, textPaint);
                        drawUnderLineCircle(canvas, gaps[gapIndex], 0);

                        if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                            ((NeonTextEffect) textEffect).drawOnCircleColorWhite(canvas, t, circle, hOffset, vOffset);
                        }

                        hOffset += gaps[gapIndex++];
                        dem++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private void drawWithMultiLevelNumber(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft;
            float l = gaps[dem];
            float l1 = (dem + 1) >= text.length() ? 0 : gaps[dem + 1];
            float l2 = (countEnter < 9) || (dem + 2) >= text.length() ? 0 : gaps[dem + 2];
            if (lineAfterEnter) {
                lineLeft = layout.getLineLeft(i) + (l + l1 + l2) / 2;
            } else {
                lineLeft = layout.getLineLeft(i);
            }
            float lineBaseline = layout.getLineBaseline(i);
            if (lineStart <= text.length() && lineEnd <= text.length()) {
                String lineText = text.subSequence(lineStart, lineEnd).toString();
                lineAfterEnter = lineText.contains("\n");
                for (int c = 0; c < lineText.length(); c++) {
                    if (dem < currentLength) {
                        String text = String.valueOf(lineText.charAt(c));

                        if (dem >= 2) {
                            afterEnter1 = String.valueOf(this.text.charAt(dem - 2)).equals("\n");
                        }
                        if (!afterEnter1 && dem >= 3 && countEnter > 8) {
                            afterEnter2 = String.valueOf(this.text.charAt(dem - 3)).equals("\n");
                        }

                        if (afterEnter || dem == 0 || afterEnter1 || dem == 1 || afterEnter2) {

                            float left;
                            if (afterEnter || dem == 0) {
                                left = -(l + l1 + l2);
                                afterEnter = false;
                            } else if (afterEnter1 || dem == 1) {
                                left = -(l1 + l2);
                                afterEnter1 = false;
                            } else {
                                left = -l2;
                                afterEnter2 = false;
                            }

                            if (!textEffectDrawWithLayout()) {
                                textEffect.drawText(canvas, text, left, lineBaseline);
                            }

                            canvas.drawText(text, left, lineBaseline, textPaint);

                            if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, left, lineBaseline);
                            }
                            gapIndex++;
                        } else {
                            if (!textEffectDrawWithLayout()) {
                                textEffect.drawText(canvas, text, lineLeft, lineBaseline);
                            }
                            canvas.drawText(text, lineLeft, lineBaseline, textPaint);
                            drawUnderLine(canvas, lineLeft, lineLeft + gaps[gapIndex], lineBaseline);

                            if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, lineBaseline);
                            }
                            lineLeft += gaps[gapIndex++];
                        }
                        dem++;
                        afterEnter = text.equals("\n");
                    } else {
                        return;
                    }
                }
                if (lineAfterEnter) {
                    countEnter++;
                }
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
            if (lineStart <= text.length() && lineEnd <= text.length()) {
                String lineText = text.subSequence(lineStart, lineEnd).toString();
                lineAfterEnter = lineText.contains("\n");
                for (int c = 0; c < lineText.length(); c++) {
                    if (dem < currentLength) {
                        String text = String.valueOf(lineText.charAt(c));
                        if (/*text.equals("\u25CF") &&*/ (afterEnter || dem == 0)) {
                            if (!textEffectDrawWithLayout()) {
                                textEffect.drawText(canvas, text, -gaps[0], lineBaseline);
                            }

                            canvas.drawText(text, -gaps[0], lineBaseline, textPaint);

                            if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, -gaps[0], lineBaseline);
                            }
                            gapIndex++;
                        } else {
                            if (!textEffectDrawWithLayout()) {
                                textEffect.drawText(canvas, text, lineLeft, lineBaseline);
                            }

                            canvas.drawText(text, lineLeft, lineBaseline, textPaint);
                            drawUnderLine(canvas, lineLeft, lineLeft + gaps[gapIndex], lineBaseline);

                            if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                                ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, lineBaseline);
                            }
                            lineLeft += gaps[gapIndex++];
                        }
                        dem++;
                        afterEnter = text.equals("\n");

                    } else {
                        return;
                    }
                }
            }
        }
    }

    private void drawDefault(Canvas canvas) {
//        setTextBoldAndItalic();
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            float lineLeft = layout.getLineLeft(i);
            float lineBaseline = layout.getLineBaseline(i);
            if (lineStart <= text.length() && lineEnd <= text.length()) {
                String lineText = text.subSequence(lineStart, lineEnd).toString();
                for (int c = 0; c < lineText.length(); c++) {
                    if (dem < currentLength) {
                        String text = String.valueOf(lineText.charAt(c));
                        if (!textEffectDrawWithLayout()) {
                            textEffect.drawText(canvas, text, lineLeft, lineBaseline);
                        }

                        canvas.drawText(text, lineLeft, lineBaseline, textPaint);
                        drawUnderLine(canvas, lineLeft, lineLeft + gaps[gapIndex], lineBaseline);

                        if (!textEffectDrawWithLayout() && textEffect instanceof NeonTextEffect) {
                            ((NeonTextEffect) textEffect).drawTextColorWhite(canvas, text, lineLeft, lineBaseline);
                        }

                        lineLeft += gaps[gapIndex++];
                        dem++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void setFrameInTime(int time) {
        if (time == 0) {
            dem = 0;
            currentLength = -1;
        }
        int timeInAnimation = time - timeDelay;

//        if (timeInAnimation >= 0) {
//            int currentLength = timeInAnimation * length / getDuration();
//            if (currentLength > this.currentLength) {
//                this.currentLength = currentLength;
//                textView.invalidate();
//            }
//        }
        if (timeInAnimation >= 0 && getDuration() != 0 && timeInAnimation <= getDuration()) {
            int currentLength = timeInAnimation * length / getDuration();
            if (currentLength > this.currentLength) {
                this.currentLength = currentLength;
                textView.invalidate();
            }
            textView.invalidate();
        } else if (timeInAnimation > getDuration()) {
            if (currentLength != length) {
                currentLength = length;
                textView.invalidate();
            }
        } else {
            if (currentLength != 0) {
                currentLength = 0;
                textView.invalidate();
            }
        }
    }

    @Override
    public int getDuration() {
        if (textView != null && textView.getText() != null) {
            return textView.getText().length() * delayType;
        }
        return 0;
    }

    @Override
    protected boolean textEffectDrawWithLayout() {
        return textEffect instanceof BackgroundTextEffect
                || textEffect instanceof HollowTextEffect;
    }

    @Override
    public BaseTextAnimate duplicate() {
        return new TypeWriterTextAnimate();
    }

    @Override
    public String getName() {
        return TextAnimate.TYPE_WRITE.name();
    }
}
