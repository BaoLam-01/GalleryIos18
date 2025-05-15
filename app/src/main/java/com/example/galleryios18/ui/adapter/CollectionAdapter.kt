package com.example.galleryios18.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.galleryios18.R
import com.example.galleryios18.data.models.CollectionItem
import com.example.galleryios18.databinding.ItemCollectionBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter

class CollectionAdapter : BaseBindingAdapter<ItemCollectionBinding>() {
    private var listCollection: AsyncListDiffer<CollectionItem>
    private val mDiffCallback = object : DiffUtil.ItemCallback<CollectionItem>() {
        override fun areItemsTheSame(
            oldItem: CollectionItem,
            newItem: CollectionItem
        ): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(
            oldItem: CollectionItem,
            newItem: CollectionItem
        ): Boolean {
            return oldItem.type == newItem.type
        }

    }

    init {
        listCollection = AsyncListDiffer(this, mDiffCallback)
    }

    fun setData(list: List<CollectionItem>) {
        listCollection.submitList(list)
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemCollectionBinding>,
        position: Int
    ) {
        holder.binding.tvTitle.text = listCollection.currentList[position].title
    }

    override val layoutIdItem: Int
        get() = R.layout.item_collection
    override val sizeItem: Int
        get() = listCollection.currentList.size
}