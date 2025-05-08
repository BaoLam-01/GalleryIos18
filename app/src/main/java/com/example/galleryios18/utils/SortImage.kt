package com.example.galleryios18.utils

import com.example.galleryios18.common.models.Image


class SortImage : Comparator<Image> {
    override fun compare(o1: Image, o2: Image): Int {
        return o2.dateModifier.compareTo(o1.dateModifier)
    }
}
