package com.example.galleryios18.ui.main.cordinatorlayout

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.R
import com.example.galleryios18.databinding.CordilatorLayoutBinding
import com.example.galleryios18.ui.adapter.MediaAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.main.MainActivity
import com.example.galleryios18.utils.Utils

class CordiLayout : BaseBindingFragment<CordilatorLayoutBinding, CordiViewModel>() {
    private lateinit var mediaAdapter: MediaAdapter

    override fun getViewModel(): Class<CordiViewModel> {
        return CordiViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.cordilator_layout

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

        binding.recyclerView.layoutParams.let {
            val layoutParams = it as ConstraintLayout.LayoutParams
            layoutParams.height =
                Utils.getScreenHeight(requireContext()) + (requireActivity() as MainActivity).navigationBarHeight + (requireActivity() as MainActivity).statusBarHeight + 10
            binding.recyclerView.layoutParams = layoutParams
        }

        mediaAdapter = MediaAdapter()
        binding.recyclerView.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = mediaAdapter
        }
        mainViewModel.getAllMedia()


        val motionLayout = binding.motionLayout
        val recyclerView = binding.recyclerView

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isAtBottom = false

            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as LinearLayoutManager
                val lastVisible = lm.findLastCompletelyVisibleItemPosition()
                val total = lm.itemCount

                isAtBottom = lastVisible == total - 1
            }

            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rv, newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && isAtBottom) {
                    // Kéo tiếp khi cuộn hết danh sách
                    motionLayout.transitionToEnd()
                }

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && !isAtBottom) {
                    // Nếu cuộn lại lên, quay về trạng thái ban đầu
                    motionLayout.transitionToStart()
                }
            }
        })
    }

    override fun observerData() {
        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {
            mediaAdapter.setData(it)
            binding.recyclerView.scrollToPosition(it.size - 1)
        }
    }
}