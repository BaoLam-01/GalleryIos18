package com.example.galleryios18.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Image
import com.example.galleryios18.databinding.ItemMediaBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter

class MediaAdapter : BaseBindingAdapter<ItemMediaBinding>() {

    private val mDiffCallback = object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(
            oldItem: Image,
            newItem: Image
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Image,
            newItem: Image
        ): Boolean {
            return oldItem == newItem
        }

    }
    val listImage: AsyncListDiffer<Image> = AsyncListDiffer(this, mDiffCallback)


    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemMediaBinding>,
        position: Int
    ) {
    }

    override val layoutIdItem: Int
        get() = R.layout.item_media
    override val sizeItem: Int
        get() = listImage.currentList.size
}