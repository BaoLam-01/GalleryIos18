package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMediaBinding
import com.example.galleryios18.utils.Utils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaAdapter : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {
    private var size = SizeAllMedia.MEDIUM
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

    fun setSize(size: Int) {
        this.size = size
    }

    fun getSize(): Int {
        return size
    }

    fun setListener(iMediaClick: IMediaClick) {
        this.listenter = iMediaClick
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(media: Media, position: Int) {

            val widthImage: Int
            val heightImage: Int

            when (size) {
                SizeAllMedia.SMALLEST -> {
                    widthImage = Utils.getScreenWidth(binding.imgThumbMedia.context) / 16
                    heightImage = widthImage
                    binding.root.setPadding(1)
                    binding.tvDuration.visibility = View.GONE
                }

                SizeAllMedia.SMALL -> {
                    widthImage = Utils.getScreenWidth(binding.imgThumbMedia.context) / 5
                    heightImage = widthImage
                    binding.root.setPadding(5)
                    if (media.isImage) {
                        binding.tvDuration.visibility = View.GONE
                    } else {
                        binding.tvDuration.visibility = View.VISIBLE
                    }
                }

                SizeAllMedia.MEDIUM -> {
                    widthImage = Utils.getScreenWidth(binding.imgThumbMedia.context) / 3
                    heightImage = widthImage
                    binding.root.setPadding(5)
                    if (media.isImage) {
                        binding.tvDuration.visibility = View.GONE
                    } else {
                        binding.tvDuration.visibility = View.VISIBLE
                    }
                }

                SizeAllMedia.LARGE -> {
                    widthImage = Utils.getScreenWidth(binding.imgThumbMedia.context)
                    heightImage =
                        (widthImage * (media.height.toFloat() / media.width.toFloat()).toFloat()).toInt()
                    binding.root.setPadding(0, 5, 0, 5)
                    if (media.isImage) {
                        binding.tvDuration.visibility = View.GONE
                    } else {
                        binding.tvDuration.visibility = View.VISIBLE
                    }
                }

                else -> {
                    widthImage = Utils.getScreenWidth(binding.imgThumbMedia.context) / 3
                    heightImage = widthImage
                    binding.root.setPadding(5)
                    if (media.isImage) {
                        binding.tvDuration.visibility = View.GONE
                    } else {
                        binding.tvDuration.visibility = View.VISIBLE
                    }
                }
            }

            binding.imgThumbMedia.layoutParams?.let {
                it.width = widthImage
                it.height = heightImage
                binding.imgThumbMedia.layoutParams = it
            }

            Timber.e("LamPro | bindData - w:h = ${media.width}:${media.height}")
            Timber.e("LamPro | bindData - width image: $widthImage")
            Timber.e("LamPro | bindData - height image: $heightImage")

//            binding.imgThumbMedia.post {

            Glide.with(binding.imgThumbMedia.context).load(media.path)
                .signature(ObjectKey(media.id)).placeholder(R.color.transparent)
                .error(R.color.transparent)
                .centerCrop()
                .override(widthImage / 2, heightImage / 2)
                .into(binding.imgThumbMedia)
            val duration = media.duration
            val minute: Long = duration / 1000 / 60
            val second: Long = duration / 1000 % 60
            binding.tvDuration.text = "$minute:$second"

            binding.root.setOnClickListener {
                listenter?.onMediaClick(media, position)
            }
//            }
        }

    }


    fun getItemTime(position: Int): String {
        val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val monthYear = formatter.format(Date(listMedia.currentList[position].dateAdded * 1000))
        return monthYear
    }

    object SizeAllMedia {
        const val SMALLEST = 0
        const val SMALL = 1
        const val MEDIUM = 2
        const val LARGE = 3
    }

    interface IMediaClick {
        fun onMediaClick(media: Media, position: Int)
    }
}