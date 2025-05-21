package com.example.galleryios18.data.models.template_item

import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Template {

    @SerializedName("id_template")
    @Expose
    var idTemplate = 0

    @SerializedName("id_category")
    @Expose
    var idCategory = 0

    @SerializedName("category_name")
    @Expose
    var nameCategory = ""

    @SerializedName("template_name")
    @Expose
    var templateName = ""

    @SerializedName("link_template_thumb")
    @Expose
    var thumb = ""

    @SerializedName("ratio")
    @Expose
    var ratio = ""

    @SerializedName("json_template")
    @Expose
    var strJson = ""

    @SerializedName("tag")
    @Expose
    var tag = ""

    @SerializedName("link_template")
    @Expose
    var linkTemplate = ""

    @SerializedName("sort_key")
    @Expose
    var sortKey = 0

    @SerializedName("update_version")
    @Expose
    var updateVersion = 0

    @Ignore
    var templateItem = TemplateItem()

    constructor()
    constructor(
        idTemplate: Int,
        idCategory: Int,
        nameCategory: String,
        templateName: String,
        thumb: String,
        ratio: String,
        strJson: String
    ) {
        this.idTemplate = idTemplate
        this.idCategory = idCategory
        this.nameCategory = nameCategory
        this.templateName = templateName
        this.thumb = thumb
        this.ratio = ratio
        this.strJson = strJson
    }

    constructor(i: Int) {
        this.idTemplate = i
    }

    fun getValueRatio(): Float {
        val ratio = ratio.split(":")
        val width = ratio[0].trim().toFloat()
        val height = ratio[1].trim().toFloat()
        return height / width
    }

    fun getSizeTemplate(): IntArray {
        val v = intArrayOf(0, 0)
        val ratio = ratio.split(":")
        v[0] = ratio[0].trim().toInt()
        v[1] = ratio[1].trim().toInt()
        return v
    }

    override fun equals(other: Any?): Boolean {
        val item: Template = other as Template
        return item.idTemplate == idTemplate
    }
}