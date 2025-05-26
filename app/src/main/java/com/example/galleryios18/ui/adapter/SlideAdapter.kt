package com.example.galleryios18.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMediaShowBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils
import timber.log.Timber

class SlideAdapter : BaseBindingAdapter<ItemMediaShowBinding>() {
    private val mDiffCallback = object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(
            oldItem: Media,
            newItem: Media
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Media,
            newItem: Media
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val mDiffer: AsyncListDiffer<Media> = AsyncListDiffer(this, mDiffCallback)
    fun setData(listMedia: List<Media>) {
        mDiffer.submitList(listMedia)
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemMediaShowBinding>,
        position: Int
    ) {
        Timber.e("LamPro | onBindViewHolderBase - $position")
        Glide.with(holder.itemView.context).load(mDiffer.currentList[position].path)
            .signature(ObjectKey(mDiffer.currentList[position].id))
            .override(Utils.getScreenWidth(holder.binding.imgShow.context))
            .into(holder.binding.imgShow)
    }

    override val layoutIdItem: Int
        get() = R.layout.item_media_show
    override val sizeItem: Int
        get() = mDiffer.currentList.size
}