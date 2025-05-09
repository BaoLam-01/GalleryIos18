package com.example.galleryios18.common.models

data class Image(
    var name: String = "",
    var bucketId: Long = 0,
    var bucketName: String = "",
    var position: Int = 0,
    var dateModifier: Long = 0,
    var path: String = "",
    var time: Long = 0,
    var isImage: Boolean = true,
    var duration: Long = 0L
) {
}