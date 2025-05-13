package com.example.galleryios18.ui.adapter

import android.view.View
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.galleryios18.R
import com.example.galleryios18.data.models.ItemProgress
import com.example.galleryios18.databinding.ItemTypeEditBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter

class TypeEditAdapter : BaseBindingAdapter<ItemTypeEditBinding>() {
    private var selectedPosition = 0

    private var listTypeEdit: AsyncListDiffer<ItemProgress>
    private val mDiffCallback = object : DiffUtil.ItemCallback<ItemProgress>() {
        override fun areItemsTheSame(
            oldItem: ItemProgress,
            newItem: ItemProgress
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: ItemProgress,
            newItem: ItemProgress
        ): Boolean {
            return oldItem.name == newItem.name
        }

    }

    init {
        listTypeEdit = AsyncListDiffer(this, mDiffCallback)
    }

    fun setData(list: List<ItemProgress>) {
        listTypeEdit.submitList(list)
    }

    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemTypeEditBinding>,
        position: Int
    ) {
        val typeEdit = listTypeEdit.currentList[position]
        val numberShow =
            if (listTypeEdit.currentList[0].isShow)
                typeEdit.numberRandomAuto else typeEdit.number
        val progressShow = (numberShow * 3.6).toInt()
        if (position == 0)
            showNumber(holder, typeEdit)
        else
            showIcon(holder, typeEdit)
        holder.binding.circularProgressBar.progress = progressShow.toFloat()
        holder.binding.tvNumber.text = numberShow.toInt().toString()
    }

    private fun showIcon(
        holder: BaseHolder<ItemTypeEditBinding>,
        itemProgress: ItemProgress
    ) {
        holder.binding.imgIcon.visibility = View.VISIBLE
        holder.binding.tvNumber.visibility = View.GONE

        holder.binding.imgIcon.setImageResource(itemProgress.idIcon)
    }

    private fun showNumber(
        holder: BaseHolder<ItemTypeEditBinding>,
        itemProgress: ItemProgress
    ) {
        holder.binding.imgIcon.visibility = View.GONE
        holder.binding.tvNumber.visibility = View.VISIBLE
        holder.binding.tvNumber.text = itemProgress.number.toString()
    }


    override val layoutIdItem: Int
        get() = R.layout.item_type_edit

    override val sizeItem: Int
        get() = listTypeEdit.currentList.size
}