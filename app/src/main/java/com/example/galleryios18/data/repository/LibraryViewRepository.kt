package com.example.galleryios18.data.repository

import com.example.galleryios18.common.models.Media
import com.example.galleryios18.data.models.AlbumRecent
import com.example.galleryios18.utils.MediaRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LibraryViewRepository @Inject constructor(private val mediaRepository: MediaRepository) {

    private val listAllMedia: MutableList<Media> = mutableListOf()
    fun getListLibs(isJustImage: Boolean): List<Media> {
        listAllMedia.clear()
        listAllMedia.addAll(mediaRepository.getListMedia(isJustImage))
        return listAllMedia
    }

    fun getAllAlbumRecent(): List<AlbumRecent> {
        val calendar = Calendar.getInstance()

        // Đặt thời gian bắt đầu của ngày hôm nay
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis

        // Thời điểm 30 ngày trước
        val thirtyDaysAgo = todayStart - TimeUnit.DAYS.toMillis(30)

        // Lọc media trong 30 ngày gần nhất
        val recentImages = listAllMedia.filter { it.dateTaken >= thirtyDaysAgo }

        // Gom nhóm media theo ngày
        val imagesGroupedByDay = recentImages.groupBy { media ->
            calendar.timeInMillis = media.dateTaken
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }

        return imagesGroupedByDay.map { (dayMillis, listMedia) ->
            // Lấy thumb (đường dẫn hoặc uri của media đầu tiên)
            val thumb = listMedia.firstOrNull()?.path ?: ""

            AlbumRecent(day = dayMillis, thumb = thumb)
        }.sortedByDescending { it.day }
    }


}