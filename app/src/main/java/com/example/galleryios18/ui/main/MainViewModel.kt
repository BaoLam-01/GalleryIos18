package com.example.galleryios18.ui.main

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.galleryios18.common.LiveEvent
import com.example.galleryios18.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel() {
    val mLiveEventNavigateScreen: LiveEvent<Int> = LiveEvent()
    val closePremiumDialog: MutableLiveData<Boolean> = LiveEvent()
}