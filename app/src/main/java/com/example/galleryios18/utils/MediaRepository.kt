package com.example.galleryios18.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.galleryios18.App
import com.example.galleryios18.common.models.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MediaRepository @Inject constructor() {
    private var context: Context? = null

    private fun setContext() {
        if (context == null) {
            context = App.instance
        }
    }

    suspend fun getListMedia(isJustImage: Boolean): ArrayList<Media> {
        return withContext(Dispatchers.IO) {
            setContext()
            val listMedia = java.util.ArrayList<Media>()
            val projectionVideo = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.SIZE,
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
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                        true,
                        0L
                    )
                    if (media.bucketName.isEmpty()) {
                        media.bucketName = "No Name"
                    }

//                    Timber.e("LamPro | getListMedia - media image: $media")
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
                        File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)))
                    if (!file.exists() || file.length() <= 0) {
                        continue
                    }

                    val media = Media(
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                        false,
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                    )
                    if (media.bucketName.isEmpty()) {
                        media.bucketName = "No Name"
                    }
//                    Timber.e("LamPro | getListMedia - media video: $media")
                    listVideo.add(media)
                }
                cursor.close()
                listMedia.addAll(listVideo)
            }

            listMedia
        }

    }

    private fun getVideoCollectionUri(): Uri {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    private fun getImageCollectionUri(): Uri {
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

}