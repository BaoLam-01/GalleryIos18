package com.example.galleryios18.common.models

import java.util.*

data class Folder(
    var bucketId: Long,
    var name: String,
    var images: ArrayList<Image> = arrayListOf()
)
