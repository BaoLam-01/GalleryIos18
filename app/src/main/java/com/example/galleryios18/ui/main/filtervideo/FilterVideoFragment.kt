package com.example.galleryios18.ui.main.filtervideo

import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentFilterVideoBinding
import com.example.galleryios18.ui.base.BaseBindingFragment
import timber.log.Timber


class FilterVideoFragment :
    BaseBindingFragment<FragmentFilterVideoBinding, FilterVideoViewModel>() {
    override fun getViewModel(): Class<FilterVideoViewModel> {
        return FilterVideoViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_filter_video

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

    }

    override fun observerData() {

        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {
            Timber.e("LamPro | observerData - size media: ${it.size}")

        }
    }
}