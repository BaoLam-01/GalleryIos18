package com.example.galleryios18.utils

import com.example.galleryios18.common.models.Album
import com.example.galleryios18.common.models.Media

object ImageHelper {

    fun singleListFromImage(media: Media): ArrayList<Media> {
        val listMedia = arrayListOf<Media>()
        listMedia.add(media)
        return listMedia
    }

    fun folderListFromImages(media: List<Media>): List<Album> {
        val folderMap: MutableMap<Long, Album> = LinkedHashMap()
        for (image in media) {
            val bucketId = image.bucketId
            val bucketName = image.bucketName
            var folder = folderMap[bucketId]
            if (folder == null) {
                folder = Album(bucketId, bucketName)
                folderMap[bucketId] = folder
            }
            folder.media.add(image)
        }
        return ArrayList(folderMap.values)
    }

    fun filterImages(media: ArrayList<Media>, bucketId: Long?): ArrayList<Media> {
        if (bucketId == null || bucketId == 0L) return media

        val filteredMedia = arrayListOf<Media>()
        for (image in media) {
            if (image.bucketId == bucketId) {
                filteredMedia.add(image)
            }
        }
        return filteredMedia
    }

    fun findImageIndex(media: Media, listMedia: ArrayList<Media>): Int {
        for (i in listMedia.indices) {
            if (listMedia[i].path == media.path) {
                return i
            }
        }
        return -1
    }

    fun findImageIndexes(subMedia: ArrayList<Media>, media: ArrayList<Media>): ArrayList<Int> {
        val indexes = arrayListOf<Int>()
        for (image in subMedia) {
            for (i in media.indices) {
                if (media[i].path == image.path) {
                    indexes.add(i)
                    break
                }
            }
        }
        return indexes
    }


    fun isGifFormat(media: Media): Boolean {
        val fileName = media.name
        val extension = if (fileName.contains(".")) {
            fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
        } else ""

        return extension.equals("gif", ignoreCase = true)
    }

}