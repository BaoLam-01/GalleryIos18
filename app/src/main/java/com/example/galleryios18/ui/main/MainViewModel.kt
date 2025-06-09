package com.example.galleryios18.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.LiveEvent
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.data.models.CollectionItem
import com.example.galleryios18.data.models.ItemForMonth
import com.example.galleryios18.data.models.ItemMediaTypeUtilities
import com.example.galleryios18.data.repository.LibraryViewRepository
import com.example.galleryios18.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val libraryViewRepository: LibraryViewRepository) :
    BaseViewModel() {
    val mLiveEventNavigateScreen: LiveEvent<Int> = LiveEvent()
    val allMediaLiveData: MutableLiveData<List<Media>> = MutableLiveData()
    val listItemForYearLiveData: MutableLiveData<List<Media>> = MutableLiveData()
    val listItemForMonthLiveData: MutableLiveData<List<ItemForMonth>> = MutableLiveData()
    val listCollectionItem: MutableList<CollectionItem> = mutableListOf()
    val listItemJsonLiveData = MutableLiveData<List<String>>()

    private val _positionCollectionChange = Channel<Int>(Channel.UNLIMITED)
    val positionCollectionChange = _positionCollectionChange.receiveAsFlow()

    val listMediaTypes: MutableList<ItemMediaTypeUtilities> = mutableListOf()

    init {
        initListCollection()
        initListMediaTypes()
    }

    fun getAllMedia() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e("LamPro: ${throwable.message}")
            }
        }) {
            val listMedia = libraryViewRepository.getAllMedia(false)
            allMediaLiveData.postValue(listMedia)

            val firstMediaEachMonth = libraryViewRepository.getFirstMediaOfEachYear()
            listItemForYearLiveData.postValue(firstMediaEachMonth)

            val listItemForMonth = libraryViewRepository.getListItemForMonth()
            listItemForMonthLiveData.postValue(listItemForMonth)

            updateCollectionAlbumRecent()

            updateCollectionAlbumMemories()

            updateCountMediaTypesUtilities()
        }

    }

    private suspend fun updateCollectionAlbumRecent() {
        val listAlbumRecent = libraryViewRepository.getAllAlbumRecent()
        updateListCollectionItem(Constant.RECENT_DAY, listAlbumRecent)
    }

    private suspend fun updateCollectionAlbumMemories() {
        val listAlbumMemories = libraryViewRepository.getListAlbumMemories()
        updateListCollectionItem(Constant.MEMORIES, listAlbumMemories)
    }

    private suspend fun updateListCollectionItem(type: Int, list: List<Any>) {
        for (position in listCollectionItem.indices) {
            if (listCollectionItem[position].type == type) {
                listCollectionItem[position].listItem = list
                _positionCollectionChange.send(position)
                break
            }
        }
    }


    private fun initListCollection() {
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

    private fun initListMediaTypes() {
        listMediaTypes.add(
            ItemMediaTypeUtilities(
                R.drawable.ic_video,
                Constant.VIDEO,
                R.string.videos,
                0
            )
        )

        listMediaTypes.add(
            ItemMediaTypeUtilities(
                R.drawable.ic_selfie,
                Constant.SELFIE,
                R.string.selfies,
                0
            )
        )
        listMediaTypes.add(
            ItemMediaTypeUtilities(
                R.drawable.ic_screenshots,
                Constant.SCREENSHOTS,
                R.string.screenShots,
                0
            )
        )
        listMediaTypes.add(
            ItemMediaTypeUtilities(
                R.drawable.ic_screen_recordings,
                Constant.SCREEN_RECORDINGS,
                R.string.screen_recordings,
                0
            )
        )

        viewModelScope.launch {
            updateListCollectionItem(Constant.MEDIA_TYPES, listMediaTypes)
        }

    }

    private suspend fun updateCountMediaTypesUtilities() {
        val countVideo = libraryViewRepository.getCountVideo()
        val countSelfie = libraryViewRepository.getCountSelfie()
        val countScreenshots = libraryViewRepository.getCountScreenShort()
        val countScreenRecordings = libraryViewRepository.getCountScreenRecordings()
        val countFavorite = libraryViewRepository.getCountFavorite()
        listMediaTypes.forEach {
            when (it.type) {
                Constant.VIDEO -> it.count = countVideo
                Constant.SELFIE -> it.count = countSelfie
                Constant.SCREENSHOTS -> it.count = countScreenshots
                Constant.SCREEN_RECORDINGS -> it.count = countScreenRecordings
            }
        }
        updateListCollectionItem(Constant.MEDIA_TYPES, listMediaTypes)
    }

    private fun initListUtilities() {

    }

}