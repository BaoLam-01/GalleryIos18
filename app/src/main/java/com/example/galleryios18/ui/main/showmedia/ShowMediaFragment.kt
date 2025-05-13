package com.example.galleryios18.ui.main.showmedia

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.galleryios18.App
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentShowMediaBinding
import com.example.galleryios18.ui.adapter.MediaShowAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.custom.FastPagerSnapHelper
import com.example.galleryios18.ui.custom.HorizontalSpaceItemDecoration
import com.example.galleryios18.ui.main.MainActivity
import com.example.galleryios18.utils.ViewUtils
import timber.log.Timber
import kotlin.math.abs


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
        initMediaShow()

        ViewUtils.adjustViewWithSystemBar(
            binding.tvDateMedia,
            binding.btnShare,
            requireActivity() as MainActivity
        )

    }

    private fun initMediaShow() {
        binding.rvMediaShow.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val snapHelper: SnapHelper = FastPagerSnapHelper(requireContext())
        snapHelper.attachToRecyclerView(binding.rvMediaShow)

        binding.rvMediaShow.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val center = recyclerView.width / 2f
                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    val childCenter = (child.left + child.right) / 2f
                    val d = abs(center - childCenter)
                    val scale = 1f - d / recyclerView.width * 0.1f
                    child.scaleY = scale
                    child.scaleX = scale
                }
            }
        })

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
        mediaShowAdapter.setListener {

            Timber.e("LamPro | listener - rv onclick")
            if (binding.gr.isVisible) {
                binding.gr.visibility = View.GONE
                binding.root.setBackgroundColor(Color.BLACK)
            } else {
                binding.gr.visibility = View.VISIBLE
                binding.root.setBackgroundColor(Color.WHITE)
            }
        }

        binding.imgBack.setOnClickListener {
            popBackStack()
        }

        binding.btnAdjust.setOnClickListener {
            navigateScreen(null, R.id.EditImageFragment)
        }
    }

    override fun observerData() {
        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {

        }
    }
}