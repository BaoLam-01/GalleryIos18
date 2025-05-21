package com.example.galleryios18.interfaces

interface OnSaveVideoListener {
    fun onSaveSuccess(path: ArrayList<String>)

    fun onSaveFailure(messError: String)

    fun onStartPreview()

    fun onStopPreview(end: Boolean)
    fun onProgress(process: Int)

    fun onProcessPreview(process: Int)
}