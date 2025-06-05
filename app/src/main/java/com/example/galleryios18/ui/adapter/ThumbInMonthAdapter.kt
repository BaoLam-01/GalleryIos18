package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.data.models.ItemThumbInMonth
import com.example.galleryios18.databinding.ItemThumbInMonthBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import timber.log.Timber

class ThumbInMonthAdapter : BaseBindingAdapter<ItemThumbInMonthBinding>() {
    private val mDiffCallback = object : DiffUtil.ItemCallback<ItemThumbInMonth>() {
        override fun areItemsTheSame(
            oldItem: ItemThumbInMonth, newItem: ItemThumbInMonth
        ): Boolean {
            return oldItem.month == newItem.month
        }

        override fun areContentsTheSame(
            oldItem: ItemThumbInMonth, newItem: ItemThumbInMonth
        ): Boolean {
            return oldItem.month == newItem.month
        }

    }
    private val mDiffer: AsyncListDiffer<ItemThumbInMonth> = AsyncListDiffer(this, mDiffCallback)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listMedia: List<ItemThumbInMonth>) {
        this.mDiffer.submitList(listMedia)
    }


    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemThumbInMonthBinding>, position: Int
    ) {
        Timber.e("LamPro | onBindViewHolderBase - bind item thumb in month")
        holder.binding.tvCountMedia.text = mDiffer.currentList[position].count.toString()
        Glide.with(holder.binding.imgMedia)
            .load(mDiffer.currentList[position].path)
            .signature(ObjectKey(mDiffer.currentList[position].month))
            .into(holder.binding.imgMedia)
    }

    override val layoutIdItem: Int
        get() = R.layout.item_thumb_in_month
    override val sizeItem: Int
        get() = mDiffer.currentList.size
}