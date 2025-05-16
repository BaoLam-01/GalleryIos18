package com.example.galleryios18.ui.main.drawtoimage

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentDrawToImageBinding
import com.example.galleryios18.ui.base.BaseBindingFragment

class DrawToImageFragment :
    BaseBindingFragment<FragmentDrawToImageBinding, DrawToImageViewModel>() {
    override fun getViewModel(): Class<DrawToImageViewModel> {
        return DrawToImageViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_draw_to_image

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
//        val drawingView = binding.drawingView
//
//// Thay đổi màu bút
//        drawingView.setBrushColor(Color.RED)
//
//// Thay đổi kích thước bút
//        drawingView.setBrushSize(20f)
    }

    override fun observerData() {

    }
}