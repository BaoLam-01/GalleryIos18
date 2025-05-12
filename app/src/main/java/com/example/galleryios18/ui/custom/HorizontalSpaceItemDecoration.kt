package com.example.galleryios18.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val itemCount = parent.adapter?.itemCount ?: 0
        if (position == 0) {
            outRect.left = 0
            outRect.right = spacing / 2
        } else if (position == itemCount - 1) {
            outRect.left = spacing / 2
            outRect.right = 0
        } else {
            outRect.left = spacing / 2
            outRect.right = spacing / 2
        }
    }
}