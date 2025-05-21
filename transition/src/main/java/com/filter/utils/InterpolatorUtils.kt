package com.filter.utils

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.CycleInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.filter.helper.InterpolatorType

object InterpolatorUtils {
    fun getInterpolator(type: InterpolatorType, input: Float): Float {
        return when (type) {
            InterpolatorType.BOUNCE -> BounceInterpolator().getInterpolation(input)
            InterpolatorType.ACCELERATE -> AccelerateInterpolator().getInterpolation(input)
            InterpolatorType.DECELERATE -> DecelerateInterpolator().getInterpolation(input)
            InterpolatorType.ACCELERATE_DECELERATE -> AccelerateDecelerateInterpolator().getInterpolation(input)
            InterpolatorType.ANTICIPATE -> AnticipateInterpolator().getInterpolation(input)
            InterpolatorType.OVERSHOOT -> OvershootInterpolator().getInterpolation(input)
            InterpolatorType.ANTICIPATE_OVERSHOOT -> AnticipateOvershootInterpolator().getInterpolation(input)
            InterpolatorType.CYCLE -> CycleInterpolator(1f).getInterpolation(input)
            InterpolatorType.LINEAR -> LinearInterpolator().getInterpolation(1f)
            else -> {
                input.coerceAtMost(1f)
            }
        }
    }
}