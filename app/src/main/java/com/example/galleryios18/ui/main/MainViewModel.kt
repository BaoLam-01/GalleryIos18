package com.example.galleryios18.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.LiveEvent
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.data.models.AlbumRecent
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
    val listCollectionItem: MutableList<CollectionItem> = mutableListOf()
    val listItemJsonLiveData = MutableLiveData<List<String>>()
    val listAlbumLast30Days = MutableLiveData<List<AlbumRecent>>()

    init {
        initListCollection()
    }

    fun getAllMedia() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e("LamPro: ${throwable.message}")
            }
        }) {
            val listMedia = libraryViewRepository.getListLibs(false)
            allMediaLiveData.postValue(listMedia)
            val listLastImage = libraryViewRepository.getAllAlbumRecent()
            Timber.e("LamPro - size list last image: ${listLastImage.size}")
            listAlbumLast30Days.postValue(listLastImage)
        }

    }


    fun initListCollection() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e("LamPro: ${throwable.message}")
            }
        }) {
            val listCollection = mutableListOf<CollectionItem>()
            listCollection.add(CollectionItem(Constant.RECENT_DAY, "Recent Days", emptyList()))
            listCollection.add(CollectionItem(Constant.MEMORIES, "Memories", emptyList()))
            listCollection.add(CollectionItem(Constant.MEDIA_TYPES, "Media types", emptyList()))
            listCollection.add(CollectionItem(Constant.UTILITIES, "Utilities", emptyList()))
            listCollection.add(CollectionItem(Constant.ALBUMS, "Albums", emptyList()))
            listCollectionItem.addAll(listCollection)
        }

    }
}