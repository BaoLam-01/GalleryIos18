package com.example.galleryios18.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.galleryios18.common.LiveEvent
import com.example.galleryios18.common.models.Image
import com.example.galleryios18.ui.base.BaseViewModel
import com.tapbi.spark.launcherios18.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mediaRepository: MediaRepository) :
    BaseViewModel() {
    val mLiveEventNavigateScreen: LiveEvent<Int> = LiveEvent()
    val allMediaLiveData: MutableLiveData<List<Image>> = MutableLiveData()

    fun getAllMedia() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e(": ${throwable.message}")
            }
        }) {
            allMediaLiveData.postValue(mediaRepository.getListImage(true))
        }

    }
}