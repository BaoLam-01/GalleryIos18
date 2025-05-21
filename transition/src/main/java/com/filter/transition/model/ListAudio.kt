package com.filter.transition.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListAudio {
    @SerializedName("Items")
    @Expose
    var listAudio : List<Audio>? = null
}