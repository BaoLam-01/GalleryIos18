package com.example.galleryios18.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.FragmentHomeBinding
import com.example.galleryios18.ui.adapter.MediaAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.main.MainActivity
import com.example.galleryios18.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tapbi.spark.launcherios18.utils.PermissionHelper
import timber.log.Timber

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {
    private var requestPermission = true
    private lateinit var mediaAdapter: MediaAdapter

    private val multiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (permissions[Manifest.permission.READ_MEDIA_IMAGES] == true || permissions[Manifest.permission.READ_MEDIA_VIDEO] == true) {
                getAllMedia()
            } else {
                if (permissions[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true) {
                    getAllMedia()
                } else {
                    // no media
                }
            }


        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (permissions[Manifest.permission.READ_MEDIA_IMAGES] == true || permissions[Manifest.permission.READ_MEDIA_VIDEO] == true) {
                getAllMedia()
            } else {
                // no media
            }
        } else {
            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                getAllMedia()
            } else {
                // no media
            }
        }

        if (permissions[Manifest.permission.POST_NOTIFICATIONS] == true) {
            Utils.startService(requireActivity(), true)
        } else {
            Utils.startService(requireActivity(), true)
        }
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPress()
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        Timber.e("LamPro | onCreatedView - ")
        initView()
        onClick()
    }


    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    override fun needToKeepView(): Boolean {
        return true
    }

    override fun observerData() {
        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {
            mediaAdapter.setData(it)
            binding.rvPhotos.scrollToPosition(it.size - 1)
        }
    }

    @SuppressLint("InlinedApi")
    override fun onStart() {
        super.onStart()
        if (requestPermission) {
            requestPermission = false
            Timber.e("LamPro | onStart - requestPermission : $requestPermission")

            if (PermissionHelper.checkPermissionMedia(requireActivity())) {
                val permissions =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.POST_NOTIFICATIONS

                        )
                    } else {
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }

                multiplePermissionLauncher.launch(permissions)
            } else {
                getAllMedia()
            }
        }
    }

    private fun initView() {

        mediaAdapter = MediaAdapter()
        binding.rvPhotos.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = mediaAdapter
        }
        initTabLayout()

        binding.tvTitle.apply {
            val layoutParams = this.layoutParams
            if (layoutParams is ConstraintLayout.LayoutParams) {
                layoutParams.topMargin = (requireActivity() as MainActivity).statusBarHeight
                this.layoutParams = layoutParams
                requestLayout()
            }
        }
    }

    private fun initTabLayout() {
        binding.tlBottom.addTab(
            binding.tlBottom.newTab().setText(getText(R.string.month))
        )
        binding.tlBottom.addTab(
            binding.tlBottom.newTab().setText(getText(R.string.all_photos))
        )
        binding.tlBottom.selectTab(binding.tlBottom.getTabAt(TabImage.TAB_ALL_PHOTO))
        changeTabLayout(TabImage.TAB_ALL_PHOTO)
        binding.tlBottom.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCurrent = tab.getPosition()
                changeTabLayout(tabCurrent)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        for (i in 0 until binding.tlBottom.getTabCount()) {
            val tabView = (binding.tlBottom.getChildAt(0) as ViewGroup).getChildAt(i)
            tabView.setOnLongClickListener(OnLongClickListener { v: View? -> true })
        }
    }

    private fun changeTabLayout(position: Int) {
        when (position) {
            TabImage.TAB_MONTH -> {
                binding.rvPhotos.layoutManager =
                    GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
                mediaAdapter.setStyle(MediaAdapter.StyleRecycler.MONTH)
                binding.rvPhotos.scheduleLayoutAnimation()

//                    binding.rvPhotos.scrollToPosition(it.size - 1)
            }

            TabImage.TAB_ALL_PHOTO -> {
                binding.rvPhotos.layoutManager =
                    GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

                mediaAdapter.setStyle(MediaAdapter.StyleRecycler.ALL)

//                    binding.rvPhotos.scrollToPosition(it.size - 1)
            }
        }
    }


    private fun onClick() {
        mediaAdapter.setListener(object : MediaAdapter.IMediaClick {
            override fun onMediaClick(media: Media) {

            }
        })
    }

    private fun getAllMedia() {
        Timber.e("LamPro | getAllMedia - ")
        mainViewModel.getAllMedia()
    }

    override fun finishRate() {
        super.finishRate()
        if (isAdded) {
        }
    }

    object TabImage {
        const val TAB_MONTH = 0
        const val TAB_ALL_PHOTO = 1
    }

}