package com.example.galleryios18.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
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
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.databinding.FragmentHomeBinding
import com.example.galleryios18.ui.adapter.CollectionAdapter
import com.example.galleryios18.ui.adapter.AllMediaAdapter
import com.example.galleryios18.ui.adapter.MonthMediaAdapter
import com.example.galleryios18.ui.adapter.YearMediaAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.custom.GroupHeaderDecoration
import com.example.galleryios18.ui.main.MainActivity
import com.example.galleryios18.ui.main.custommize.CustomizeBottomSheet
import com.example.galleryios18.utils.Utils
import com.example.galleryios18.utils.ViewUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.tapbi.spark.launcherios18.utils.PermissionHelper
import timber.log.Timber
import java.util.Calendar

class HomeFragment : BaseBindingFragment<FragmentHomeBinding, HomeViewModel>() {
    private var requestPermission = true
    private lateinit var allMediaAdapter: AllMediaAdapter
    private lateinit var monthMediaAdapter: MonthMediaAdapter
    private lateinit var yearMediaAdapter: YearMediaAdapter
    private lateinit var collectionAdapter: CollectionAdapter
    private var isRcvAllMediaTop = true

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var currentSpanCount = 3 // Số cột hiện tại
    private var accumulatedScale = 1f

    private lateinit var customizeBottomSheet: CustomizeBottomSheet

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
            allMediaAdapter.setData(it)
            binding.rcvAllMedia.scrollToPosition(it.size - 1)
        }

        mainViewModel.listItemForYearLiveData.observe(viewLifecycleOwner) {
            yearMediaAdapter.setData(it)
        }

        mainViewModel.listItemForMonthLiveData.observe(viewLifecycleOwner) {
            monthMediaAdapter.setData(it)
        }

        mainViewModel.listAlbumLast30Days.observe(viewLifecycleOwner) { list ->
            mainViewModel.listCollectionItem.forEach {
                if (it.type == Constant.RECENT_DAY) {
                    it.listItem = list
                    collectionAdapter.notifyItemChanged(0)
                }
            }
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
        initRcvAllMedia()
        initRcvMonthMedia()
        initRcvYearMedia()
        initRcvCollection()
        initTabLayout()
        initCustomizeBottomSheet()

        ViewUtils.adjustViewWithSystemBar(
            binding.tvTitle, binding.imgSort, requireActivity() as MainActivity
        )

    }

    private fun initRcvAllMedia() {
        allMediaAdapter = AllMediaAdapter()
        allMediaAdapter.setSize(AllMediaAdapter.SizeAllMedia.MEDIUM, requireContext())
        binding.viewBgGradient.post {
            binding.rcvAllMedia.setPadding(
                binding.rcvAllMedia.paddingLeft,
                binding.tvCountItem.bottom + 20,
                binding.rcvAllMedia.paddingRight,
                binding.rcvAllMedia.paddingBottom
            )
        }
        binding.rcvAllMedia.layoutParams.let {
            val layoutParams = it as ConstraintLayout.LayoutParams
            layoutParams.height =
                Utils.getScreenHeight(requireContext()) + (requireActivity() as MainActivity).navigationBarHeight + (requireActivity() as MainActivity).statusBarHeight
            binding.rcvAllMedia.layoutParams = layoutParams
        }
        binding.rcvAllMedia.apply {
            clipToPadding = false
            val gridLayout =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            gridLayout.isItemPrefetchEnabled = true
            layoutManager = gridLayout

            addItemDecoration(GroupHeaderDecoration { position ->
                if (currentSpanCount == Constant.SPAN_COUNT_SMALLEST) {
                    allMediaAdapter.getItemTime(position)
                } else {
                    ""
                }
            })
            adapter = allMediaAdapter
        }
        TransitionManager.beginDelayedTransition(binding.rcvAllMedia)
    }

    private fun initRcvMonthMedia() {
        monthMediaAdapter = MonthMediaAdapter()
        binding.viewBgGradient.post {
            binding.rcvMonthMedia.setPadding(
                binding.rcvMonthMedia.paddingLeft,
                binding.tvCountItem.bottom + 20,
                binding.rcvMonthMedia.paddingRight,
                binding.rcvMonthMedia.paddingBottom
            )
        }
//        binding.rcvMonthMedia.layoutParams.let {
//            val layoutParams = it as ConstraintLayout.LayoutParams
//            layoutParams.height =
//                Utils.getScreenHeight(requireContext()) + (requireActivity() as MainActivity).navigationBarHeight + (requireActivity() as MainActivity).statusBarHeight
//            binding.rcvMonthMedia.layoutParams = layoutParams
//        }
        binding.rcvMonthMedia.apply {
            clipToPadding = false
            val linearLayout =
                LinearLayoutManager(requireContext(), GridLayoutManager.VERTICAL, false)
            layoutManager = linearLayout

            adapter = monthMediaAdapter
        }
    }

    private fun initRcvYearMedia() {
        yearMediaAdapter = YearMediaAdapter()
        binding.viewBgGradient.post {
            binding.rcvYearMedia.setPadding(
                binding.rcvYearMedia.paddingLeft,
                binding.viewBgGradient.bottom + 20,
                binding.rcvYearMedia.paddingRight,
                binding.rcvYearMedia.paddingBottom
            )
        }

        binding.rcvYearMedia.apply {
            clipToPadding = false
            val linearLayout =
                LinearLayoutManager(requireContext(), GridLayoutManager.VERTICAL, false)
            layoutManager = linearLayout

            adapter = yearMediaAdapter
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
        collectionAdapter.setData(mainViewModel.listCollectionItem)
        binding.rcvCollection.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = collectionAdapter
        }
    }

    private fun initTabLayout() {
        binding.tlBottom.addTab(
            binding.tlBottom.newTab().setText(getText(R.string.year))
        )
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

    private fun initCustomizeBottomSheet() {
        customizeBottomSheet = CustomizeBottomSheet()
    }

    private fun changeTabLayout(position: Int) {
        when (position) {
            TabImage.TAB_YEAR -> {
                binding.rcvAllMedia.visibility = View.INVISIBLE
                binding.rcvMonthMedia.visibility = View.INVISIBLE
                binding.rcvYearMedia.visibility = View.VISIBLE
            }

            TabImage.TAB_MONTH -> {
                binding.rcvAllMedia.visibility = View.INVISIBLE
                binding.rcvMonthMedia.visibility = View.VISIBLE
                binding.rcvYearMedia.visibility = View.INVISIBLE
            }

            TabImage.TAB_ALL_PHOTO -> {
                binding.rcvAllMedia.visibility = View.VISIBLE
                binding.rcvMonthMedia.visibility = View.INVISIBLE
                binding.rcvYearMedia.visibility = View.INVISIBLE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listener() {
        allMediaAdapter.setListener(object : AllMediaAdapter.IMediaClick {
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
                val viewTop = binding.rcvAllMedia.top
                if (viewTop - scrollY == 0) {
                    isRcvAllMediaTop = true
                } else {
                    isRcvAllMediaTop = false
                }
                binding.rcvAllMedia.isScrollEnabled = isRcvAllMediaTop == true
                binding.rcvMonthMedia.isScrollEnabled = isRcvAllMediaTop == true
                binding.rcvYearMedia.isScrollEnabled = isRcvAllMediaTop == true
                Timber.e("LamPro | listener - iisrcvAllMediaTop : $isRcvAllMediaTop")

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
                    Timber.e("LamPro | onScaleEnd - size: ${allMediaAdapter.getSize()}")

                    if (accumulatedScale > 1.03f && allMediaAdapter.getSize() < 3) {
                        allMediaAdapter.setSize(allMediaAdapter.getSize() + 1, requireContext())
                        updateGridSpan()
                    } else if (accumulatedScale < 0.97f && allMediaAdapter.getSize() > 0) {
                        allMediaAdapter.setSize(allMediaAdapter.getSize() - 1, requireContext())
                        updateGridSpan()
                    }
                }
            })

        binding.rcvAllMedia.setOnTouchListener { _, event ->
            scaleGestureDetector?.onTouchEvent(event)
            false
        }


        binding.tvCustomizeAndReorder.setOnClickListener {
            customizeBottomSheet.show(childFragmentManager, "customize")
        }
    }

    private fun updateGridSpan() {
        val layoutManager = binding.rcvAllMedia.layoutManager as GridLayoutManager
        when (allMediaAdapter.getSize()) {
            AllMediaAdapter.SizeAllMedia.SMALLEST -> {
                currentSpanCount = Constant.SPAN_COUNT_SMALLEST
            }

            AllMediaAdapter.SizeAllMedia.SMALL -> {
                currentSpanCount = Constant.SPAN_COUNT_SMALL
            }

            AllMediaAdapter.SizeAllMedia.MEDIUM -> {
                currentSpanCount = Constant.SPAN_COUNT_MEDIUM
            }

            AllMediaAdapter.SizeAllMedia.LARGE -> {
                currentSpanCount = Constant.SPAN_COUNT_LARGE
            }

            else -> currentSpanCount = 3
        }
        layoutManager.spanCount = currentSpanCount
        binding.rcvAllMedia.adapter?.notifyItemRangeChanged(
            0, binding.rcvAllMedia.adapter?.itemCount ?: 0
        )
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
        const val TAB_YEAR = 0
        const val TAB_MONTH = 1
        const val TAB_ALL_PHOTO = 2
    }

}