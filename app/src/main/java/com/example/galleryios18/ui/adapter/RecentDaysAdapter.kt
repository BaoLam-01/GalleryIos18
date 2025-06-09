package com.example.galleryios18.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.galleryios18.R
import com.example.galleryios18.data.models.AlbumRecent
import com.example.galleryios18.databinding.ItemRecentDaysBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils
import java.text.SimpleDateFormat
import java.util.Locale

class RecentDaysAdapter : BaseBindingAdapter<ItemRecentDaysBinding>() {
    private val mDiffCallback = object : DiffUtil.ItemCallback<AlbumRecent>() {
        override fun areItemsTheSame(
            oldItem: AlbumRecent,
            newItem: AlbumRecent
        ): Boolean {
            return oldItem.day == newItem.day
        }

        override fun areContentsTheSame(
            oldItem: AlbumRecent,
            newItem: AlbumRecent
        ): Boolean {
            return oldItem.day == newItem.day
        }

    }

    private val mDiffer = AsyncListDiffer(this, mDiffCallback)

    fun setData(listAlbum: List<AlbumRecent>) {
        mDiffer.submitList(listAlbum)
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemRecentDaysBinding>,
        position: Int
    ) {
        if (position == 0) {
            val layoutParams = holder.binding.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = Utils.dpToPx(22f)
            holder.binding.root.layoutParams = layoutParams
        } else if (position == mDiffer.currentList.size - 1) {
            val layoutParams = holder.binding.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginEnd = Utils.dpToPx(22f)
            holder.binding.root.layoutParams = layoutParams
        } else {
            val layoutParams = holder.binding.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = Utils.dpToPx(8f)
            layoutParams.marginEnd = Utils.dpToPx(8f)
            holder.binding.root.layoutParams = layoutParams
        }

        val albumRecent = mDiffer.currentList[position]
        val formatter = SimpleDateFormat("dd MMMM", Locale.getDefault())
        val textDay = formatter.format(albumRecent.day)
        holder.binding.tvDayMonth.text = textDay
        Glide.with(holder.binding.imgMedia)
            .load(albumRecent.thumb)
            .into(holder.binding.imgMedia)
    }

    override val layoutIdItem: Int
        get() = R.layout.item_recent_days
    override val sizeItem: Int
        get() = mDiffer.currentList.size
}