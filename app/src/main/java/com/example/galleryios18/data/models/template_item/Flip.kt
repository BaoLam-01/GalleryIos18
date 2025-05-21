package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Flip {
    @SerializedName("h")
    @Expose
    var h: Boolean = false

    @SerializedName("v")
    @Expose
    var v: Boolean = false

    override fun toString(): String {
        return "Flip(h=$h, v=$v)"
    }


}