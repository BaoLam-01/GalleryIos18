package com.example.galleryios18.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.galleryios18.common.LiveEvent
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.data.models.CollectionItem
import com.example.galleryios18.data.repository.LibraryViewRepository
import com.example.galleryios18.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val libraryViewRepository: LibraryViewRepository) :
    BaseViewModel() {
    val mLiveEventNavigateScreen: LiveEvent<Int> = LiveEvent()
    val allMediaLiveData: MutableLiveData<List<Media>> = MutableLiveData()
    val allCollectionLiveData: MutableLiveData<List<CollectionItem>> = MutableLiveData()

    fun getAllMedia() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e("LamPro: ${throwable.message}")
            }
        }) {
            allMediaLiveData.postValue(libraryViewRepository.getListLibs(false))
        }

    }

    fun getAllCollection() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e("LamPro: ${throwable.message}")
            }
        }) {
            val listCollection = mutableListOf<CollectionItem>()
            listCollection.add(CollectionItem(0, "Recent", emptyList()))
            listCollection.add(CollectionItem(1, "Persons", emptyList()))
            listCollection.add(CollectionItem(2, "Album", emptyList()))
            listCollection.add(CollectionItem(3, "History", emptyList()))
            listCollection.add(CollectionItem(3, "History", emptyList()))
            listCollection.add(CollectionItem(3, "History", emptyList()))
            listCollection.add(CollectionItem(3, "History", emptyList()))
            listCollection.add(CollectionItem(3, "History", emptyList()))
            allCollectionLiveData.postValue(listCollection)
        }

    }
}