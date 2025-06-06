package com.example.galleryios18.ui.custom

import android.graphics.Rect

fun Rect.isAdjacentTo(rect: Rect): Boolean {
    return (this.right == rect.left
            || this.top == rect.bottom
            || this.left == rect.right
            || this.bottom == rect.top)
}

fun Rect.intersects(rect: Rect): Boolean {
    return this.intersects(rect.left, rect.top, rect.right, rect.bottom)
}