package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.galleryios18.R
import com.example.galleryios18.data.models.CollectionItem
import com.example.galleryios18.databinding.ItemCustomizeCollectionBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import java.util.Collections



class CustomizeCollectionAdapter : BaseBindingAdapter<ItemCustomizeCollectionBinding>() {

    private val mDiffCallback = object : DiffUtil.ItemCallback<CollectionItem>() {
        override fun areItemsTheSame(
            oldItem: CollectionItem, newItem: CollectionItem
        ): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(
            oldItem: CollectionItem, newItem: CollectionItem
        ): Boolean {
            return oldItem.type == newItem.type
        }

    }
    private val mDiffer: AsyncListDiffer<CollectionItem> = AsyncListDiffer(this, mDiffCallback)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listCollection: List<CollectionItem>) {
        this.mDiffer.submitList(listCollection)
    }


    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemCustomizeCollectionBinding>, position: Int
    ) {
        holder.binding.tvCollection.text = mDiffer.currentList[position].title
    }

    fun onMove(fromPosition: Int, toPosition: Int) {
        // Sao chép danh sách hiện tại thành một danh sách có thể chỉnh sửa
        val modifiableList = mDiffer.currentList.toMutableList()

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(modifiableList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(modifiableList, i, i - 1)
            }
        }

        // Cập nhật danh sách cho mDiffer
        mDiffer.submitList(modifiableList)

        // Thông báo cho adapter (nếu cần)
        // notifyItemMoved(fromPosition, toPosition) // Nếu submitList rồi, thường không cần nữa
    }
    override val layoutIdItem: Int
        get() = R.layout.item_customize_collection
    override val sizeItem: Int
        get() = mDiffer.currentList.size
}