package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Shape {
    @SerializedName("type")
    @Expose
    var type: String = "NORMAL"

    @SerializedName("curve")
    @Expose
    var curve: Float = 0f

    @SerializedName("reverse")
    @Expose
    var reverse = false
}