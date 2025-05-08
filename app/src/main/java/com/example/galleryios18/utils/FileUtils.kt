package com.example.galleryios18.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import com.example.galleryios18.App
import com.example.galleryios18.common.Constant
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


object FileUtils {

    fun downloadBackground(background: String): String {
        val folder =
            File(App.instance.filesDir.toString() + File.separator + Constant.FILE_BACKGROUND_STORAGE)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return download(background, folder)
    }

    fun downloadBody(body: String): String {
        val folder =
            File(App.instance.filesDir.toString() + File.separator + Constant.FILE_BODY_STORAGE)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return download(body, folder)
    }

    fun downloadZip(zip: String): String {
        val folder =
            File(App.instance.filesDir.toString() + File.separator + Constant.FILE_ZIP_STORAGE)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return download(zip, folder)
    }

    fun downloadRow(string: String): String {
        val folder =
            File(App.instance.filesDir.toString() + File.separator + Constant.FILE_ROW_STORAGE)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return download(string, folder)
    }

    fun getFileInStorage(zip: String, fileStorage: String): File {
        val strings: Array<String> =
            zip.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return getFile(
            strings,
            File(App.instance.filesDir.toString() + File.separator + fileStorage)
        )
    }


    private fun download(zip: String, folder: File): String {
        val strings: Array<String> =
            zip.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val file: File = getFile(strings, folder)
        return if (!file.exists()) {
            downloadFile(zip, file)
        } else {
            val fileSize = (file.length() / 1024).toString().toInt()
            if (fileSize == 0) {
                downloadFile(zip, file)
            } else {
                file.absolutePath
            }
        }
    }

    private fun downloadFile(zip: String, file: File) = try {
        val url = URL(zip)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000 // Optional: Set timeout
        connection.readTimeout = 5000

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int

                while ((inputStream.read(buffer).also { read = it }) != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            file.absolutePath
        } else {
            ""
        }
    } catch (e: Exception) {
        file.delete()
        ""
    }

    private fun getFile(strings: Array<String>, folder: File): File {
        val stringsName =
            strings[strings.size - 2].split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val name = stringsName[stringsName.size - 1]

        val file = File(folder.path + File.separator + name + ".webp")
        return file
    }

    private fun saveBitmapToLocal(bm: Bitmap, file: File): String {
        val path: String
        try {
            val fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            path = file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
        return path
    }

    fun savePreViewThemeCustom(fileName: String, bitmap: Bitmap): String {
        val folder =
            File(App.instance.filesDir.toString() + File.separator + Constant.FILE_PREVIEW_THEME_CUSTOM_STORAGE)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder.path + File.separator + fileName + ".webp")
        return saveBitmapToLocal(bitmap, file)
    }

    fun checkMusicExists(path: String): Boolean {
        val str = path.split(File.separator)
        val folder =
            File(App.instance.filesDir.toString() + File.separator + Constant.FILE_MUSIC)
        val fileSave = File(folder.path + File.separator + str[str.size - 1])
        return fileSave.exists()
    }

    fun saveImageToLocal(uriImg: Uri, context: Context): String {
        val folder =
            File(App.instance.filesDir.toString() + File.separator + Constant.FOLDER_FILE_IMAGE_LOCAL)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val fileName =
            getFileNameFromUri(uriImg, context) ?: "image_${System.currentTimeMillis()}.jpg"
        val file = File(folder, fileName)

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uriImg)
            inputStream?.use { input ->
                val outputStream = FileOutputStream(file)
                input.copyTo(outputStream)
                outputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun getFileNameFromUri(uri: Uri, context: Context): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val imgSrc = it.getString(index)
                it.close()
                return imgSrc
            }
        }
        return null
    }

}
