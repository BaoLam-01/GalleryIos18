package com.example.galleryios18.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.graphics.LinearGradient
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.App
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.FragmentHomeBinding
import com.example.galleryios18.ui.adapter.CollectionAdapter
import com.example.galleryios18.ui.adapter.MediaAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.main.MainActivity
import com.example.galleryios18.utils.Utils
import com.example.galleryios18.utils.ViewUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tapbi.spark.launcherios18.utils.PermissionHelper
import timber.log.Timber

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {
    private var requestPermission = true
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var collectionAdapter: CollectionAdapter
    private var isRcvMediaOverScroll = false
    private var isRcvMediaTop = true
    private var isRcvCollectionTop = true

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
        listener()
    }


    private fun onBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
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
            binding.rcvMedia.scrollToPosition(it.size - 1)
        }

        mainViewModel.allCollectionLiveData.observe(viewLifecycleOwner) {
            Timber.e("LamPro | observerData - setdata: ${it.size}")
            collectionAdapter.setData(it)
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

        binding.rcvMedia.layoutParams.let {
            val layoutParams = it as ConstraintLayout.LayoutParams
            layoutParams.height =
                Utils.getScreenHeight(requireContext()) + (requireActivity() as MainActivity).navigationBarHeight + (requireActivity() as MainActivity).statusBarHeight
            binding.rcvMedia.layoutParams = layoutParams
        }
        binding.rcvCollection.layoutParams.let {
            val layoutParams = it as ConstraintLayout.LayoutParams
            layoutParams.height =
                Utils.getScreenHeight(requireContext()) + (requireActivity() as MainActivity).navigationBarHeight + (requireActivity() as MainActivity).statusBarHeight
            binding.rcvCollection.layoutParams = layoutParams
        }

        mediaAdapter = MediaAdapter()
        binding.rcvMedia.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = mediaAdapter
        }

        collectionAdapter = CollectionAdapter()
        binding.rcvCollection.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = collectionAdapter
        }
        initTabLayout()

        ViewUtils.adjustViewWithSystemBar(
            binding.tvTitle, binding.imgSort, requireActivity() as MainActivity
        )


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
                val tabCurrent = tab.position
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
                binding.rcvMedia.layoutManager =
                    GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
                mediaAdapter.setStyle(MediaAdapter.StyleRecycler.MONTH)
                binding.rcvMedia.scheduleLayoutAnimation()

//                    binding.rvPhotos.scrollToPosition(it.size - 1)
            }

            TabImage.TAB_ALL_PHOTO -> {
                binding.rcvMedia.layoutManager =
                    GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

                mediaAdapter.setStyle(MediaAdapter.StyleRecycler.ALL)

//                    binding.rvPhotos.scrollToPosition(it.size - 1)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listener() {
        mediaAdapter.setListener(object : MediaAdapter.IMediaClick {
            override fun onMediaClick(media: Media, position: Int) {
                if (!checkClick()) return
                App.instance.currentMediaShow = media
                App.instance.currentPositionShow = position
                navigateScreen(null, R.id.showMediaFragment)
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                val viewTop = binding.rcvMedia.top
                if (viewTop - scrollY == 0) {
                    isRcvMediaTop = true
                } else {
                    isRcvMediaTop = false
                }
                // Nếu nestedScrollView cuộn đến cuối, cho phép rcvMedia cuộn
                binding.rcvMedia.isScrollEnabled = isRcvMediaTop == true

//                val rvCollectionTop = binding.rcvCollection.top
//                if (rvCollectionTop - scrollY <= 0) {
//                    isRcvCollectionTop = true
//                } else {
//                    isRcvCollectionTop = false
//                }
//                Timber.e("LamPro - isRcvCollectionTop: $isRcvCollectionTop")
//                binding.rcvCollection.isScrollEnabled = isRcvCollectionTop == true


                val rcvCollectionTop = binding.rcvCollection.top

                // Nếu rcvCollection đã chạm top màn hình
                val isRcvCollectionVisibleAtTop = rcvCollectionTop - scrollY <= 0
                if (isRcvCollectionVisibleAtTop) {
                    // Bật cuộn cho rcvCollection
                    binding.rcvCollection.isScrollEnabled = true

                    // Dừng fling của NestedScrollView nếu đang cuộn
                    stopNestedScrollImmediately(binding.nestedScrollView)
                } else {
                    // Ngược lại: cho phép nestedScrollView cuộn
                    binding.rcvCollection.isScrollEnabled = false
                }
                // Optional: tắt NestedScrollView khi rcvCollection ở top
                binding.nestedScrollView.setOnTouchListener { _, _ ->
                    // Khi rcvCollection đang cuộn, ta "ngăn" NestedScrollView cuộn bằng cách consume sự kiện
                    isRcvCollectionVisibleAtTop
                }



                Timber.d("rcvCollectionTop: $rcvCollectionTop, scrollY: $scrollY -> ${isRcvCollectionVisibleAtTop}")

            }
        }
    }

    fun stopNestedScrollImmediately(scrollView: NestedScrollView) {
        scrollView.fling(0) // dừng vận tốc
        scrollView.stopNestedScroll() // dừng nested scroll
    }


    private fun getAllMedia() {
        Timber.e("LamPro | getAllMedia - ")
        mainViewModel.getAllMedia()
        mainViewModel.getAllCollection()
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