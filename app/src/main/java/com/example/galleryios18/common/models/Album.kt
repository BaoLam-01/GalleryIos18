package com.example.galleryios18.common.models

import java.util.*

data class Album(
    var bucketId: Long,
    var name: String,
    var dateModifier: Long = 0L,
    var imgThumb: String = "",
    var media: ArrayList<Media> = arrayListOf()
)
