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
    private var listenter: IMediaClick? = null

    private val mDiffCallback = object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(
            oldItem: Media,
            newItem: Media,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Media,
            newItem: Media,
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }

    private val listMedia: AsyncListDiffer<Media> = AsyncListDiffer(this, mDiffCallback)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listMedia: List<Media>) {
        this.listMedia.submitList(listMedia)
    }

    fun setStyle(style: Int) {
        this.style = style
    }

    fun setListener(iMediaClick: IMediaClick) {
        this.listenter = iMediaClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MediaViewHolder {
        val binding: ViewDataBinding
        if (viewType == StyleRecycler.ALL) {
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
        holder.bindData(listMedia.currentList[position], position)
    }

    override fun getItemCount(): Int {
        return listMedia.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return style
    }

    inner class MediaViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(media: Media, position: Int) {
            if (getItemViewType(position) == StyleRecycler.ALL) {
                binding as ItemMediaBinding
                Glide.with(binding.imgThumbMedia.context)
                    .load(media.path)
                    .signature(ObjectKey(media.id))
                    .placeholder(R.color.white)
                    .error(R.color.white)
                    .override(Utils.getScreenWidth(binding.imgThumbMedia.context) / 3)
                    .into(binding.imgThumbMedia)
                val duration = media.duration
                val minute: Long = duration / 1000 / 60
                val second: Long = duration / 1000 % 60
                binding.tvDuration.text = "$minute:$second"
                if (media.isImage) {
                    binding.tvDuration.visibility = View.GONE
                } else {
                    binding.tvDuration.visibility = View.VISIBLE
                }
            } else {
                binding as ItemMediaMonthBinding
                Glide.with(binding.imgThumbMedia.context)
                    .load(media.path)
                    .signature(ObjectKey(media.id))
                    .placeholder(R.color.white)
                    .error(R.color.white)
                    .override(Utils.getScreenWidth(binding.imgThumbMedia.context))
                    .into(binding.imgThumbMedia)
            }

            binding.root.setOnClickListener {
                listenter?.onMediaClick(media, position)
            }

        }

    }


    object StyleRecycler {
        const val ALL = 1
        const val MONTH = 0
    }

    interface IMediaClick {
        fun onMediaClick(media: Media, position: Int)
    }
}