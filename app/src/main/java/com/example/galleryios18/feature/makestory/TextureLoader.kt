package com.example.galleryios18.feature.makestory

import android.content.Context
import android.graphics.Bitmap

class TextureLoader(private val context: Context) {

    fun drawImage(bitmap: Bitmap) {
        // Tải bitmap thành texture
        // Vẽ texture full screen
    }

    fun drawFadeTransition(current: Bitmap, next: Bitmap, alpha: Float) {
        // Tạo 2 texture từ bitmap
        // Shader: mix(current, next, alpha)
        // Vẽ kết quả ra màn hình
    }

    // TODO: Bạn cần viết shader hoặc dùng GLProgram để load shader .glsl nếu cần
}