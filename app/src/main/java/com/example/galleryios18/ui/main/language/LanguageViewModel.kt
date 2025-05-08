package com.example.galleryios18.ui.main.language

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.galleryios18.common.models.ListLanguage
import com.example.galleryios18.ui.base.BaseViewModel
import com.example.galleryios18.utils.LocaleUtils
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor() : BaseViewModel() {
    var listLanguage = MutableLiveData<ListLanguage>()

    fun getListLanguage(context: Context) {
        viewModelScope.launch(parentJob + Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
        }) {
            listLanguage.postValue(LocaleUtils.getListLanguage(context))
        }
    }
}