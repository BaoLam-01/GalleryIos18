package com.example.galleryios18.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.data.models.ItemMediaTypeUtilities
import com.example.galleryios18.databinding.ItemMediaTypeUtilitiesBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.gone
import com.example.galleryios18.utils.show

class MediaTypeUtilitiesAdapter : BaseBindingAdapter<ItemMediaTypeUtilitiesBinding>() {
    private val mDiffCallback = object : DiffUtil.ItemCallback<ItemMediaTypeUtilities>() {
        override fun areItemsTheSame(
            oldItem: ItemMediaTypeUtilities, newItem: ItemMediaTypeUtilities
        ): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(
            oldItem: ItemMediaTypeUtilities, newItem: ItemMediaTypeUtilities
        ): Boolean {
            return oldItem.type == newItem.type
        }

    }
    private val mDiffer: AsyncListDiffer<ItemMediaTypeUtilities> =
        AsyncListDiffer(this, mDiffCallback)

    fun setData(listItem: List<ItemMediaTypeUtilities>) {
        mDiffer.submitList(listItem)
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemMediaTypeUtilitiesBinding>, position: Int
    ) {
        val itemMediaTypeUtilities = mDiffer.currentList[position]
        holder.binding.tvItem.text =
            holder.binding.tvItem.context.getString(itemMediaTypeUtilities.title)
        holder.binding.tvCount.text = itemMediaTypeUtilities.count.toString()
        holder.binding.tvItem.setCompoundDrawablesRelativeWithIntrinsicBounds(
            itemMediaTypeUtilities.ic, 0, 0, 0
        )
        if (itemMediaTypeUtilities.type == Constant.HIDDEN || itemMediaTypeUtilities.type == Constant.RECENTLY_DELETED) {
            holder.binding.imgLock.show()
            holder.binding.tvItem.gone()
        } else {
            holder.binding.imgLock.gone()
            holder.binding.tvItem.show()
        }

        if (position == mDiffer.currentList.size - 1) {
            holder.binding.line.gone()
        } else {
            holder.binding.line.show()
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_media_type_utilities
    override val sizeItem: Int
        get() = mDiffer.currentList.size
}