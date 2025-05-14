package com.example.galleryios18.ui.main.editimage

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.R
import com.example.galleryios18.data.models.TypeEdit
import com.example.galleryios18.databinding.FragmentEditImageBinding
import com.example.galleryios18.ui.adapter.TypeEditAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.main.MainActivity
import com.example.galleryios18.utils.ViewUtils
import com.example.galleryios18.utils.rcvhelper.CenterRcv
import com.github.shchurov.horizontalwheelview.HorizontalWheelView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import timber.log.Timber

class EditImageFragment : BaseBindingFragment<FragmentEditImageBinding, EditImageViewModel>() {
    private lateinit var typeEditAdapter: TypeEditAdapter
    private var currentTypeEdit: TypeEdit? = null
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
        ViewUtils.adjustViewWithSystemBar(
            binding.tvCancel, binding.btnFilter, requireActivity() as MainActivity
        )
        OverScrollDecoratorHelper.setUpOverScroll(
            binding.rvTypeAdjust, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
        )
        binding.rvTypeAdjust.adapter = typeEditAdapter
    }

    private fun listener() {

        binding.rvTypeAdjust.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var position = binding.rvTypeAdjust.getPos()
                Timber.e("LamPro | onScrolled - pre position : $position")
                if (position == -1) {
                    position = viewModel.listItemAdjust.value!!.size - 1
                }
                Timber.e("LamPro | onScrolled - position: $position")
                currentTypeEdit = viewModel.listItemAdjust.value?.get(position)
            }
        })

        binding.wheelView.setListener(object : HorizontalWheelView.Listener() {
            override fun onRotationChanged(radians: Double) {
                super.onRotationChanged(radians)
//                val degree = binding.wheelView.degreesAngle
//                Timber.e("LamPro | onRotationChanged - degree: $degree")
            }
        })
        typeEditAdapter.setListener { position, typeEdit ->
            if (typeEdit.isCrop) {
//                CenterRcv.scrollToCenter(
//                    binding.inControlsCrop.inScale.rcvTypeScale.getLayoutManager() as LinearLayoutManager?,
//                    binding.inControlsFilter.rcvFilter,
//                    pos
//                )
            } else {
                if (currentTypeEdit?.name.equals(typeEdit.name)) {
                    typeEdit.isShow = !typeEdit.isShow
                    binding.vBlockWheelView.visibility =
                        if (typeEdit.isShow) View.GONE else View.VISIBLE
                    typeEditAdapter.notifyItemChanged(position)
                }
                CenterRcv.scrollToCenter(
                    binding.rvTypeAdjust.layoutManager as LinearLayoutManager?,
                    binding.rvTypeAdjust,
                    position
                )
            }
        }

    }

    override fun observerData() {
        viewModel.listItemAdjust.observe(viewLifecycleOwner) {
            typeEditAdapter.setData(it)
        }
    }
}