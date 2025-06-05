package com.example.galleryios18.data.repository

import com.example.galleryios18.common.models.Media
import com.example.galleryios18.data.models.AlbumRecent
import com.example.galleryios18.data.models.ItemForMonth
import com.example.galleryios18.data.models.ItemThumbInMonth
import com.example.galleryios18.utils.MediaRepository
import timber.log.Timber
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LibraryViewRepository @Inject constructor(private val mediaRepository: MediaRepository) {

    private val listAllMedia: MutableList<Media> = mutableListOf()
    fun getAllMedia(isJustImage: Boolean): List<Media> {
        listAllMedia.clear()
        listAllMedia.addAll(mediaRepository.getListMedia(isJustImage))
        return listAllMedia
    }

    fun getAllAlbumRecent(): List<AlbumRecent> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis

        val thirtyDaysAgo = todayStart - TimeUnit.DAYS.toMillis(30)

        val recentImages = listAllMedia.filter { it.dateTaken >= thirtyDaysAgo }

        val imagesGroupedByDay = recentImages.groupBy { media ->
            calendar.timeInMillis = media.dateTaken
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }

        return imagesGroupedByDay.map { (dayMillis, listMedia) ->
            val thumb = listMedia.firstOrNull()?.path ?: ""

            AlbumRecent(day = dayMillis, thumb = thumb)
        }.sortedByDescending { it.day }
    }

    fun getFirstMediaOfEachYear(): List<Media> {
        val sortedPhotos = listAllMedia.sortedBy { it.dateTaken }

        val firstMediaOfYear = sortedPhotos
            .groupBy { media ->
                val calendar = Calendar.getInstance().apply { timeInMillis = media.dateTaken }
                calendar.get(Calendar.YEAR)
            }
            .mapValues { it.value.first() }
            .values
            .toList()

        Timber.e("LamPro | getFirstPhotoOfEachYear - firstPhotosOfYear size: ${firstMediaOfYear.size}")
        return firstMediaOfYear
    }

    fun getListItemForMonth(): List<ItemForMonth> {
        val listItemDay = createItemThumbInMonthList()
        val listItemForMonth = listItemDay
            .groupBy { getYearTimestamp(it.month) }
            .map { (year, daysInYear) ->
                when (daysInYear.size) {
                    1 -> {
                        daysInYear.first().ratioWidth = 6
                        daysInYear.first().ratioHeight = 4
                    }

                    2 -> {
                        daysInYear[0].ratioWidth = 3
                        daysInYear[0].ratioHeight = 4
                        daysInYear[1].ratioWidth = 3
                        daysInYear[1].ratioHeight = 4
                    }

                    3 -> {
                        daysInYear[0].ratioWidth = 4
                        daysInYear[0].ratioHeight = 4
                        daysInYear[1].ratioWidth = 2
                        daysInYear[1].ratioHeight = 2
                        daysInYear[2].ratioWidth = 2
                        daysInYear[2].ratioHeight = 2

                    }

                    4 -> {
                        daysInYear[0].ratioWidth = 6
                        daysInYear[0].ratioHeight = 4
                        daysInYear[1].ratioWidth = 2
                        daysInYear[1].ratioHeight = 2
                        daysInYear[2].ratioWidth = 2
                        daysInYear[2].ratioHeight = 2
                        daysInYear[3].ratioWidth = 2
                        daysInYear[3].ratioHeight = 2

                    }

                    5 -> {
                        daysInYear[0].ratioWidth = 2
                        daysInYear[0].ratioHeight = 2
                        daysInYear[1].ratioWidth = 4
                        daysInYear[1].ratioHeight = 4
                        daysInYear[2].ratioWidth = 2
                        daysInYear[2].ratioHeight = 2
                        daysInYear[3].ratioWidth = 2
                        daysInYear[3].ratioHeight = 2
                        daysInYear[4].ratioWidth = 4
                        daysInYear[4].ratioHeight = 2
                    }
                }
                ItemForMonth(year, daysInYear)
            }
            .sortedBy { it.year }
        return listItemForMonth
    }


    fun createItemThumbInMonthList(): List<ItemThumbInMonth> {
        val monthThumbs = listAllMedia
            .groupBy { getMonthTimestamp(it.dateTaken) }
            .map { (monthTimestamp, imagesInMonth) ->
                val firstImage = imagesInMonth.first()
                ItemThumbInMonth(monthTimestamp, firstImage.path, imagesInMonth.size, 0, 0)
            }
            .sortedBy { it.month }

        val monthGroups = listOf(
            listOf(1, 2, 3),
            listOf(4, 5),
            listOf(6, 7),
            listOf(8, 9),
            listOf(10, 11, 12)
        )

        val calendar = Calendar.getInstance()
        val selectedThumbs = mutableListOf<ItemThumbInMonth>()

        for (group in monthGroups) {
            val item = monthThumbs.find { item ->
                calendar.timeInMillis = item.month
                val month = calendar.get(Calendar.MONTH) + 1
                month in group
            }

            item?.let {
                selectedThumbs.add(it)
            }

            if (selectedThumbs.size >= 5) break
        }

        return selectedThumbs
    }


    fun getMonthTimestamp(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getYearTimestamp(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.MONTH, 0) // Tháng 1 (0)
            set(Calendar.DAY_OF_MONTH, 1) // Ngày 1
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

}