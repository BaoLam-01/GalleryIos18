package com.example.galleryios18.data.models

import com.example.galleryios18.common.models.Media

data class AlbumMemories(
    var title: String,
    var thumb: String,
    var date: Long,
    var listMedia: List<Media>,
    var isFavorite: Boolean
)