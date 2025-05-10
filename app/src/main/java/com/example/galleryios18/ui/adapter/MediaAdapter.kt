package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMediaBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils
import timber.log.Timber

class MediaAdapter : BaseBindingAdapter<ItemMediaBinding>() {

    private val mDiffCallback = object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(
            oldItem: Media,
            newItem: Media
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Media,
            newItem: Media
        ): Boolean {
            return oldItem == newItem
        }

    }

    val listMedia: AsyncListDiffer<Media> = AsyncListDiffer(this, mDiffCallback)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listMedia: List<Media>) {
        this.listMedia.submitList(listMedia)
        notifyDataSetChanged()
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemMediaBinding>,
        position: Int
    ) {
        Glide.with(holder.binding.imgThumbMedia.context)
            .load(listMedia.currentList[position].path)
            .signature(ObjectKey(listMedia.currentList[position].id))
            .placeholder(R.drawable.imagepicker_image_placeholder)
            .error(R.drawable.imagepicker_image_error)
            .override(Utils.getScreenWidth(holder.binding.imgThumbMedia.context) / 3)
            .into(
                holder.binding.imgThumbMedia
            )
        val duration = listMedia.currentList[position].duration
        val minute: Long = duration / 1000 / 60
        val second: Long = duration / 1000 % 60
        Timber.e("LamPro | onBindViewHolderBase - $minute:$second")
        holder.binding.tvDuration.text = "$minute:$second"
        if (listMedia.currentList[position].isImage) {
            holder.binding.tvDuration.visibility = View.INVISIBLE
        } else {
            holder.binding.tvDuration.visibility = View.VISIBLE
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_media
    override val sizeItem: Int
        get() = listMedia.currentList.size
}