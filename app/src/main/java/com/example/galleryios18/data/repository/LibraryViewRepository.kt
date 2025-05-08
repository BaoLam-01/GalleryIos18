package com.example.galleryios18.data.repository

import com.example.galleryios18.App
import com.example.galleryios18.common.models.Image
import com.example.galleryios18.utils.LibsManagerUtils
import javax.inject.Inject

class LibraryViewRepository @Inject constructor(){

    fun getListLibs(): List<Image> {
        return LibsManagerUtils.getListImages(App.instance)
    }
}