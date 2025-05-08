package com.example.galleryios18.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseBindingAdapter<B : ViewDataBinding> :
    RecyclerView.Adapter<BaseBindingAdapter.BaseHolder<B>>() {

    protected abstract fun onBindViewHolderBase(holder: BaseHolder<B>, position: Int)
    protected open fun onCreateViewHolder(binding: ViewDataBinding) {}
    protected abstract val layoutIdItem: Int
    protected abstract val sizeItem: Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<B> {
        val binding = DataBindingUtil.inflate<B>(
            LayoutInflater.from(parent.context), layoutIdItem, parent, false
        )
        onCreateViewHolder(binding)
        return BaseHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseHolder<B>, position: Int) {
        onBindViewHolderBase(holder, holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return sizeItem
    }

    class BaseHolder<B : ViewDataBinding>(var binding: B) : RecyclerView.ViewHolder(
        binding.root
    )
}