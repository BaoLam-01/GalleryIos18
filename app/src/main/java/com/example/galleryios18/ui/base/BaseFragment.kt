package com.example.galleryios18.ui.base

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import com.example.galleryios18.data.local.SharedPreferenceHelper
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {
    @Inject
    lateinit var mPrefHelper: SharedPreferenceHelper
}