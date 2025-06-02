package com.example.galleryios18.ui.main.custommize

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.galleryios18.R
import com.example.galleryios18.databinding.BottomSheetCustomizeBinding
import com.example.galleryios18.feature.SimpleItemTouchHelperCallback
import com.example.galleryios18.interfaces.ItemTouchListener
import com.example.galleryios18.ui.adapter.CustomizeCollectionAdapter
import com.example.galleryios18.ui.base.BaseBottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class CustomizeBottomSheet : BaseBottomSheetDialog<BottomSheetCustomizeBinding, Any>() {
    private lateinit var collectionAdapter: CustomizeCollectionAdapter

    override val layoutId: Int
        get() = R.layout.bottom_sheet_customize

    override fun observer() {
        mainViewModel.allCollectionLiveData.observe(viewLifecycleOwner) {
            collectionAdapter.setData(it)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0f)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            bottomSheet?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams

                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        binding.btnClose.setOnClickListener(this)
    }

    private fun initView() {
        collectionAdapter = CustomizeCollectionAdapter()
        binding.rvCollection.adapter = collectionAdapter

        val callback = SimpleItemTouchHelperCallback()
        callback.setListener(object : ItemTouchListener {
            override fun onMove(oldPosition: Int, newPosition: Int) {
                collectionAdapter.onMove(oldPosition, newPosition);
            }

        })
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvCollection)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnClose.id -> {
                this.dismissAllowingStateLoss()
            }
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}