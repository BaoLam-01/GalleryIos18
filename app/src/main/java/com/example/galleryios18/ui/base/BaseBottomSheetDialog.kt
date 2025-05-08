package com.example.galleryios18.ui.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.galleryios18.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.galleryios18.ui.main.MainViewModel
import com.example.galleryios18.utils.ViewUtils

abstract class BaseBottomSheetDialog<B : ViewDataBinding, T> : BottomSheetDialogFragment(),
    View.OnClickListener {
    lateinit var mainViewModel: MainViewModel
    lateinit var binding: B
    abstract val layoutId: Int
    var behavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialog1: DialogInterface ->
            val bottomDialog =
                dialog1 as BottomSheetDialog
            val bottomSheet =
                bottomDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)!!
            behavior =
                BottomSheetBehavior.from(
                    bottomSheet
                )
            behavior?.isHideable = true
            behavior?.maxHeight = (ViewUtils.getScreenHeight(requireContext()) * 1.5f / 2).toInt()
            behavior!!.peekHeight = ViewUtils.getScreenHeight(requireContext()) / 2
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreatedView(view, savedInstanceState)
        observer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            layoutId,
            container,
            false
        )
        return binding.root
    }

    abstract fun observer()
    abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
}