package com.tapbi.spark.launcherios18.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.galleryios18.App
import com.example.galleryios18.common.models.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaRepository @Inject constructor() {
    private var context: Context? = null

    private fun setContext() {
        if (context == null) {
            context = App.instance
        }
    }

    suspend fun getListImage(isJustImage: Boolean): ArrayList<Image> {
        return withContext(Dispatchers.IO) {
            setContext()
            val listMedia = java.util.ArrayList<Image>()
            val projectionVideo = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.BUCKET_ID
            )

            val projectionImage = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID
            )

            val imageCollectionUri = getImageCollectionUri()
            val videoCollectionUri = getVideoCollectionUri()

            if (context != null) {
                val images = java.util.ArrayList<Image>()
                val cursor = context!!.contentResolver.query(
                    imageCollectionUri,
                    projectionImage,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_ADDED + " DESC"
                )
                while (cursor!!.moveToNext()) {
                    val image = Image()
                    image.isImage = true
                    image.path = cursor.getString(cursor.getColumnIndexOrThrow(projectionImage[2]))
                    image.name = cursor.getString(cursor.getColumnIndexOrThrow(projectionImage[1]))
                    image.duration = 0
                    image.dateModifier =
                        cursor.getLong(cursor.getColumnIndexOrThrow(projectionImage[4]))
                    if (cursor.getString(cursor.getColumnIndexOrThrow(projectionImage[3])) != null) {
                        image.bucketName =
                            cursor.getString(cursor.getColumnIndexOrThrow(projectionImage[3]))
                    }
                    if (image.bucketName.isEmpty()) {
                        image.bucketName = "No Name"
                    }
                    image.bucketId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(projectionImage[5]))
                    images.add(image)
                }
                cursor.close()
                listMedia.addAll(images)
            }

            if (context != null && !isJustImage) {
                val images = java.util.ArrayList<Image>()
                val cursor = context!!.contentResolver.query(
                    videoCollectionUri,
                    projectionVideo,
                    null,
                    null,
                    MediaStore.Video.Media.DATE_ADDED + " DESC"
                )
                while (cursor!!.moveToNext()) {
                    val image = Image()
                    image.isImage = false
                    image.path = cursor.getString(cursor.getColumnIndexOrThrow(projectionVideo[2]))
                    image.name = cursor.getString(cursor.getColumnIndexOrThrow(projectionVideo[1]))
                    image.duration =
                        cursor.getLong(cursor.getColumnIndexOrThrow(projectionVideo[5]))
                    image.dateModifier =
                        cursor.getLong(cursor.getColumnIndexOrThrow(projectionVideo[4]))
                    if (cursor.getString(cursor.getColumnIndexOrThrow(projectionVideo[3])) != null) {
                        image.bucketName =
                            cursor.getString(cursor.getColumnIndexOrThrow(projectionVideo[3]))
                    }
                    if (image.bucketName.isEmpty()) {
                        image.bucketName = "No Name"
                    }
                    image.bucketId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(projectionVideo[6]))
                    images.add(image)
                }
                cursor.close()
                listMedia.addAll(images)
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