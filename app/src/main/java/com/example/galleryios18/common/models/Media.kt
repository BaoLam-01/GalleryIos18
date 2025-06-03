package com.example.galleryios18.common.models

data class Media(
    var id: Long,
    var name: String = "",
    var path: String,
    var bucketId: Long = 0,
    var bucketName: String = "",
    var dateAdded: Long = 0,
    var dateTaken: Long = 0,
    var dateModifier: Long = 0,
    var width: Int,
    var height: Int,
    var size: Long,
    var isImage: Boolean = true,
    var duration: Long = 0L,
) {
    override fun toString(): String {
        return "id: $id\n" +
                "name: $name\n" +
                "path: $path\n" +
                "bucketId: $bucketId\n" +
                "bucketName: $bucketName\n" +
                "dateAdded: $dateAdded\n" +
                "dateTaken: $dateTaken\n" +
                "dateModifier: $dateModifier\n" +
                "width: $width\n" +
                "height: $height\n" +
                "size: $size\n" +
                "isImage: $isImage\n" +
                "duration: $duration"
    }
}