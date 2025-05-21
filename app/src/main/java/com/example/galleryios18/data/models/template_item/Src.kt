package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Src {
    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("src")
    @Expose
    var src: String = ""
}