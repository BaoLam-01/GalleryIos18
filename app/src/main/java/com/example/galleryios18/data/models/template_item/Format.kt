package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Format {
    @SerializedName("bold")
    @Expose
    var bold: Boolean = false

    @SerializedName("italic")
    @Expose
    var italic: Boolean = false

    @SerializedName("under_line")
    @Expose
    var underLine: Boolean = false

    @SerializedName("all_cap")
    @Expose
    var allCap: Boolean = false

    @SerializedName("gravity")
    @Expose
    var gravity: String = "START"

    @SerializedName("multilevel_list")
    @Expose
    var multilevelList: String? = "NONE"
}