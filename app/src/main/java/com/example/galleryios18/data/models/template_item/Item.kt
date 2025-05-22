package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Item {

    @SerializedName("width")
    @Expose
    var width: Float = 0f

    @SerializedName("height")
    @Expose
    var height: Float = 0f


    @SerializedName("is_video")
    @Expose
    var isVideo = false

    @SerializedName("src")
    @Expose
    var src: String = ""


    @SerializedName("video_time_start")
    @Expose
    var videoTimeStart: Int = 0

    @SerializedName("video_time_end")
    @Expose
    var videoTimeEnd: Int = 0

    @SerializedName("animate")
    @Expose
    var animate: String = "NONE"

    @SerializedName("folder_frame")
    @Expose
    var folderFrame = ""


    override fun equals(other: Any?): Boolean {
        val item: Item = other as Item
        return item.animate == animate
    }
}