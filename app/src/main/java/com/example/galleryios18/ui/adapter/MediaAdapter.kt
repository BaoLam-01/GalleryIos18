package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMediaBinding
import com.example.galleryios18.databinding.ItemMediaMonthBinding
import com.example.galleryios18.utils.Utils
import timber.log.Timber

class MediaAdapter : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {
    private var style = StyleRecycler.ALL

    private val mDiffCallback = object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(
            oldItem: Media,
            newItem: Media,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Media,
            newItem: Media,
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val listMedia: AsyncListDiffer<Media> = AsyncListDiffer(this, mDiffCallback)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listMedia: List<Media>) {
        this.listMedia.submitList(listMedia)
        notifyDataSetChanged()
    }

    fun setStyle(style: StyleRecycler) {
        this.style = style
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MediaViewHolder {
        val binding: ViewDataBinding
        if (style == StyleRecycler.ALL) {
            binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        } else {
            binding =
                ItemMediaMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MediaViewHolder,
        position: Int,
    ) {
        holder.bindData(listMedia.currentList[position], holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return listMedia.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class MediaViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(media: Media, position: Int) {
            if (style == StyleRecycler.ALL) {
                binding as ItemMediaBinding
                Glide.with(binding.imgThumbMedia.context)
                    .load(media.path)
                    .signature(ObjectKey(media.id))
                    .placeholder(R.drawable.imagepicker_image_placeholder)
                    .error(R.drawable.imagepicker_image_error)
                    .override(Utils.getScreenWidth(binding.imgThumbMedia.context) / 3)
                    .into(binding.imgThumbMedia)
                val duration = media.duration
                val minute: Long = duration / 1000 / 60
                val second: Long = duration / 1000 % 60
                Timber.e("LamPro | onBindViewHolderBase - $minute:$second")
                binding.tvDuration.text = "$minute:$second"
                if (media.isImage) {
                    binding.tvDuration.visibility = View.INVISIBLE
                } else {
                    binding.tvDuration.visibility = View.VISIBLE
                }
            } else {
                binding as ItemMediaMonthBinding
                Glide.with(binding.imgThumbMedia.context)
                    .load(media.path)
                    .signature(ObjectKey(media.id))
                    .placeholder(R.drawable.imagepicker_image_placeholder)
                    .error(R.drawable.imagepicker_image_error)
                    .override(Utils.getScreenWidth(binding.imgThumbMedia.context))
                    .into(binding.imgThumbMedia)
            }

        }

    }


    enum class StyleRecycler {
        ALL,
        MONTH
    }
}