package com.example.galleryios18.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import com.example.galleryios18.R
import com.example.galleryios18.databinding.DialogConfirmBinding

class CancelDialog(private val context: Activity, private val confirmListener: ConfirmListener) :
    Dialog(context) {
    var isCancel = false
    private var binding: DialogConfirmBinding =
        DialogConfirmBinding.inflate(LayoutInflater.from(context))

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        onClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    private fun onClick() {
        binding.layoutParent.setOnClickListener {
            dismiss()
        }
        binding.tvSave.setOnClickListener {
            confirmListener.saveListener()
            dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            dismiss()
        }
    }

    fun setDialogIsCancel() {
        isCancel = true
        binding.tvTitle.text = context.getString(R.string.exit_editing)
        binding.tvSave.text = context.getString(R.string.ok)
    }

    interface ConfirmListener {
        fun saveListener()
    }
}