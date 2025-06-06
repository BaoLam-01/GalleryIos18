package com.filter.base

import android.graphics.SurfaceTexture


/**
 * 渲染器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 * @Datetime 2019-10-12 10:04
 *
 */
interface IDrawer {
    fun setVideoSize(videoW: Int, videoH: Int)
    fun setWorldSize(worldW: Int, worldH: Int)
    fun getSurfaceTexture(cb: (st: SurfaceTexture)->Unit) {}
    fun release()
}