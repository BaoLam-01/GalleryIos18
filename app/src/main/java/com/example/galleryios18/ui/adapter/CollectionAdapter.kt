package com.example.galleryios18.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.models.Album
import com.example.galleryios18.data.models.AlbumRecent
import com.example.galleryios18.data.models.CollectionItem
import com.example.galleryios18.databinding.ItemCollectionBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter

class CollectionAdapter : BaseBindingAdapter<ItemCollectionBinding>() {
    private var listCollection: AsyncListDiffer<CollectionItem>

    private var recentDaysAdapter: RecentDaysAdapter
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

        when (collectionItem.type) {
            Constant.RECENT_DAY -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                holder.binding.rvAlbum.background = null
                holder.binding.rvAlbum.adapter = recentDaysAdapter
                recentDaysAdapter.setData(collectionItem.listItem as List<AlbumRecent>)
            }

            Constant.MEMORIES -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                holder.binding.rvAlbum.background = null
            }

            Constant.MEDIA_TYPES -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.VERTICAL,
                    false
                )
                holder.binding.rvAlbum.setBackgroundResource(R.drawable.bg_rv_media_types)
            }

            Constant.UTILITIES -> {
                holder.binding.rvAlbum.layoutManager = LinearLayoutManager(
                    holder.binding.rvAlbum.context,
                    GridLayoutManager.VERTICAL,
                    false
                )
                holder.binding.rvAlbum.setBackgroundResource(R.drawable.bg_rv_media_types)
            }

            else -> {
                holder.binding.rvAlbum.layoutManager = GridLayoutManager(
                    holder.binding.rvAlbum.context,
                    2,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                holder.binding.rvAlbum.background = null

            }

        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_collection
    override val sizeItem: Int
        get() = listCollection.currentList.size
}