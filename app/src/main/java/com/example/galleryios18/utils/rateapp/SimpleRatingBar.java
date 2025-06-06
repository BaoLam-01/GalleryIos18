package com.example.galleryios18.utils.rateapp;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;


/**
 * Created by willy on 2017/5/10.
 */
interface SimpleRatingBar {

    void setNumStars(int numStars);

    int getNumStars();

    void setRating(float rating);

    float getRating();

    void setStarPadding(int ratingPadding);

    int getStarPadding();


    void setEmptyDrawable(Drawable drawable);

    void setEmptyDrawableRes(@DrawableRes int res);

    void setFilledDrawable(Drawable drawable);

    void setFilledDrawableRes(@DrawableRes int res);


}
