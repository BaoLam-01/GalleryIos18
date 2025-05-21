package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TemplateItem {
    @SerializedName("width")
    @Expose
    var width = 1080

    @SerializedName("height")
    @Expose
    var height = 1080

    @SerializedName("background")
    @Expose
    var background = Background()

    @SerializedName("item")
    @Expose
    var item = ArrayList<Item>()
}