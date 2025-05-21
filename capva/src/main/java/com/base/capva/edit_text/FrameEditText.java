package com.base.capva.edit_text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.capva.text.CapVaTextView;

public class FrameEditText extends FrameLayout {
    private CustomEditText customEditText;
    private Matrix matrix;

    public FrameEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public FrameEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        matrix = new Matrix();
        customEditText = new CustomEditText(getContext());
        addView(customEditText, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setClipChildren(false);
        setClipToPadding(false);
    }

    public CustomEditText getCustomEditText() {
        return customEditText;
    }

    public void setBaseView(CapVaTextView baseView) {
        post(new Runnable() {
            @Override
            public void run() {
                if (!isAttachedToWindow()) {
                    return;
                }
                if (baseView != null) {
                    float scale = baseView.getScale();
                    float x = (int) ((baseView.getWidth() - getWidth()) / 2f) * scale;
                    float y = (int) ((baseView.getHeight() - getHeight()) / 2f) * scale;

                    matrix.reset();
                    matrix.set(baseView.getMatrix());
                    matrix.postTranslate(x, y);
                    invalidate();
                }
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.concat(matrix);
        super.dispatchDraw(canvas);
    }
}
