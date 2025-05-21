package com.base.capva.edit_text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.base.capva.callback.OnOverlayEditTextListener;

public class OverlayEditText extends View {

    private GestureDetector gestureDetector;
    private OnOverlayEditTextListener onOverlayEditTextListener;

    public void setOnOverlayEditTextListener(OnOverlayEditTextListener onOverlayEditTextListener) {
        this.onOverlayEditTextListener = onOverlayEditTextListener;
    }

    public OverlayEditText(Context context) {
        super(context);
        init();
    }

    public OverlayEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (onOverlayEditTextListener != null) {
                    onOverlayEditTextListener.onSingleTapUpEditText(e);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
}
