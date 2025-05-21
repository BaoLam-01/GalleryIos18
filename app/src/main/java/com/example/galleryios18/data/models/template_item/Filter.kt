package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Filter {
    @SerializedName("type")
    @Expose
    var type: String = "NONE"

    @SerializedName("intensity")
    @Expose
    var intensity: Float = 1f

    override fun toString(): String {
        return "Filter(type='$type', intensity=$intensity)"
    }


}