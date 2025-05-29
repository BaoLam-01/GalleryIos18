package com.example.galleryios18.ui.custom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.Typeface
import android.media.effect.EffectFactory
import android.os.Build
import androidx.recyclerview.widget.RecyclerView

class GroupHeaderDecoration(
    private val getGroup: (position: Int) -> String
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 100 // px
    private val textPadding = 30f
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 42f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private val backgroundPaint = Paint().apply {
        color = Color.WHITE
        alpha = 30 // translucent white
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        if (childCount == 0) return

        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            val group = getGroup(position)
            var lastGroup = ""

            if (group != lastGroup) {
            val top = view.top.toFloat().coerceAtLeast(0f)


            val text = group
            val textWidth = textPaint.measureText(text)
            val fontMetrics = textPaint.fontMetrics
            val textHeight = fontMetrics.bottom - fontMetrics.top

// Padding xung quanh chữ
            val horizontalPadding = 30f
            val verticalPadding = 20f

// Tính toạ độ của nền
            val bgLeft = textPadding
            val bgTop = top + headerHeight / 2f - textHeight / 2f - verticalPadding / 2
            val bgRight = bgLeft + textWidth + horizontalPadding * 2
            val bgBottom = bgTop + textHeight + verticalPadding

// Vẽ nền bo tròn
            c.drawRoundRect(bgLeft, bgTop, bgRight, bgBottom, 24f, 24f, backgroundPaint)

// Tính toạ độ vẽ text để nằm giữa theo chiều dọc
            val textBaseline = bgTop + (bgBottom - bgTop - textHeight) / 2f - fontMetrics.top

// Vẽ chữ ở giữa rect
            c.drawText(text, bgLeft + horizontalPadding, textBaseline, textPaint)
                lastGroup = group
            }
        }
    }
}
