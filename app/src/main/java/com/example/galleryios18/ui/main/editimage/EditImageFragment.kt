package com.example.galleryios18.ui.main.editimage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.R
import com.example.galleryios18.data.models.TypeEdit
import com.example.galleryios18.databinding.FragmentEditImageBinding
import com.example.galleryios18.ui.adapter.TypeEditAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.main.MainActivity
import com.example.galleryios18.utils.Utils
import com.example.galleryios18.utils.ViewUtils
import com.example.galleryios18.utils.enable
import com.example.galleryios18.utils.rcvhelper.CenterRcv
import com.github.shchurov.horizontalwheelview.HorizontalWheelView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import timber.log.Timber
import kotlin.math.round

class EditImageFragment : BaseBindingFragment<FragmentEditImageBinding, EditImageViewModel>() {
    private lateinit var typeEditAdapter: TypeEditAdapter
    private var currentTypeEdit: TypeEdit? = null
    private lateinit var listTypeEditAdjust: List<TypeEdit>
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
        listTypeEditAdjust = viewModel.getListItemAdjust()
        typeEditAdapter.setData(listTypeEditAdjust)
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
                    if (listTypeEditAdjust.isNotEmpty()) {
                        position = listTypeEditAdjust.size - 1
                    } else return
                }
                Timber.e("LamPro | onScrolled - position: $position")
                currentTypeEdit = listTypeEditAdjust[position]

                binding.vBlockWheelView.visibility =
                    if (currentTypeEdit!!.isShow) View.GONE else View.VISIBLE
            }

        })

        binding.wheelView.setListener(object : HorizontalWheelView.Listener() {
            override fun onRotationChanged(radians: Double) {
                super.onRotationChanged(radians)
                val degree = binding.wheelView.degreesAngle
                Timber.e("LamPro | onRotationChanged - degree: $degree")
                val percent = round(Utils.changeDegreesToPercent(degree))

                if (listTypeEditAdjust.isNotEmpty()) {
                    if (listTypeEditAdjust[0].isShow) {
                        currentTypeEdit?.numberRandomAuto = percent
                    } else {
                        currentTypeEdit?.number = percent
                    }
                }
                currentTypeEdit?.let {
                    typeEditAdapter.notifyItemChanged(it)
                }

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
    }
}