package com.example.galleryios18.data.models.template_item

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Background {
    @SerializedName("src")
    @Expose
    var src = Src()

    @SerializedName("duration")
    @Expose
    var duration: Int = 3000

    @SerializedName("filter")
    @Expose
    var filter = Filter()

    @SerializedName("adjust")
    @Expose
    var adjust = Adjust()

    @SerializedName("crop")
    @Expose
    var crop = Crop()

    @SerializedName("flip")
    @Expose
    var flip = Flip()

    @SerializedName("transparency")
    @Expose
    var transparency: Float = 1f

    @SerializedName("animate")
    @Expose
    var animate = ""

    override fun toString(): String {
        return "Background(src=$src, duration=$duration, filter=$filter, adjust=$adjust, crop=$crop, flip=$flip, transparency=$transparency)"
    }


}