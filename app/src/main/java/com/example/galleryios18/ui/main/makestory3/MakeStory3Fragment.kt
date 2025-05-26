package com.example.galleryios18.ui.main.makestory3

import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentMakeStory2Binding
import com.example.galleryios18.databinding.FragmentMakeStory3Binding
import com.example.galleryios18.ui.base.BaseBindingFragment
import timber.log.Timber

class MakeStory3Fragment : BaseBindingFragment<FragmentMakeStory3Binding, MakeStory3ViewModel>() {

    override fun getViewModel(): Class<MakeStory3ViewModel> {
        return MakeStory3ViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_make_story_3

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {
            Timber.e("LamPro | onCreatedView - all media obsver : ${it.size}")
            if (it.isNotEmpty()) {
                binding.storyView.setListItem(it)
            }
        }
        mainViewModel.getAllMedia()

    }

    override fun observerData() {
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}