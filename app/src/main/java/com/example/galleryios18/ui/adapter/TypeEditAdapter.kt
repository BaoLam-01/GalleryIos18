package com.example.galleryios18.ui.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.example.galleryios18.R
import com.example.galleryios18.data.models.TypeEdit
import com.example.galleryios18.databinding.ItemTypeEditBinding
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.utils.Utils
import com.example.galleryios18.utils.rcvhelper.CenterRcv

class TypeEditAdapter : BaseBindingAdapter<ItemTypeEditBinding>() {
    private var selectedPosition = 0
    private var listener: ((Int, TypeEdit) -> Unit)? = null
    fun setListener(typeEditClick: ((Int, TypeEdit) -> Unit)) {
        listener = typeEditClick
    }

    private var listTypeEdit: AsyncListDiffer<TypeEdit>
    private val mDiffCallback = object : DiffUtil.ItemCallback<TypeEdit>() {
        override fun areItemsTheSame(
            oldItem: TypeEdit,
            newItem: TypeEdit,
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: TypeEdit,
            newItem: TypeEdit,
        ): Boolean {
            return oldItem.name == newItem.name
        }

    }

    init {
        listTypeEdit = AsyncListDiffer(this, mDiffCallback)
    }

    fun setData(list: List<TypeEdit>) {
        listTypeEdit.submitList(list)
    }

    fun notifyItemChanged(item: TypeEdit) {
        for (i in listTypeEdit.currentList.indices) {
            if (listTypeEdit.currentList[i].name.equals(item.name, true)) {
                notifyItemChanged(i)
                break
            }
        }
    }


    override fun onBindViewHolderBase(
        holder: BaseHolder<ItemTypeEditBinding>,
        position: Int,
    ) {
        val typeEdit = listTypeEdit.currentList[position]
        var numberShow = 0.0
        var progressShow = 0
        if (typeEdit.isCrop) {
            numberShow = typeEdit.number
            progressShow = numberShow.toInt()
        } else {
            numberShow = if (listTypeEdit.currentList[0].isShow)
                typeEdit.numberRandomAuto else typeEdit.number
            progressShow = (numberShow * 3.6).toInt()
            if (position == 0) {
                showIcon(holder, typeEdit)
            } else {
                if (typeEdit.isShow && position == selectedPosition) {
                    showNumber(holder, typeEdit)
                } else {
                    showIcon(holder, typeEdit)
                }
            }
            holder.binding.vBlockTypeEdit.visibility =
                if (typeEdit.isShow) View.GONE else View.VISIBLE
        }

        holder.binding.circularProgressBar.progress = progressShow.toFloat()
        holder.binding.tvNumber.text = numberShow.toInt().toString()

        var progressBarColor: Int = 0
        var backgroundProgressBarColor: Int = 0
        if (progressShow <= 0) {
            progressBarColor =
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            backgroundProgressBarColor =
                ContextCompat.getColor(holder.itemView.context, R.color.color_8a8a8a)
        } else {
            progressBarColor =
                ContextCompat.getColor(holder.itemView.context, R.color.color_FFD60A)
            backgroundProgressBarColor =
                ContextCompat.getColor(holder.itemView.context, R.color.color_7a786a)
        }
        holder.binding.circularProgressBar.progressBarColor = progressBarColor
        holder.binding.circularProgressBar.backgroundProgressBarColor = backgroundProgressBarColor
        holder.binding.tvNumber.setTextColor(progressBarColor)

        holder.binding.root.setOnClickListener {
            Utils.checkClick(it, 500)
            listener?.invoke(position, typeEdit)
        }
        CenterRcv.setMarginItem(listTypeEdit.currentList.size, position, holder.itemView)

    }

    private fun showIcon(
        holder: BaseHolder<ItemTypeEditBinding>,
        typeEdit: TypeEdit,
    ) {
        holder.binding.imgIcon.visibility = View.VISIBLE
        holder.binding.tvNumber.visibility = View.GONE

        holder.binding.imgIcon.setImageResource(typeEdit.idIcon)
    }

    private fun showNumber(
        holder: BaseHolder<ItemTypeEditBinding>,
        typeEdit: TypeEdit,
    ) {
        holder.binding.imgIcon.visibility = View.GONE
        holder.binding.tvNumber.visibility = View.VISIBLE
        holder.binding.tvNumber.text = typeEdit.number.toString()
    }


    override val layoutIdItem: Int
        get() = R.layout.item_type_edit

    override val sizeItem: Int
        get() = listTypeEdit.currentList.size
}