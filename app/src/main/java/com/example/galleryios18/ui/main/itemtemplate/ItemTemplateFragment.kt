package com.example.galleryios18.ui.main.itemtemplate

import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.data.models.template_item.Item
import com.example.galleryios18.data.models.template_item.Template
import com.example.galleryios18.databinding.FragmentItemTemplateBinding
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.google.gson.Gson
import kotlin.compareTo
import kotlin.math.max
import kotlin.text.get

class ItemTemplateFragment :
    BaseBindingFragment<FragmentItemTemplateBinding, ItemTemplateViewModel>() {
    private var item: Item? = null
    private var itemString: String? = null

    companion object {
        fun newIns(
            pathImage: String,
            pos: Int,
        ): ItemTemplateFragment {
            val fragment = ItemTemplateFragment()
            val bundle = Bundle()
            bundle.putString(Constant.PATH_MEDIA_FROM_GALLERY, pathImage)
            bundle.putInt(Constant.CURRENT_POS_TEMPLATE, pos)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var isCreatedView = false
    private var pathImageBegin = ""


    override fun getViewModel(): Class<ItemTemplateViewModel> {
        return ItemTemplateViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_item_template

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        pathImageBegin = requireArguments().getString(Constant.PATH_MEDIA_FROM_GALLERY, "")
//        templateString = requireArguments().getString(Constant.TEMPLATE_STRING, "")
        mainViewModel.listItemJsonLiveData.observe(viewLifecycleOwner) {
            if (item == null) {
                val pos = requireArguments().getInt(Constant.CURRENT_POS_TEMPLATE, 0)
                if (pos >= 0 && it.isNotEmpty()) {
                    itemString = it[pos]
                }
                item = if (itemString?.isNotEmpty() == true) {
                    Gson().fromJson(itemString, Item::class.java)
                } else {
                    Item()
                }
            }

            binding.templateView.setupTemplate(item, false)
            isCreatedView = true
        }
    }


    override fun observerData() {
    }


    private var isReplaceTemplate = false

    fun getTemplateView(): View? {
        if (isCreatedView) {
            return binding.templateView
        }
        return null
    }

    fun getTemplate(): Template {
        return binding.templateView.templateSave
    }


    fun getDurationTemplate(): Int {
        if (isCreatedView) {
            val d = binding.templateView.setTimeDelay(0)
            return max(d, binding.templateView.duration/*Constant.DEFAULT_DURATION_PAGE*/)
        }
        return 0
    }


    fun setDuration(duration: Int) {
        binding.templateView.duration = duration
    }

    fun getDuration(): Int {
        return binding.templateView.duration
    }

}