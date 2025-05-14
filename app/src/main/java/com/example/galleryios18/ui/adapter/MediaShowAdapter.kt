package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMediaShowBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils

class MediaShowAdapter : BaseBindingAdapter<ItemMediaShowBinding>() {

    private var listener: (() -> Unit)? = null

    private var listMediaShow: AsyncListDiffer<Media>
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
            return oldItem.id == newItem.id
        }
    }

    init {
        listMediaShow = AsyncListDiffer(this, mDiffCallback)
    }

    fun setData(listMedia: List<Media>) {
        listMediaShow.submitList(listMedia)
    }

    fun setListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemMediaShowBinding>,
        position: Int
    ) {
        Glide.with(holder.binding.imgShow.context)
            .load(listMediaShow.currentList[position].path)
            .signature(ObjectKey(listMediaShow.currentList[position].id))
            .placeholder(R.color.white)
            .error(R.color.white)
            .override(Utils.getScreenWidth(holder.binding.imgShow.context))
            .into(holder.binding.imgShow)

        holder.binding.imgShow.setOnClickListener {
            listener?.invoke()
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_media_show
    override val sizeItem: Int
        get() = listMediaShow.currentList.size

}