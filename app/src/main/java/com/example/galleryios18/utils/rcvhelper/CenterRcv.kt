package com.example.galleryios18.utils.rcvhelper

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import com.example.galleryios18.App
import com.example.galleryios18.utils.Utils

object CenterRcv {
    const val DIMENSION = 2
    const val HORIZONTAL = 0
    const val VERTICAL: Int = 1

    fun setMarginItem(sizeList: Int, pos: Int, itemView: View) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (pos == 0) {
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.setMargins(
                marginLeftRight(itemView.measuredWidth.toFloat()).toInt(),
                Utils.dpToPx(1f),
                Utils.dpToPx(1f),
                Utils.dpToPx(1f)
            )
        } else if (pos == sizeList - 1) {
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.setMargins(
                Utils.dpToPx(1f),
                Utils.dpToPx(1f),
                marginLeftRight(itemView.measuredWidth.toFloat()).toInt(),
                Utils.dpToPx(1f)
            )
        } else {
            params.setMargins(
                Utils.dpToPx(1f),
                Utils.dpToPx(1f),
                Utils.dpToPx(1f),
                Utils.dpToPx(1f)
            )
        }
        itemView.setLayoutParams(params)
    }

    private fun marginLeftRight(widthItem: Float): Float {
        return (Utils.getScreenWidth(App.instance) - widthItem) / 2
    }


    fun scrollToCenter(
        layoutManager: LinearLayoutManager?,
        recyclerList: RecyclerView,
        clickPosition: Int
    ) {
        if (clickPosition == -1) {
            return
        }
        try {
            val smoothScroller: SmoothScroller? = createSnapScroller(recyclerList, layoutManager)
            if (smoothScroller != null) {
                smoothScroller.setTargetPosition(clickPosition)
                layoutManager!!.startSmoothScroll(smoothScroller)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createSnapScroller(
        mRecyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager?
    ): LinearSmoothScroller? {
        if (layoutManager !is ScrollVectorProvider) {
            return null
        }
        return object : LinearSmoothScroller(mRecyclerView.getContext()) {
            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                val snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView)
                action.update(
                    snapDistances[HORIZONTAL],
                    snapDistances[VERTICAL],
                    200,
                    mDecelerateInterpolator
                )
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return 0.015f
            }
        }
    }

    private fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(DIMENSION)
        if (layoutManager.canScrollHorizontally()) {
            out[HORIZONTAL] = distanceToCenter(
                layoutManager, targetView,
                OrientationHelper.createHorizontalHelper(layoutManager)
            )
        }

        if (layoutManager.canScrollVertically()) {
            out[VERTICAL] = distanceToCenter(
                layoutManager, targetView,
                OrientationHelper.createHorizontalHelper(layoutManager)
            )
        }
        return out
    }

    private fun distanceToCenter(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View,
        helper: OrientationHelper
    ): Int {
        val childCenter = (helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2))
        val containerCenter: Int
        if (layoutManager.getClipToPadding()) {
            containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2
        } else {
            containerCenter = helper.getEnd() / 2
        }
        return childCenter - containerCenter
    }
}
