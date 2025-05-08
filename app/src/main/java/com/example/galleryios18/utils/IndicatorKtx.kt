package com.example.galleryios18.utils

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.galleryios18.R

fun LinearLayout.setIndicator(index: Int, context: Context) {
    val childCount = childCount
    for (i in 0 until childCount) {
        val imageView = this[i] as ImageView
        if (i == index) {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.indicator_active)
            )
        } else {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.indicator_inactive)
            )
        }
    }
}

fun LinearLayout.setupIndicators(
    itemCount: Int,
    checkVertical: Boolean,
    context: Context,
) {
    val itemCountMax = if (itemCount > 5) {
        5
    } else itemCount
    val indicators = arrayOfNulls<ImageView>(itemCountMax)
    val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    val margin = ViewUtils.dpToPx(context, 6f).toInt()
    if (checkVertical) layoutParams.setMargins(0, margin, 0, margin)
    else layoutParams.setMargins(margin, 0, margin, 0)

    for (i in indicators.indices) {
        indicators[i] = ImageView(context.applicationContext)
        indicators[i].apply {
            this?.setImageDrawable(
                ContextCompat.getDrawable(
                    context.applicationContext,
                    R.drawable.indicator_inactive
                )
            )
            this?.layoutParams = layoutParams
        }
        addView(indicators[i])
    }
}

fun LinearLayout.setupIndicatorsLock(
    itemCount: Int,
    checkVertical: Boolean,
    context: Context,
) {
    val indicators = arrayOfNulls<ImageView>(itemCount)
    val layoutParams: LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    val margin = ViewUtils.dpToPx(context, 6f).toInt()
    if (checkVertical) layoutParams.setMargins(0, margin, 0, margin)
    else layoutParams.setMargins(margin, 0, margin, 0)

    for (i in indicators.indices) {
        indicators[i] = ImageView(context.applicationContext)
        indicators[i].apply {
            this?.setImageDrawable(
                ContextCompat.getDrawable(
                    context.applicationContext,
                    R.drawable.indicator_inactive_view_lock
                )
            )
            this?.layoutParams = layoutParams
        }
        addView(indicators[i])
    }
}

fun LinearLayout.setIndicatorIsEnterPass(itemCount: Int, context: Context) {
    val childCount = childCount
    for (i in 0 until childCount) {
        val imageView = this[i] as ImageView
        if (i <= itemCount) {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.indicator_active_view_lock)
            )
        } else {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.indicator_inactive_view_lock)
            )
        }
    }
}