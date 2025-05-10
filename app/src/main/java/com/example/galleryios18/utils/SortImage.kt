package com.example.galleryios18.utils

import com.example.galleryios18.common.models.Media


class SortImage : Comparator<Media> {
    override fun compare(o1: Media, o2: Media): Int {
        return o2.dateModifier.compareTo(o1.dateModifier)
    }
}
