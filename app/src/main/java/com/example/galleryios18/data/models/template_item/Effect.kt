package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Effect {
    @SerializedName("style")
    @Expose
    var style = Style()

    @SerializedName("shape")
    @Expose
    var shape = Shape()
}