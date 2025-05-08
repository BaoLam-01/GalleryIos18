package com.example.galleryios18.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.RelativeLayout
import com.example.galleryios18.databinding.DialogPermissionMediaBinding

class PermissionSelectPhotoDialog(
    context: Context,
    private val permissionSelectPhotoListener: PermissionSelectPhotoListener
) : Dialog(context) {

    private var binding: DialogPermissionMediaBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogPermissionMediaBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initView() {
        binding.layoutParent.setOnClickListener { dismiss() }
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvSelectMorePhotos.setOnClickListener {
            permissionSelectPhotoListener.onSelectMorePhotos()
            dismiss()
        }
        binding.tvChangeSettings.setOnClickListener {
            permissionSelectPhotoListener.onChangeSettings()
            dismiss()
        }
    }

    interface PermissionSelectPhotoListener {
        fun onSelectMorePhotos()
        fun onChangeSettings()
    }
}