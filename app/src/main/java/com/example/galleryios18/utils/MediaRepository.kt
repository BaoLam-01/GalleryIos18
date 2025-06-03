package com.example.galleryios18.utils

import android.content.Context
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.example.galleryios18.App
import com.example.galleryios18.common.models.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MediaRepository @Inject constructor() {
    private var context: Context? = null

    private fun setContext() {
        if (context == null) {
            context = App.instance
        }
    }

    fun getListMedia(isJustImage: Boolean): List<Media> {
        Timber.e("LamPro | getListMedia - ")
        setContext()
        val listMedia = java.util.ArrayList<Media>()
        val projectionVideo = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION
        )

        val projectionImage = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.SIZE,
        )

        val imageCollectionUri = getImageCollectionUri()
        val videoCollectionUri = getVideoCollectionUri()

        if (context != null) {
            val listImage = java.util.ArrayList<Media>()
            val cursor = context!!.contentResolver.query(
                imageCollectionUri,
                projectionImage,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
            )
            while (cursor!!.moveToNext()) {
                val file =
                    File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)))
                if (!file.exists() || file.length() <= 0) {
                    continue
                }

                val media = Media(
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)),
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                        ?: "",
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)) * 1000,
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)) * 1000,
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                    true,
                    0L
                )
                if (media.bucketName.isEmpty()) {
                    media.bucketName = "No Name"
                }

//                Timber.e("LamPro | getListMedia - media image: $media")
                if (media.dateTaken == 0L) {
                    media.dateTaken = media.dateAdded
                }
                listImage.add(media)
            }
            cursor.close()
            listMedia.addAll(listImage)
        }

        if (context != null && !isJustImage) {
            val listVideo = java.util.ArrayList<Media>()
            val cursor = context!!.contentResolver.query(
                videoCollectionUri,
                projectionVideo,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
            )
            while (cursor!!.moveToNext()) {
                val file =
                    File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                if (!file.exists() || file.length() <= 0) {
                    continue
                }

                val (realWidth, realHeight) = getRealVideoSize(file.path)
                val media = Media(
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)),
                    cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                        ?: "",
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)) * 1000,
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)) * 1000,
                    realWidth,
                    realHeight,
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)),
                    false,
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                )
                if (media.bucketName.isEmpty()) {
                    media.bucketName = "No Name"
                }
//                    Timber.e("LamPro | getListMedia - media video: $media")
                if (media.dateTaken == 0L) {
                    media.dateTaken = media.dateAdded
                }

                listVideo.add(media)
            }
            cursor.close()
            listMedia.addAll(listVideo)
        }
        val listSort = listMedia.sortedBy { it.dateTaken }
        return listSort
    }

    fun getRealVideoSize(path: String): Pair<Int, Int> {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toIntOrNull() ?: 0
            val rotation =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                    ?.toIntOrNull() ?: 0

            if (rotation == 90 || rotation == 270) {
                Pair(height, width)
            } else {
                Pair(width, height)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(0, 0)
        } finally {
            retriever.release()
        }
    }


    fun sortVideosByCreatedDateAscending(list: ArrayList<Media>) {
        list.sortBy { it.dateAdded }
    }

    private fun getVideoCollectionUri(): Uri {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    private fun getImageCollectionUri(): Uri {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
}
