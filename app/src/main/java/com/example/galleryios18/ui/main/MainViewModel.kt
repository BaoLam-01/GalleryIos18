package com.example.galleryios18.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val libraryViewRepository: LibraryViewRepository) :
    BaseViewModel() {
    val mLiveEventNavigateScreen: LiveEvent<Int> = LiveEvent()
    val allMediaLiveData: MutableLiveData<List<Media>> = MutableLiveData()
    val allCollectionLiveData: MutableLiveData<List<CollectionItem>> = MutableLiveData()
    val listItemJsonLiveData = MutableLiveData<List<String>>()

    fun getAllMedia() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e("LamPro: ${throwable.message}")
            }
        }) {
            val listMedia = libraryViewRepository.getListLibs(false)
            allMediaLiveData.postValue(listMedia)
        }

    }

    private fun getAllImageLast30Days(listMedia: List<Media>): List<AlbumRecent> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis

        val thirtyDaysAgo = todayStart - TimeUnit.DAYS.toMillis(30)

        val recentImages = listMedia.filter { it.dateTaken >= thirtyDaysAgo }

        val imagesGroupedByDay = recentImages.groupBy { media ->
            calendar.timeInMillis = media.dateTaken
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return imagesGroupedByDay
            .map { (dayMillis, listMedia) ->
                val dayString = dateFormat.format(Date(dayMillis))
                AlbumRecent(day = dayString, listMedia = listMedia)
            }
            .sortedByDescending { it.day }
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