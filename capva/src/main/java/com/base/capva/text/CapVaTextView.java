package com.base.capva.text;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.base.capva.base.BaseView;
import com.base.capva.callback.OnElementAnimationListener;
import com.base.capva.common.Common;
import com.base.capva.text.text_animate.BaseTextAnimate;
import com.base.capva.text.text_effect.BaseTextEffect;
import com.base.capva.utils.AnchorText;
import com.base.capva.utils.CapVaGravity;
import com.base.capva.utils.MultiLevelList;
import com.base.capva.utils.ShapeStyle;

public class CapVaTextView extends BaseView {
    public static final float MAX_SCALE = 10f;

    private CustomTextView textView;
    private BaseTextAnimate textAnimate;
    private BaseTextEffect textEffect;

    private MultiLevelList multiLevelList = MultiLevelList.NONE;
    private ShapeStyle shapeStyle = ShapeStyle.NORMAL;

    private String text = "";
    private int textColor = 0;
    private Typeface typeface;
    private String fontName;
    private boolean isBold, isItalic, isUnderline;
    private boolean allCap;
    private int gravity = CapVaGravity.START;
    private float letterSpacing = 0;
    private float lineSpacing = 1;
    private float transparent = 1;
    private float percentCircle = 0;

    private AnchorText anchorText = AnchorText.BOT;
    private OnElementAnimationListener onElementAnimationListener;

    public void setOnElementAnimationListener(OnElementAnimationListener onElementAnimationListener) {
        this.onElementAnimationListener = onElementAnimationListener;
    }

    public CapVaTextView(Context context) {
        super(context, false);
    }

    public CapVaTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CapVaTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        textView = new CustomTextView(getContext());
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(textView, params);
    }

    @Override
    public void invalidate() {
        prepare();
        super.invalidate();
        textView.invalidate();
    }

    public void invalidateNotPrepare() {
        super.invalidate();
    }

    /**
     * effect
     */
    public void setEffect(BaseTextEffect effect) {
        this.textEffect = effect;
        if (textAnimate != null) {
            textAnimate.setTextEffect(textEffect);
            textEffect.setTextAnimate(textAnimate);
        }
    }

    public BaseTextEffect getTextEffect() {
        return textEffect;
    }

    /**
     * animate
     */
    public void setAnimate(BaseTextAnimate animate) {
        boolean isReverse = false;
        if (this.textAnimate != null)
            isReverse = this.textAnimate.isCircleReverse();

        this.textAnimate = animate;
        textAnimate.init(CapVaTextView.this, textView, onElementAnimationListener);
        this.textAnimate.setMultiLevelList(multiLevelList);
        if (percentCircle != 0) {
            this.textAnimate.setPercentCircle(percentCircle);
        }
        textAnimate.setCircleReverse(isReverse);
        this.textAnimate.setShapeStyle(shapeStyle);
        textView.setAnimate(textAnimate);
        if (textEffect != null) {
            textEffect.setTextAnimate(textAnimate);
            textAnimate.setTextEffect(textEffect);
        }
    }

    public BaseTextAnimate getAnimate() {
        return textAnimate;
    }

    public void setPercentCircle(float percentCircle) {
        this.percentCircle = percentCircle;
        if (textAnimate != null) {
            textAnimate.setPercentCircle(percentCircle);
        }
    }

    public void setShapeStyle(ShapeStyle shapeStyle) {
        this.shapeStyle = shapeStyle;
        if (textAnimate != null) {
            textAnimate.setShapeStyle(shapeStyle);
            invalidate();
        }
    }

    public void setMultiLevelList(MultiLevelList multiLevelList) {
        this.multiLevelList = multiLevelList;
        String text = updateAllCap();
        textView.setText(multiLevelList == MultiLevelList.NONE ? text : getTextMultiLevelList(text, multiLevelList));
        setShapeStyle(ShapeStyle.NORMAL);
        if (textAnimate != null) {
            textAnimate.setMultiLevelList(multiLevelList);
        }
    }

    private String getTextMultiLevelList(String text, MultiLevelList multiLevelList) {
        String s;
        if (multiLevelList == MultiLevelList.DOT) {
            String temp = Common.DOT + text;
            s = temp.replaceAll("\n", "\n" + Common.DOT);
        } else {
            String[] splits = text.split("\n");
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < splits.length; i++) {
                temp.append(i != 0 ? "\n" : "").append(i + 1).append(".").append(splits[i]);
            }
            s = temp.toString();
        }
        return s;
    }

    public MultiLevelList getMultiLevelList() {
        return multiLevelList;
    }

    public ShapeStyle getShapeStyle() {
        return shapeStyle;
    }

    @Override
    public void setTimeDelay(int timeDelay) {
        textView.setTimeDelay(timeDelay);
    }

    @Override
    public void startAnimation() {
        textView.startAnimation();
    }

    @Override
    public void stopAnimation() {
        textView.stopAnimation();
    }

    @Override
    public void prepare() {
        textView.prepare();
    }

    @Override
    public void setFrameInTime(int time) {
        textView.setFrameInTime(time);
    }

    public void setAnchorText(AnchorText anchorText) {
        this.anchorText = anchorText;
    }

    public AnchorText getAnchorText() {
        return anchorText;
    }

    public void setText(String text) {
        this.text = text;
        textView.setText(updateAllCap());
        textView.prepare();
        textView.invalidate();
//        textView.setText(text);
//        updateFormatText();
    }

    public void setTextSize(float size) {
        textView.setTextSize(size);
    }

    public void setTextColor(int color) {
        this.textColor = color;
        textView.setTextColor(color);
    }

    public void setTypeface(String fontName, Typeface typeface) {
        this.fontName = fontName;
//        this.typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
        this.typeface = typeface;
        textView.setTypeface(typeface);
    }

    public void setTextStyle(boolean bold, boolean italic, boolean underline) {
        isBold = bold;
        isItalic = italic;
        isUnderline = underline;
//        updateFormatText();
    }

    public void setBold(boolean bold) {
        isBold = bold;
//        updateFormatText();
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
//        updateFormatText();
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
//        updateFormatText();
    }

    public void setAllCap(boolean allCap) {
        this.allCap = allCap;
        String s = updateAllCap();
        if (multiLevelList != MultiLevelList.NONE) {
            setMultiLevelList(multiLevelList);
        } else {
            textView.setText(s);
        }
//        updateFormatText();
//        textView.setAllCaps(allCap);
    }

    private String updateAllCap() {
        String s = text + "";
        if (allCap) {
            return s.toUpperCase();
        }
        return s;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        textView.setGravity(gravity);
    }

    public void setTransparent(float transparent) {
        this.transparent = transparent;
        if (textAnimate != null) {
            textAnimate.setTransparent((int) (transparent * 255));
        }
        if (textEffect != null) {
            textEffect.setAlpha((int) (transparent * 255));
        }

    }


    /**
     * : -0.2 -> 0.8 default 0
     *
     * @param letterSpacing
     */
    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
        textView.setLetterSpacing(letterSpacing);
        invalidate();
    }

    /**
     * 0.5 -> 1.5 default 1
     *
     * @param lineSpacing
     */
    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
        textView.setLineSpacing(0, lineSpacing);
    }

    private void updateFormatText() {
//        SpannableString spanString = new SpannableString(text);
//        if (isUnderline) {
//            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
//        }
//        if (isBold) {
//            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
//        }
//        if (isItalic) {
//            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
//        }
//        textView.setText(spanString);
        textView.setText(getTextWithFormat(text));
        invalidate();
    }

    public SpannableString getTextWithFormat(CharSequence text) {
        SpannableString spanString = new SpannableString(text);
        if (isUnderline) {
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        }
        if (isBold) {
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        }
        if (isItalic) {
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        }
        return spanString;
    }

    public void setSelection(int p) {
//        textView.setSelection(p);
    }

    public String getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public String getFontName() {
        return fontName;
    }

    public boolean isBold() {
        return isBold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public boolean isAllCap() {
        return allCap;
    }

    public int getGravity() {
        return gravity;
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public float getTransparent() {
        return transparent;
    }

    public CustomTextView getTextView() {
        return textView;
    }

    @Override
    protected float[] getBoundPoints(int padding) {
//        return super.getBoundPoints(padding);
        if (shapeStyle == ShapeStyle.CIRCLE) {
            float centerX = textView.getWidth() / 2f;
            float centerY = textView.getHeight() / 2f;
            float radius = getRadiusWithHeightText();
            return new float[]{
                    centerX - radius, centerY - radius,
                    centerX + radius, centerY - radius,
                    centerX + radius, centerY + radius,
                    centerX - radius, centerY + radius
            };
        } else {
            switch (multiLevelList) {
                default:
                case NONE:
                    return super.getBoundPoints(padding);
                case DOT:
                case NUMBER:
                    float left = textAnimate.getLeftMultiLevelList();
                    return new float[]{
                            -padding - left, -padding,
                            getWidth() + padding, -padding,
                            getWidth() + padding, getHeight() + padding,
                            -padding - left, getHeight() + padding
                    };
            }
        }
    }

    public float getRadiusWithHeightText() {
        return textAnimate.getRadiusCircle()
                + (float) textView.getHeight() / (float) textView.getLineCount()
//                + textView.getPaint().getFontMetrics().bottom - textView.getPaint().getFontMetrics().top
                ;
    }
}
