package com.example.galleryios18.data.models.template_item

import com.base.capva.model.AdjustFilter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Adjust {
    @SerializedName("brightness")
    @Expose
    var brightness: Float = AdjustFilter.DEFAULT_BRIGHTNESS

    @SerializedName("contrast")
    @Expose
    var contrast: Float = AdjustFilter.DEFAULT_CONTRAST

    @SerializedName("exposure")
    @Expose
    var exposure: Float = AdjustFilter.DEFAULT_EXPOSURE

    @SerializedName("brilliance")
    @Expose
    var brilliance: Float = AdjustFilter.DEFAULT_BRILLIANCE

    @SerializedName("highlight")
    @Expose
    var highlight: Float = AdjustFilter.DEFAULT_HIGHLIGHT

    @SerializedName("shadow")
    @Expose
    var shadow: Float = AdjustFilter.DEFAULT_SHADOW

    @SerializedName("saturation")
    @Expose
    var saturation: Float = AdjustFilter.DEFAULT_SATURATION

    @SerializedName("sharpness")
    @Expose
    var sharpness: Float = AdjustFilter.DEFAULT_SHARPNESS

    @SerializedName("tint")
    @Expose
    var tint: Float = AdjustFilter.DEFAULT_TINT

    @SerializedName("black_point")
    @Expose
    var blackPoint: Float = AdjustFilter.DEFAULT_BLACK_POINT

    @SerializedName("vibrance")
    @Expose
    var vibrance: Float = AdjustFilter.DEFAULT_VIBRANCE

    override fun toString(): String {
        return "Adjust(brightness=$brightness, contrast=$contrast, exposure=$exposure, brilliance=$brilliance, highlight=$highlight, shadow=$shadow, saturation=$saturation, sharpness=$sharpness, tint=$tint, blackPoint=$blackPoint, vibrance=$vibrance)"
    }


}