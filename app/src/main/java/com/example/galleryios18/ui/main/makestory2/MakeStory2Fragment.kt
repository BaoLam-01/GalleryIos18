package com.example.galleryios18.ui.main.makestory2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentMakeStory2Binding
import com.example.galleryios18.ui.adapter.SlideAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.ui.custom.FadeZoomPageTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MakeStory2Fragment : BaseBindingFragment<FragmentMakeStory2Binding, MakeStory2ViewModel>() {
    private lateinit var slideAdapter: SlideAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollDelay = 3000L // thời gian giữa các slide

    var isUserScrolling = false
    override fun getViewModel(): Class<MakeStory2ViewModel> {
        return MakeStory2ViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_make_story_2

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        slideAdapter = SlideAdapter()
        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {
            slideAdapter.setData(it)
            startAutoScroll(binding.vpShowImage)
        }
        mainViewModel.getAllMedia()

        binding.vpShowImage.adapter = slideAdapter
        binding.vpShowImage.setPageTransformer(FadeZoomPageTransformer())


//        binding.vpShowImage.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//            override fun onPageScrollStateChanged(state: Int) {
//
//                // Nếu người dùng bắt đầu vuốt tay
//                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
//                    isUserScrolling = true
//                    binding.vpShowImage.setPageTransformer(null) // Bỏ hiệu ứng
//                }
//
//                // Khi vuốt xong, quay lại chế độ tự động
//                if (state == ViewPager2.SCROLL_STATE_IDLE && isUserScrolling) {
//                    isUserScrolling = false
//                    binding.vpShowImage.setPageTransformer(FadeZoomPageTransformer()) // Gắn lại hiệu ứng
//                }
//                super.onPageScrollStateChanged(state)
//            }
//        })
        binding.vpShowImage

    }

    override fun observerData() {
    }

    fun startAutoScroll(
        viewPager: ViewPager2,
        delayBetweenSlides: Long = 3000L,
        scrollDuration: Long = 500L
    ) {
        val frameRate = 60
        val steps = frameRate * scrollDuration / 1000
        val delayPerFrame = 1000L / frameRate

        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {
            while (true) {
                delay(delayBetweenSlides)
                val width = viewPager.width
                val distancePerStep = width / steps.toFloat()

                if (viewPager.currentItem < (viewPager.adapter?.itemCount ?: 0) - 1) {
                    if (viewPager.beginFakeDrag()) {
                        repeat(steps.toInt()) {
                            viewPager.fakeDragBy(-distancePerStep)
                            delay(delayPerFrame)
                        }
                        viewPager.endFakeDrag()
                    }
                } else {
                    // Quay về trang đầu sau khi hết
                    viewPager.setCurrentItem(0, false)
                }
            }
        }
    }

}