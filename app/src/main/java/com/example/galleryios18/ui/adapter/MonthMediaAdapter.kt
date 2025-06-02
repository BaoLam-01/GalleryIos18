package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.ItemMonthType1Binding

class MonthMediaAdapter : RecyclerView.Adapter<MonthMediaAdapter.MonthMediaViewHolder>() {
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
        this.mDiffer.submitList(listMedia)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MonthMediaViewHolder {
        val binding =
            ItemMonthType1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonthMediaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MonthMediaViewHolder, position: Int
    ) {
        holder.bindData(mDiffer.currentList[position], position)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    inner class MonthMediaViewHolder(private val binding: ItemMonthType1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(media: Media, position: Int) {

        }
    }
}