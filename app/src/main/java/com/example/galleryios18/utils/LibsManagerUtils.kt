package com.example.galleryios18.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.galleryios18.common.models.Image

object LibsManagerUtils {

    fun getListImages(context: Context?): ArrayList<Image> {
        val listMedia: ArrayList<Image> = ArrayList()

        val projectionImage = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_ID
        )

        val imageCollectionUri: Uri = getImageCollectionUri()

        if (context != null) {
            val images: ArrayList<Image> = ArrayList()
            val cursor = context.contentResolver.query(
                imageCollectionUri,
                projectionImage,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val image = Image()
                    image.path = cursor.getString(cursor.getColumnIndexOrThrow(projectionImage[2]))
                    image.name = cursor.getString(cursor.getColumnIndexOrThrow(projectionImage[1]))
                    image.dateModifier = cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                            projectionImage[4]
                        )
                    )

                    if (cursor.getString(cursor.getColumnIndexOrThrow(projectionImage[3])) != null) {
                        image.bucketName = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                projectionImage[3]
                            )
                        )

                    }
                    if (image.bucketName.isEmpty()) {
                        image.bucketName = "No Name"
                    }
                    image.bucketId =
                        (cursor.getLong(cursor.getColumnIndexOrThrow(projectionImage[5])))
                    images.add(image)
                }
                cursor.close()
                listMedia.addAll(images)
            }
        }

        return listMedia
    }

    private fun getImageCollectionUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
        else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
}