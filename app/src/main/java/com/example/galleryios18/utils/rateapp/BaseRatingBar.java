package com.example.galleryios18.utils.rateapp;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.galleryios18.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by willy on 2017/5/5.
 */

public class BaseRatingBar extends LinearLayout implements SimpleRatingBar {

    public interface OnRatingChangeListener {
        void onRatingChange(BaseRatingBar ratingBar, float rating);
    }

    public static final String TAG = "SimpleRatingBar";

    private static final int MAX_CLICK_DISTANCE = 5;
    private static final int MAX_CLICK_DURATION = 200;

    private int mNumStars;
    private int mPadding = 20;
    private int mStarWidth;
    private int mStarHeight;
    private float mRating = -1;
    private float mPreviousRating = 0;
    private boolean mIsTouchable = true;
    private boolean mClearRatingEnabled = true;

    private float mStartX;
    private float mStartY;


    private OnRatingChangeListener mOnRatingChangeListener;

    protected List<PartialView> mPartialViews;

    public BaseRatingBar(Context context) {
        this(context, null);
    }

    /* Call by xml layout */
    public BaseRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context      context
     * @param attrs        attributes from XML => app:mainText="mainText"
     * @param defStyleAttr attributes from default style (Application theme or activity theme)
     */
    public BaseRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBarAttributes);
        float rating = typedArray.getFloat(R.styleable.RatingBarAttributes_rating, mRating);
        mNumStars = typedArray.getInt(R.styleable.RatingBarAttributes_numStars, mNumStars);
        mPadding = typedArray.getInt(R.styleable.RatingBarAttributes_starPadding, mPadding);
        mStarWidth = typedArray.getDimensionPixelSize(R.styleable.RatingBarAttributes_starWidth, 0);
        mStarHeight = typedArray.getDimensionPixelSize(R.styleable.RatingBarAttributes_starHeight, 0);
        mIsTouchable = typedArray.getBoolean(R.styleable.RatingBarAttributes_touchable, mIsTouchable);
        mClearRatingEnabled = typedArray.getBoolean(R.styleable.RatingBarAttributes_clearRatingEnabled, mClearRatingEnabled);
        typedArray.recycle();

        verifyParamsValue();

        initRatingView();
        setRating(rating);
    }

    private void verifyParamsValue() {
        if (mNumStars <= 0) {
            mNumStars = 5;
        }

        if (mPadding < 0) {
            mPadding = 0;
        }

    }

    private void initRatingView() {
        mPartialViews = new ArrayList<>();

//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                mStarWidth == 0 ? LayoutParams.WRAP_CONTENT : mStarWidth,
//                mStarHeight == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : mStarHeight);

        int size = dpToPx(52);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size, size);

        for (int i = 1; i <= 5; i++) {
            PartialView partialView = getPartialView(i);
            mPartialViews.add(partialView);
            addView(partialView, params);
        }
    }

    private PartialView getPartialView(final int ratingViewId) {
        PartialView partialView = new PartialView(getContext());
        partialView.setId(ratingViewId);
        partialView.setPadding(mPadding, mPadding, mPadding, mPadding);
        partialView.setFilledDrawable();
        partialView.setEmptyDrawable();
        return partialView;
    }

    /**
     * Retain this method to let other RatingBar can custom their decrease animation.
     */
    protected void emptyRatingBar() {
        fillRatingBar(0);
    }

    /**
     * Use {maxIntOfRating} because if the rating is 3.5
     * the view which id is 3 also need to be filled.
     */
    protected void fillRatingBar(final float rating) {
        for (PartialView partialView : mPartialViews) {
            int ratingViewId = partialView.getId();
            double maxIntOfRating = Math.ceil(rating);

            if (ratingViewId > maxIntOfRating) {
                partialView.setEmpty();
                Log.d(TAG, "fillRatingBar:1 ");
                continue;
            }

            if (ratingViewId == maxIntOfRating) {
                Log.d(TAG, "fillRatingBar:2 ");
                partialView.setPartialFilled(rating);
            } else {
                Log.d(TAG, "fillRatingBar:3 ");
                partialView.setFilled();
            }
        }
    }

    @Override
    public void setNumStars(int numStars) {
        if (numStars <= 0) {
            return;
        }

        mPartialViews.clear();
        removeAllViews();

        mNumStars = numStars;
        initRatingView();
    }

    @Override
    public int getNumStars() {
        return mNumStars;
    }

    @Override
    public void setRating(float rating) {
        if (rating > mNumStars) {
            rating = mNumStars;
        }

        if (rating < 0) {
            rating = 0;
        }

        if (mRating == rating) {
            return;
        }

        mRating = rating;

        if (mOnRatingChangeListener != null) {
            mOnRatingChangeListener.onRatingChange(this, mRating);
        }

        fillRatingBar(rating);
    }

    @Override
    public float getRating() {
        return mRating;
    }

    @Override
    public void setStarPadding(int ratingPadding) {
        if (ratingPadding < 0) {
            return;
        }

        mPadding = ratingPadding;

        for (PartialView partialView : mPartialViews) {
            partialView.setPadding(mPadding, mPadding, mPadding, mPadding);
        }
    }

    @Override
    public int getStarPadding() {
        return mPadding;
    }

    @Override
    public void setEmptyDrawableRes(@DrawableRes int res) {
        setEmptyDrawable(ContextCompat.getDrawable(getContext(), res));
    }

    @Override
    public void setFilledDrawableRes(@DrawableRes int res) {
        setFilledDrawable(ContextCompat.getDrawable(getContext(), res));
    }

    @Override
    public void setEmptyDrawable(Drawable drawable) {
        for (PartialView partialView : mPartialViews) {
            partialView.setEmptyDrawable();
        }
    }

    @Override
    public void setFilledDrawable(Drawable drawable) {
        for (PartialView partialView : mPartialViews) {
            partialView.setFilledDrawable();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchable()) {
            return false;
        }

        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = eventX;
                mStartY = eventY;
                mPreviousRating = mRating;
                handleMoveEvent(eventX);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMoveEvent(eventX);
                break;
            case MotionEvent.ACTION_UP:
                if (!isClickEvent(mStartX, mStartY, event)) {
                    return false;
                }

                handleClickEvent(eventX);
        }

        return true;
    }

    private void handleMoveEvent(float eventX) {
        for (PartialView partialView : mPartialViews) {
            if (eventX < partialView.getWidth() / 2f) {
                setRating(0);
                return;
            }

            if (!isPositionInRatingView(eventX, partialView)) {
                continue;
            }

            int rating = partialView.getId();
            if (mRating != rating) {
                setRating(rating);
            }
        }
    }

    private void handleClickEvent(float eventX) {
        for (PartialView partialView : mPartialViews) {
            if (!isPositionInRatingView(eventX, partialView)) {
                continue;
            }

            int rating = partialView.getId();
            if (mPreviousRating == rating && isClearRatingEnabled()) {
                setRating(0);
            } else {
                setRating(rating);
            }
            break;
        }
    }

    private boolean isPositionInRatingView(float eventX, View ratingView) {
        return eventX > ratingView.getLeft() && eventX < ratingView.getRight();
    }

    private boolean isClickEvent(float startX, float startY, MotionEvent event) {
        float duration = event.getEventTime() - event.getDownTime();
        if (duration > MAX_CLICK_DURATION) {
            return false;
        }

        float differenceX = Math.abs(startX - event.getX());
        float differenceY = Math.abs(startY - event.getY());
        return !(differenceX > MAX_CLICK_DISTANCE || differenceY > MAX_CLICK_DISTANCE);
    }

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        mOnRatingChangeListener = onRatingChangeListener;
    }

    public boolean isTouchable() {
        return mIsTouchable;
    }

    public void setTouchable(boolean touchable) {
        this.mIsTouchable = touchable;
    }

    public void setClearRatingEnabled(boolean enabled) {
        this.mClearRatingEnabled = enabled;
    }

    public boolean isClearRatingEnabled() {
        return mClearRatingEnabled;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
