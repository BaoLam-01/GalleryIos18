package com.example.galleryios18.feature

import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.R
import com.example.galleryios18.interfaces.ItemTouchListener

class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {
    private var mListener: ItemTouchListener? = null

    fun setListener(listener: ItemTouchListener?) {
        this.mListener = listener
    }

    override fun isLongPressDragEnabled(): Boolean = true
    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlag, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mListener?.onMove(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
            val itemView = viewHolder.itemView
            itemView.animate()
                .scaleX(1.03f)
                .scaleY(1.03f)
                .setDuration(300) // 500ms = 0.5s
                .start()

            itemView.animate()
                .translationZ(40f)
                .setDuration(300)
                .start()

            itemView.setBackgroundResource(R.color.color_9CFFFFFF)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        val itemView = viewHolder.itemView
        itemView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start()

        itemView.animate()
            .translationZ(0f)
            .setDuration(300)
            .start()

        itemView.setBackgroundColor(Color.TRANSPARENT)
    }

}
