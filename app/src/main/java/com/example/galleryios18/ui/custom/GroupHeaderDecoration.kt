package com.example.galleryios18.ui.custom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import kotlin.math.max

class GroupHeaderDecoration(
    private val getGroup: (position: Int) -> String,
) : RecyclerView.ItemDecoration() {

    private val textPadding = 30f
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 42f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        color = Color.WHITE
        alpha = 180 // translucent white
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        Timber.e("LamPro | onDrawOver -")
        val childCount = parent.childCount
        if (childCount == 0) return

        var lastGroup = ""
        var lastHeaderTop = -1f // lưu vị trí top header cuối cùng đã vẽ

        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            if (view.bottom < parent.paddingTop) {
                continue
            }
            val position = parent.getChildAdapterPosition(view)
            if (position == RecyclerView.NO_POSITION) continue

            val group = getGroup(position)
            if (group != lastGroup) {
                val top = view.top.toFloat().coerceAtLeast(0f)

                // Nếu header ở cùng vị trí top hoặc rất gần với header trước đó => bỏ qua (để tránh chồng)
                if (lastHeaderTop != -1f && kotlin.math.abs(top - lastHeaderTop) < 10f) {
                    // Đã vẽ header ở vị trí này rồi, bỏ qua
                    continue
                }

                val text = group
                val textWidth = textPaint.measureText(text)
                val fontMetrics = textPaint.fontMetrics
                val textHeight = fontMetrics.bottom - fontMetrics.top

                val horizontalPadding = 30f
                val verticalPadding = 20f

                val bgLeft = textPadding
                val bgTop = max(top, parent.paddingTop.toFloat())
                val bgRight = bgLeft + textWidth + horizontalPadding * 2
                val bgBottom = bgTop + textHeight + verticalPadding

                c.drawRoundRect(bgLeft, bgTop + 10, bgRight, bgBottom, 24f, 24f, backgroundPaint)

                val textBaseline =
                    bgTop + (bgBottom - bgTop + 10 - textHeight) / 2f - fontMetrics.top

                c.drawText(text, bgLeft + horizontalPadding, textBaseline, textPaint)

                lastGroup = group
                lastHeaderTop = top // cập nhật vị trí header mới vừa vẽ
            }
        }
    }

}
