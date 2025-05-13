package com.example.galleryios18.ui.main.editimage

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentEditImageBinding
import com.example.galleryios18.ui.adapter.TypeEditAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class EditImageFragment : BaseBindingFragment<FragmentEditImageBinding, EditImageViewModel>() {
    private lateinit var typeEditAdapter: TypeEditAdapter
    override fun getViewModel(): Class<EditImageViewModel> {
        return EditImageViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_edit_image

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initData()
        initView()
        listener()
    }

    private fun initData() {
        typeEditAdapter = TypeEditAdapter()
        viewModel.getAllItemAdjust()
    }

    private fun initView() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.layoutAdjust.rvTypeAdjust)
        binding.layoutAdjust.rvTypeAdjust.adapter = typeEditAdapter

        OverScrollDecoratorHelper.setUpOverScroll(
            binding.layoutAdjust.rvTypeAdjust,
            OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
        )
    }

    private fun listener() {
    }

    override fun observerData() {
        viewModel.listItemAdjust.observe(viewLifecycleOwner) {
            typeEditAdapter.setData(it)
        }
    }
}