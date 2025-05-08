package com.example.galleryios18.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.SupervisorJob

abstract class BaseViewModel : ViewModel() {
    var parentJob = SupervisorJob()
    override fun onCleared() {
        parentJob.cancel()
        super.onCleared()
    }
}