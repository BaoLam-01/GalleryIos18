package com.example.galleryios18.ui.main.editimage

import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentEditImageBinding
import com.example.galleryios18.ui.base.BaseBindingFragment

class EditImageFragment : BaseBindingFragment<FragmentEditImageBinding, EditImageViewModel>() {
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
    }

    private fun initView() {
    }

    private fun listener() {
    }

    override fun observerData() {
    }
}