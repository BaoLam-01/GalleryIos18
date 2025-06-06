package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMediaBinding
import com.example.galleryios18.utils.Utils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AllMediaAdapter : RecyclerView.Adapter<AllMediaAdapter.MediaViewHolder>() {
    private var size = SizeAllMedia.MEDIUM
    private var listener: IMediaClick? = null
    private var widthImage: Int = 0
    private var heightImage: Int = 0
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

    fun setSize(size: Int, context: Context) {
        this.size = size

        when (size) {
            SizeAllMedia.SMALLEST -> {
                widthImage = Utils.getScreenWidth(context) / Constant.SPAN_COUNT_SMALLEST
                heightImage = widthImage
            }

            SizeAllMedia.SMALL -> {
                widthImage = Utils.getScreenWidth(context) / Constant.SPAN_COUNT_SMALL
                heightImage = widthImage
            }

            SizeAllMedia.MEDIUM -> {
                widthImage = Utils.getScreenWidth(context) / Constant.SPAN_COUNT_MEDIUM
                heightImage = widthImage
            }

            SizeAllMedia.LARGE -> {
                widthImage = Utils.getScreenWidth(context)
                heightImage = widthImage
            }

            else -> {
                widthImage = Utils.getScreenWidth(context) / Constant.SPAN_COUNT_MEDIUM
                heightImage = widthImage
            }
        }

    }

    fun getSize(): Int {
        return size
    }

    fun setListener(iMediaClick: IMediaClick) {
        this.listener = iMediaClick
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
        @SuppressLint("CheckResult", "ClickableViewAccessibility")
        fun bindData(media: Media, position: Int) {
            Timber.e("LamPro | bindData - ")

            when (size) {
                SizeAllMedia.SMALLEST -> {
                    binding.root.setPadding(1)
                    binding.tvDuration.visibility = View.GONE
                }

                SizeAllMedia.SMALL -> {
                    binding.root.setPadding(5)
                    if (media.isImage) {
                        binding.tvDuration.visibility = View.GONE
                    } else {
                        binding.tvDuration.visibility = View.VISIBLE
                    }
                }

                SizeAllMedia.MEDIUM -> {
                    binding.root.setPadding(5)
                    if (media.isImage) {
                        binding.tvDuration.visibility = View.GONE
                    } else {
                        binding.tvDuration.visibility = View.VISIBLE
                    }
                }

                SizeAllMedia.LARGE -> {
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


            try {
                Glide.with(binding.imgThumbMedia.context)
                    .asBitmap()
                    .load(media.path)
                    .signature(ObjectKey(media.id))
                    .error(R.color.gray)
                    .apply(
                        RequestOptions()
                            .format(DecodeFormat.PREFER_RGB_565)
                            .override(widthImage, heightImage)
                    )
                    .thumbnail(0.1f)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Timber.e("LamPro | onLoadFailed - ${media.path}")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            model: Any,
                            target: Target<Bitmap>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            Timber.e("LamPro | onResourceReady - w: ${resource.width}, h: ${resource.height}")
                            return false
                        }

                    }).into(binding.imgThumbMedia)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val duration = media.duration
            val minute: Long = duration / 1000 / 60
            val second: Long = duration / 1000 % 60
            binding.tvDuration.text = "$minute:$second"


            binding.root.setOnClickListener { view ->
                if (size == SizeAllMedia.SMALLEST) {
                    // Lấy tọa độ gốc của itemView
                    val location = IntArray(2)
                    view.getLocationOnScreen(location)
                    val centerX = location[0] + view.width
                    val centerY = location[1] + view.height

                    listener?.onChangeLayoutToSmall(centerX.toFloat(), centerY.toFloat())
                } else {
                    listener?.onMediaClick(media, position)
                }
            }
        }

    }


    fun getItemTime(position: Int): String {
        val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val monthYear = formatter.format(Date(listMedia.currentList[position].dateTaken))
        return monthYear
    }

    fun getFirstItemOfMonth(month: Long): Int {
        for (i in listMedia.currentList.indices) {
            if (Utils.getMonthTimestamp(listMedia.currentList[i].dateTaken) == Utils.getMonthTimestamp(
                    month
                )
            ) {
                return i
            }
        }
        return listMedia.currentList.size
    }

    object SizeAllMedia {
        const val SMALLEST = 0
        const val SMALL = 1
        const val MEDIUM = 2
        const val LARGE = 3
    }

    interface IMediaClick {
        fun onChangeLayoutToSmall(x: Float, y: Float)
        fun onMediaClick(media: Media, position: Int)
    }
}