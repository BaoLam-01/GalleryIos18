package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMediaYearBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class YearMediaAdapter : BaseBindingAdapter<ItemMediaYearBinding>() {
    private var listenter: IItemYearClick? = null

    private val mDiffCallback = object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(
            oldItem: Media, newItem: Media
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Media, newItem: Media
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }
    private val mDiffer: AsyncListDiffer<Media> = AsyncListDiffer(this, mDiffCallback)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listMedia: List<Media>) {
        Timber.e("LamPro | setData - year setdata: ${listMedia.size}")
        this.mDiffer.submitList(listMedia)
    }

    fun setListener(iItemYearClick: IItemYearClick) {
        this.listenter = iItemYearClick
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemMediaYearBinding>, position: Int
    ) {
        val media = mDiffer.currentList[position]
        val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
        val year = formatter.format(Date(media.dateTaken))
        holder.binding.tvYear.text = year

        Glide.with(holder.binding.imgThumbMedia.context).load(media.path)
            .signature(ObjectKey(media.id)).placeholder(R.color.white).error(R.color.white)
            .override(Utils.getScreenWidth(holder.binding.imgThumbMedia.context))
            .into(holder.binding.imgThumbMedia)

        holder.binding.root.setOnClickListener { listenter?.onItemYearClick(media) }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_media_year
    override val sizeItem: Int
        get() = mDiffer.currentList.size

    interface IItemYearClick {
        fun onItemYearClick(media: Media)
    }
}