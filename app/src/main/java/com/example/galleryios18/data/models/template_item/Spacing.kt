package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Spacing {
    @SerializedName("letter")
    @Expose
    var letter: Float = 0f

    @SerializedName("line")
    @Expose
    var line: Float = 0f

    @SerializedName("anchor")
    @Expose
    var anchor: String = "TOP"
}