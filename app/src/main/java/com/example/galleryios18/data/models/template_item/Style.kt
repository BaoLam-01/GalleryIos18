package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Style {
    @SerializedName("type")
    @Expose
    var type: String = "NONE"

    @SerializedName("options")
    @Expose
    var options = Options()
}