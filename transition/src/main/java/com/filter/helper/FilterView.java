package com.filter.helper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.filter.R;
import com.filter.transition.model.Scene;
import com.filter.transition.view.TransitionView;

import java.util.ArrayList;

public class FilterView extends RelativeLayout {
    private ValueAnimator valueAnimator;
    private final ArrayList<Scene> list = new ArrayList<>();
    public TransitionView transitionView;

    public FilterView(Context context) {
        super(context);
        init();
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        transitionView = new TransitionView(getContext());
        addView(transitionView, params);

        valueAnimator = ValueAnimator.ofInt(0,1000);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) valueAnimator.getAnimatedValue();
            updateValue(value);
            if(value>=999){
                valueAnimator.start();
            }
        });
    }

    private void updateValue(int value) {
        transitionView.setCurrentTime(value);
    }

}
