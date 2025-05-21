package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Options {
    @SerializedName("offset")
    @Expose
    var offset: Float = 50f

    @SerializedName("direction")
    @Expose
    var direction: Float = 90f

    @SerializedName("blur")
    @Expose
    var blur: Float = 0f

    @SerializedName("transparent")
    @Expose
    var transparent: Float = 1f

    @SerializedName("color")
    @Expose
    var color: String = ""

    @SerializedName("intensity")
    @Expose
    var intensity: Float = 100f

    @SerializedName("thickness")
    @Expose
    var thickness: Float = 100f

    @SerializedName("color_glitch")
    @Expose
    var colorGlitch: Int = -1

    @SerializedName("roundness")
    @Expose
    var roundness: Float = 50f

    @SerializedName("spread")
    @Expose
    var spread: Float = 50f
}