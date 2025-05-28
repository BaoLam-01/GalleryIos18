package com.example.galleryios18.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryios18.App;

import org.jetbrains.annotations.NotNull;

public class CustomRecycleView extends RecyclerView implements RecyclerView.OnItemTouchListener {
    GestureDetector gestureDetector;

    public CustomRecycleView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener(this, false));
        addOnItemTouchListener(this);
    }

    public void setInverse(boolean inverse) {
        gestureDetector = new GestureDetector(App.Companion.getInstance(), new GestureListener(this, inverse));
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= 0.7;
        return super.fling(velocityX, velocityY);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(e);
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull @NotNull RecyclerView rv, @NonNull @NotNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}

class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private final int Y_BUFFER = 10;
    ViewGroup recyclerView;
    private boolean isInverse = false;

    public GestureListener(ViewGroup recyclerView, boolean isInverse) {
        this.recyclerView = recyclerView;
        this.isInverse = isInverse;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Prevent ViewPager from intercepting touch events as soon as a DOWN is detected.
        // If we don't do this the next MOVE event may trigger the ViewPager to switch
        // tabs before this view can intercept the event.
        recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        return super.onDown(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isInverse) {
            condition(Math.abs(distanceX) > Math.abs(distanceY), Math.abs(distanceY) > Y_BUFFER);
        } else {
            condition(Math.abs(distanceY) > Math.abs(distanceX), Math.abs(distanceX) > Y_BUFFER);
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    private void condition(boolean v1, boolean v2) {
        if (v1) {
            // Detected a horizontal scroll, allow the viewpager from switching tabs
            recyclerView.getParent().requestDisallowInterceptTouchEvent(false);
        } else if (v2) {
            // Detected a vertical scroll prevent the viewpager from switching tabs
            recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

}