package com.example.galleryios18.ui.main.drawtoimage

import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentDrawToImageBinding
import com.example.galleryios18.ui.base.BaseBindingFragment

class DrawToImageFragment : BaseBindingFragment<FragmentDrawToImageBinding, DrawToImageViewModel>(){
    override fun getViewModel(): Class<DrawToImageViewModel> {
        return DrawToImageViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_draw_to_image

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun observerData() {

    }
}