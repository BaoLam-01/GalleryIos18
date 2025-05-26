package com.example.galleryios18.ui.custom

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class FadeItemAnimator : DefaultItemAnimator() {
    override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?,
                               fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        newHolder?.itemView?.alpha = 0f
        newHolder?.itemView?.animate()
            ?.alpha(1f)
            ?.setDuration(1000) // 1 giây
            ?.start()
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY)
    }
}
