package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Item {
    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("width")
    @Expose
    var width: Float = 0f

    @SerializedName("height")
    @Expose
    var height: Float = 0f

    @SerializedName("left")
    @Expose
    var left: Float = 0f

    @SerializedName("top")
    @Expose
    var top: Float = 0f

    @SerializedName("scale")
    @Expose
    var scale: Float = 1f

    @SerializedName("rotate")
    @Expose
    var rotate: Float = 0f

    @SerializedName("is_video")
    @Expose
    var isVideo = false

    @SerializedName("src")
    @Expose
    var src: String = ""

    @SerializedName("border")
    @Expose
    var border: String? = null

    @SerializedName("video_time_start")
    @Expose
    var videoTimeStart: Int = 0

    @SerializedName("video_time_end")
    @Expose
    var videoTimeEnd: Int = 0

    @SerializedName("filter")
    @Expose
    var filter = Filter()

    @SerializedName("adjust")
    @Expose
    var adjust = Adjust()

    @SerializedName("crop")
    @Expose
    var crop = Crop()

    @SerializedName("clip")
    @Expose
    var clip: String = ""

    @SerializedName("corner")
    @Expose
    var corner: Float = 0f

    @SerializedName("blur")
    @Expose
    var blur: Float = 0f

    @SerializedName("flip")
    @Expose
    var flip = Flip()

    @SerializedName("animate")
    @Expose
    var animate: String = "NONE"

    @SerializedName("text")
    @Expose
    var text: String = ""

    @SerializedName("font")
    @Expose
    var font: String = ""

    @SerializedName("text_color")
    @Expose
    var textColor: String = ""

    @SerializedName("format")
    @Expose
    var format = Format()

    @SerializedName("spacing")
    @Expose
    var spacing = Spacing()

    @SerializedName("effect")
    @Expose
    var effect = Effect()

    @SerializedName("transparent")
    @Expose
    var transparent: Float = 1f

    @SerializedName("folder_frame")
    @Expose
    var folderFrame = ""

    @SerializedName("disable_edit")
    @Expose
    var disableEdit = false

    @SerializedName("disable_edit_size")
    @Expose
    var disableEditSize = false

    override fun equals(other: Any?): Boolean {
        val item: Item = other as Item
        return item.animate == animate
    }
}