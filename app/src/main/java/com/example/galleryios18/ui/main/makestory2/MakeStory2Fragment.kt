package com.example.galleryios18.ui.main.makestory2

import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentMakeStory2Binding
import com.example.galleryios18.ui.base.BaseBindingFragment
import timber.log.Timber

class MakeStory2Fragment : BaseBindingFragment<FragmentMakeStory2Binding, MakeStory2ViewModel>() {

    override fun getViewModel(): Class<MakeStory2ViewModel> {
        return MakeStory2ViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_make_story_2

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {
            Timber.e("LamPro | onCreatedView - all media obsver : ${it.size}")
            if (it.isNotEmpty()) {
                binding.storyPlayerView.setMediaList(it)
                binding.storyPlayerView.start()
            }
        }
        mainViewModel.getAllMedia()

    }

    override fun observerData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.storyPlayerView.release()
    }
}