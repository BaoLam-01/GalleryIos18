package com.example.galleryios18.ui.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.galleryios18.R
import com.example.galleryios18.data.models.AlbumMemories
import com.example.galleryios18.databinding.ItemStoryBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils
import timber.log.Timber

class MemoriesAdapter : BaseBindingAdapter<ItemStoryBinding>() {

    private val mDiffCallback = object : DiffUtil.ItemCallback<AlbumMemories>() {
        override fun areItemsTheSame(
            oldItem: AlbumMemories,
            newItem: AlbumMemories
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AlbumMemories,
            newItem: AlbumMemories
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val mDiffer: AsyncListDiffer<AlbumMemories> = AsyncListDiffer(this, mDiffCallback)

    fun setData(listAlbumStory: List<AlbumMemories>) {
        mDiffer.submitList(listAlbumStory)
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemStoryBinding>,
        position: Int
    ) {
        val imgWidth = Utils.getScreenWidth(holder.binding.imgMedia.context) - Utils.dpToPx(50f)
        val height = imgWidth * 4 / 3
        Timber.e("LamPro | onBindViewHolderBase - thumb : ${mDiffer.currentList[position].thumb}")
        Glide.with(holder.binding.imgMedia)
            .load(mDiffer.currentList[position].thumb)
            .override(imgWidth, height)
            .into(holder.binding.imgMedia)

        val layoutParams = holder.binding.imgMedia.layoutParams as ConstraintLayout.LayoutParams
        if (position == 0) {
            layoutParams.marginStart = Utils.dpToPx(16f)
            layoutParams.marginEnd = Utils.dpToPx(0f)
        } else if (position == mDiffer.currentList.size - 1) {
            layoutParams.marginStart = Utils.dpToPx(0f)
            layoutParams.marginEnd = Utils.dpToPx(16f)
        } else {
            layoutParams.marginStart = Utils.dpToPx(0f)
            layoutParams.marginEnd = Utils.dpToPx(0f)
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_story

    override val sizeItem: Int
        get() = mDiffer.currentList.size
}