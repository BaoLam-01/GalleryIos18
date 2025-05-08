package com.example.galleryios18.utils

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat

fun TextView.setTextViewDrawableColor(color: Int) {
    for (drawable in compoundDrawables) {
        drawable?.mutate()
        drawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        val drawables = compoundDrawablesRelative

        for (i in drawables.indices) {
            drawables[i]?.let { originalDrawable ->
                // Create a new drawable with the desired color
                val wrappedDrawable = DrawableCompat.wrap(originalDrawable).mutate()
                DrawableCompat.setTint(wrappedDrawable, color)

                // Set the new drawable to the TextView
                when (i) {
                    0 -> setCompoundDrawablesRelativeWithIntrinsicBounds(
                        wrappedDrawable,
                        drawables[1],
                        drawables[2],
                        drawables[3]
                    )

                    1 -> setCompoundDrawablesRelativeWithIntrinsicBounds(
                        drawables[0],
                        wrappedDrawable,
                        drawables[2],
                        drawables[3]
                    )

                    2 -> setCompoundDrawablesRelativeWithIntrinsicBounds(
                        drawables[0],
                        drawables[1],
                        wrappedDrawable,
                        drawables[3]
                    )

                    3 -> setCompoundDrawablesRelativeWithIntrinsicBounds(
                        drawables[0],
                        drawables[1],
                        drawables[2],
                        wrappedDrawable
                    )
                }
            }
        }
    }
}