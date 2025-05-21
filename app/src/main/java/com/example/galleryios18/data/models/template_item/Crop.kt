package com.example.galleryios18.data.models.template_item

import android.graphics.RectF
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Crop {
    @SerializedName("left")
    @Expose
    var left: Float = 0f

    @SerializedName("top")
    @Expose
    var top: Float = 0f

    @SerializedName("right")
    @Expose
    var right: Float = 0f

    @SerializedName("bot")
    @Expose
    var bot: Float = 0f

    @Ignore
    fun isDefault(): Boolean {
        if (left == 0f && top == 0f && right == 0f && bot == 0f) {
            return true
        }
        return false
    }

    @Ignore
    fun getRect(): RectF {
        return RectF(left, top, right, bot)
    }

    override fun toString(): String {
        return "Crop(left=$left, top=$top, right=$right, bot=$bot)"
    }


}