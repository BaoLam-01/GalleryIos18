package com.example.galleryios18.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.galleryios18.App
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.FragmentHomeBinding
import com.example.galleryios18.ui.adapter.CollectionAdapter
import com.example.galleryios18.ui.adapter.MediaAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.custom.GroupHeaderDecoration
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
    private var isRcvMediaTop = true

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var currentSpanCount = 3 // Số cột hiện tại
    private var accumulatedScale = 1f

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
            Timber.e("LamPro | observerData - size media: ${it.size}")
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
        initRcvMedia()
        initRcvCollection()
        initTabLayout()

        ViewUtils.adjustViewWithSystemBar(
            binding.tvTitle, binding.imgSort, requireActivity() as MainActivity
        )

    }

    private fun initRcvMedia() {
        mediaAdapter = MediaAdapter()
        mediaAdapter.setSize(MediaAdapter.SizeAllMedia.MEDIUM)
        binding.viewBgGradient.post {
            binding.rcvMedia.setPadding(
                binding.rcvMedia.paddingLeft,
                binding.tvCountItem.bottom + 20,
                binding.rcvMedia.paddingRight,
                binding.rcvMedia.paddingBottom
            )
        }
        binding.rcvMedia.clipToPadding = false
        binding.rcvMedia.layoutParams.let {
            val layoutParams = it as ConstraintLayout.LayoutParams
            layoutParams.height =
                Utils.getScreenHeight(requireContext()) + (requireActivity() as MainActivity).navigationBarHeight + (requireActivity() as MainActivity).statusBarHeight
            binding.rcvMedia.layoutParams = layoutParams
        }
        val gridLayout = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        gridLayout.isItemPrefetchEnabled = true
        binding.rcvMedia.layoutManager = gridLayout

//        binding.rcvMedia.addItemDecoration(GroupHeaderDecoration { position ->
//            Timber.e("LamPro | initRcvMedia - position : $position")
//            if (currentSpanCount == 14) {
//                mediaAdapter.getItemTime(position)
//            } else {
//                ""
//            }
//        })
        binding.rcvMedia.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = mediaAdapter
        }
    }

    private fun initRcvCollection() {
        collectionAdapter = CollectionAdapter()

        //        binding.rcvCollection.layoutParams.let {
        //            val layoutParams = it as ConstraintLayout.LayoutParams
        //            layoutParams.height =
        //                Utils.getScreenHeight(requireContext()) + (requireActivity() as MainActivity).navigationBarHeight + (requireActivity() as MainActivity).statusBarHeight + 100
        //            binding.rcvCollection.layoutParams = layoutParams
        //        }

        binding.rcvCollection.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = collectionAdapter
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

        for (i in 0 until binding.tlBottom.getTabCount()) {
            val tabView = (binding.tlBottom.getChildAt(0) as ViewGroup).getChildAt(i)
            tabView.setOnLongClickListener(OnLongClickListener { v: View? -> true })
        }
    }

    private fun changeTabLayout(position: Int) {
        when (position) {
            TabImage.TAB_MONTH -> {
                binding.rcvMedia.visibility = View.GONE
//                binding.rcvMedia.layoutManager =
//                    GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
//                binding.rcvMedia.scheduleLayoutAnimation()
            }

            TabImage.TAB_ALL_PHOTO -> {
                binding.rcvMedia.visibility = View.VISIBLE
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                val viewTop = binding.rcvMedia.top
                if (viewTop - scrollY == 0) {
                    isRcvMediaTop = true
                } else {
                    isRcvMediaTop = false
                }
                binding.rcvMedia.isScrollEnabled = isRcvMediaTop == true
                Timber.e("LamPro | listener - iisRcvMediaTop : $isRcvMediaTop")

            }
        }

        scaleGestureDetector = ScaleGestureDetector(
            requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    accumulatedScale = 1f
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    accumulatedScale *= detector.scaleFactor
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) {
                    Timber.e("LamPro | onScaleEnd - accumulated scale: $accumulatedScale")
                    Timber.e("LamPro | onScaleEnd - size: ${mediaAdapter.getSize()}")

                    if (accumulatedScale > 1.03f && mediaAdapter.getSize() < 3) {
                        mediaAdapter.setSize(mediaAdapter.getSize() + 1)
                        updateGridSpan()
                    } else if (accumulatedScale < 0.97f && mediaAdapter.getSize() > 0) {
                        mediaAdapter.setSize(mediaAdapter.getSize() - 1)
                        updateGridSpan()
                    }
                }
            })

        binding.rcvMedia.setOnTouchListener { _, event ->
            scaleGestureDetector?.onTouchEvent(event)
            false
        }
    }

    private fun updateGridSpan() {
        val layoutManager = binding.rcvMedia.layoutManager as GridLayoutManager
        when (mediaAdapter.getSize()) {
            MediaAdapter.SizeAllMedia.SMALLEST -> {
                currentSpanCount = 14
            }

            MediaAdapter.SizeAllMedia.SMALL -> {
                currentSpanCount = 5

            }

            MediaAdapter.SizeAllMedia.MEDIUM -> {
                currentSpanCount = 3

            }

            MediaAdapter.SizeAllMedia.LARGE -> {
                currentSpanCount = 1

            }

            else -> currentSpanCount = 3
        }
        layoutManager.spanCount = currentSpanCount
        binding.rcvMedia.adapter?.notifyItemRangeChanged(
            0, binding.rcvMedia.adapter?.itemCount ?: 0
        )
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