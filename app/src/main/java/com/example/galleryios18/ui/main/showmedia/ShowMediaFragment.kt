package com.example.galleryios18.ui.main.showmedia

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.galleryios18.App
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentShowMediaBinding
import com.example.galleryios18.ui.adapter.MediaShowAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.custom.FastPagerSnapHelper


class ShowMediaFragment : BaseBindingFragment<FragmentShowMediaBinding, ShowMediaViewModel>() {
    private lateinit var mediaShowAdapter: MediaShowAdapter

    override fun getViewModel(): Class<ShowMediaViewModel> {
        return ShowMediaViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_show_media

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initData()
        initView()
        listener()
    }

    private fun initData() {
        mediaShowAdapter = MediaShowAdapter()
        mainViewModel.allMediaLiveData.value?.let {
            mediaShowAdapter.setData(it)
        }
    }

    private fun initView() {
        binding.rvMediaShow.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper: SnapHelper = FastPagerSnapHelper(requireContext())
        snapHelper.attachToRecyclerView(binding.rvMediaShow)
        binding.rvMediaShow.adapter = mediaShowAdapter
        mainViewModel.allMediaLiveData.value?.let {
            if (App.instance.currentMediaShow != null && App.instance.currentPositionShow != -1) {
                if (it[App.instance.currentPositionShow].id == App.instance.currentMediaShow?.id) {
                    binding.rvMediaShow.scrollToPosition(App.instance.currentPositionShow)
                } else {
                    for (i in it.indices) {
                        if (it[i].id == App.instance.currentMediaShow!!.id) {
                            binding.rvMediaShow.scrollToPosition(i)
                        }
                    }
                }
            } else {
                binding.rvMediaShow.scrollToPosition(it.size - 1)
            }
        }

    }

    private fun listener() {

    }

    override fun observerData() {
        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {

        }
    }
}