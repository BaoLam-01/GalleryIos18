package com.example.galleryios18.ui.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.example.galleryios18.R
import com.example.galleryios18.ui.base.BaseBindingAdapter
import com.example.galleryios18.common.models.Language
import com.example.galleryios18.common.models.ListLanguage
import com.example.galleryios18.databinding.ItemRcvLanguageBinding

class LanguageAdapter(stringList: ListLanguage, listener: LanguageListener) :
    BaseBindingAdapter<ItemRcvLanguageBinding>() {

    private var listLanguage = mutableListOf<Language>()
    private var mListener: LanguageListener? = null
    private var posSelected = 0
    private var lanDefault: Language? = null

    init {
        listLanguage.clear()
        listLanguage.addAll(stringList.list)
        posSelected = stringList.posLanguageSelected
        mListener = listener
        lanDefault = (listLanguage as MutableList<Language>?)!![posSelected]
    }

    fun getSelectedLanguage(): Language {
        return listLanguage[posSelected]
    }

    fun setPosSelect(position: Int) {
        notifyItemChanged(posSelected)
        this.posSelected = position
        notifyItemChanged(position)
    }

    override fun onBindViewHolderBase(holder: BaseHolder<ItemRcvLanguageBinding>, position: Int) {
        val language = listLanguage[position]
        val context = holder.binding.root.context
        if (position == posSelected) {
            holder.binding.radioChoose.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.blue)
            holder.binding.radioChoose.isChecked = true
        } else {
            holder.binding.radioChoose.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.black)
            holder.binding.radioChoose.isChecked = false
        }
        when (position) {
            0 -> {
//                holder.binding.layoutParent.setBackgroundResource(bg_ripple_ffffff_top_10)
            }
            listLanguage.size - 1 -> {
//                holder.binding.layoutParent.setBackgroundResource(R.drawable.bg_ripple_ffffff_bottom_10)
            }
            else -> {
//                holder.binding.layoutParent.setBackgroundResource(R.drawable.bg_ripple_ffffff)
            }
        }
        if (position == listLanguage.size - 1) holder.binding.viewLine.visibility = View.GONE
        else holder.binding.viewLine.visibility = View.VISIBLE
        holder.binding.tvLanguage.text = language.nameLanguage
        holder.binding.root.setOnClickListener {
            if (position != posSelected) {
                mListener?.onClick(position, language)
            }
        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_rcv_language

    override val sizeItem: Int
        get() = listLanguage.size

    interface LanguageListener {
        fun onClick(position: Int, language: Language)
    }
}