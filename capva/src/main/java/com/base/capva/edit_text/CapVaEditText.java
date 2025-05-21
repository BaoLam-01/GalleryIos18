package com.base.capva.edit_text;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.capva.callback.OnCapVaEditTextListener;
import com.base.capva.callback.OnOverlayEditTextListener;
import com.base.capva.text.CapVaTextView;

public class CapVaEditText extends RelativeLayout implements OnOverlayEditTextListener, TextWatcher {

    private OverlayEditText overlayEditText;
    private FrameEditText customEditText;
    private CapVaTextView capVaTextView;
    private OnCapVaEditTextListener onCapVaEditTextListener;
    //    private String text;
    private boolean isAllCap;

    public CapVaEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public CapVaEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CapVaEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        overlayEditText = new OverlayEditText(getContext());
        addView(overlayEditText, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        overlayEditText.setOnOverlayEditTextListener(this);
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void setOnCapVaEditTextListener(OnCapVaEditTextListener onCapVaEditTextListener) {
        this.onCapVaEditTextListener = onCapVaEditTextListener;
    }

    public void editText(CapVaTextView capVaTextView) {
        this.capVaTextView = capVaTextView;
        customEditText = new FrameEditText(getContext());
        customEditText.getCustomEditText().setBackgroundColor(Color.TRANSPARENT);
        customEditText.getCustomEditText().setTextColor(Color.TRANSPARENT);
        customEditText.getCustomEditText().setGravity(capVaTextView.getGravity());
        customEditText.getCustomEditText().setLetterSpacing(capVaTextView.getLetterSpacing());
        customEditText.getCustomEditText().setLineSpacing(0, capVaTextView.getLineSpacing());
        String text = capVaTextView.getText();
        isAllCap = capVaTextView.isAllCap();
        String t;
        if (isAllCap) {
            t = text.toUpperCase();
        } else {
            t = text;
        }
        customEditText.getCustomEditText().setAllCaps(capVaTextView.isAllCap());
        customEditText.getCustomEditText().setText(t);
        customEditText.getCustomEditText().setTypeface(capVaTextView.getTypeface());
        customEditText.getCustomEditText().setTextSize(TypedValue.COMPLEX_UNIT_PX, capVaTextView.getTextView().getTextSize());
        customEditText.getCustomEditText().addTextChangedListener(this);

        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) capVaTextView.getLayoutParams();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(p.width, p.height);
        addView(customEditText, params);
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            customEditText.getCustomEditText().setCursorVisible(false);
        }
        overlayEditText.bringToFront();
        customEditText.post(() -> {
            if (!isAttachedToWindow()) {
                return;
            }
            customEditText.setBaseView(capVaTextView);
            customEditText.post(() -> dispatchTouchEditText(customEditText.getWidth(), customEditText.getHeight()));
        });
    }

    private void setCursorEditText(MotionEvent e) {
        float[] translate = capVaTextView.getTranslate();
        float scale = capVaTextView.getScale();
        float x = (e.getX() - translate[0]) / scale;
        float y = (e.getY() - translate[1]) / scale;
        dispatchTouchEditText(x, y);
    }

    private void dispatchTouchEditText(float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        customEditText.getCustomEditText().dispatchTouchEvent(event);

        customEditText.getCustomEditText().postDelayed(() -> {
            if(!isAttachedToWindow()){
                return;
            }
            MotionEvent event1 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
            customEditText.getCustomEditText().dispatchTouchEvent(event1);
        }, 10);
    }

    @Override
    public void onSingleTapUpEditText(MotionEvent e) {
        if (capVaTextView.touchInView(e.getX(), e.getY(), 10)) {
            setCursorEditText(e);
        } else {
            editTextDone();
        }
    }

    public void editTextDone() {
        if (onCapVaEditTextListener != null) {
            Editable editable = customEditText.getCustomEditText().getText();
            if (editable != null) {
                String text = editable.toString().trim();
                onCapVaEditTextListener.onTextChanged(text);
            }
        }

        if (onCapVaEditTextListener != null) {
            onCapVaEditTextListener.onEditTextDone();
        }
        removeView(customEditText);
        setVisibility(INVISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (onCapVaEditTextListener != null && length != s.length()) {
            String text;
            if (isAllCap) {
                text = s.toString().toUpperCase();
            } else {
                text = s.toString()/*.toLowerCase()*/;
            }
            length = s.length();
            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                customEditText.getCustomEditText().setText(text);
            }
            customEditText.getCustomEditText().setSelection(s.length());
            onCapVaEditTextListener.onTextChanged(text);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().equals("")) {
            checkShowSelection = true;
        } else if (checkShowSelection) {
            checkShowSelection = false;
            post(() -> dispatchTouchEditText(customEditText.getWidth(), 0));
        }
    }

    private int length = 0;
    private boolean checkShowSelection = false;
}
