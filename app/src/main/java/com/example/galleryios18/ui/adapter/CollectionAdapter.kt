package com.example.galleryios18.ui.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.data.models.AlbumRecent
import com.example.galleryios18.data.models.AlbumMemories
import com.example.galleryios18.data.models.CollectionItem
import com.example.galleryios18.data.models.ItemMediaTypeUtilities
import com.example.galleryios18.databinding.ItemCollectionBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils

class CollectionAdapter : BaseBindingAdapter<ItemCollectionBinding>() {
    private var listCollection: AsyncListDiffer<CollectionItem>

    private var recentDaysAdapter: RecentDaysAdapter
    private var memoriesAdapter: MemoriesAdapter
    private var mediaTypeUtilitiesAdapter: MediaTypeUtilitiesAdapter
    private val mDiffCallback = object : DiffUtil.ItemCallback<CollectionItem>() {
        override fun areItemsTheSame(
            oldItem: CollectionItem,
            newItem: CollectionItem
        ): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(
            oldItem: CollectionItem,
            newItem: CollectionItem
        ): Boolean {
            return oldItem.type == newItem.type
        }

    }

    init {
        listCollection = AsyncListDiffer(this, mDiffCallback)
        recentDaysAdapter = RecentDaysAdapter()
        memoriesAdapter = MemoriesAdapter()
        mediaTypeUtilitiesAdapter = MediaTypeUtilitiesAdapter()
    }

    fun setData(list: List<CollectionItem>) {
        listCollection.submitList(list)
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemCollectionBinding>,
        position: Int
    ) {
        val collectionItem = listCollection.currentList[position]
        holder.binding.tvTitle.text = collectionItem.title
        var background = 0
        var marginsHorizontal = 0

        when (collectionItem.type) {
            Constant.RECENT_DAY -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                marginsHorizontal = 0
                background = 0

                holder.binding.rvAlbum.adapter = recentDaysAdapter
                recentDaysAdapter.setData(collectionItem.listItem as List<AlbumRecent>)
            }

            Constant.MEMORIES -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                marginsHorizontal = 0
                background = 0

                holder.binding.rvAlbum.adapter = memoriesAdapter
                val snapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(holder.binding.rvAlbum)
                memoriesAdapter.setData(collectionItem.listItem as List<AlbumMemories>)
            }

            Constant.MEDIA_TYPES -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.VERTICAL,
                    false
                )
                marginsHorizontal = Utils.dpToPx(20f)
                background = R.drawable.bg_rv_media_types

                holder.binding.rvAlbum.adapter = mediaTypeUtilitiesAdapter
                mediaTypeUtilitiesAdapter.setData(collectionItem.listItem as List<ItemMediaTypeUtilities>)
            }

            Constant.UTILITIES -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.VERTICAL,
                    false
                )
                marginsHorizontal = Utils.dpToPx(20f)
                background = R.drawable.bg_rv_media_types
            }

            else -> {
                holder.binding.rvAlbum.layoutManager = GridLayoutManager(
                    holder.binding.rvAlbum.context,
                    2,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                marginsHorizontal = 0
                background = 0

            }
        }

        val layoutParams =
            holder.binding.rvAlbum.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(
            marginsHorizontal,
            holder.binding.rvAlbum.marginTop,
            marginsHorizontal,
            holder.binding.rvAlbum.marginBottom
        )

        if (background != 0) {
            holder.binding.rvAlbum.setBackgroundResource(background)
        } else {
            holder.binding.rvAlbum.background = null
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_collection
    override val sizeItem: Int
        get() = listCollection.currentList.size
}