package com.example.galleryios18.data.repository

import com.example.galleryios18.common.models.Media
import com.example.galleryios18.utils.MediaRepository
import javax.inject.Inject

class LibraryViewRepository @Inject constructor(private val mediaRepository: MediaRepository) {

    suspend fun getListLibs(isJustImage: Boolean): List<Media> {
        return mediaRepository.getListMedia(isJustImage)
    }
}