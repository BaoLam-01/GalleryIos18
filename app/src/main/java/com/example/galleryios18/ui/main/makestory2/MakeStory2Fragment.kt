package com.example.galleryios18.ui.main.makestory2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentMakeStory2Binding
import com.example.galleryios18.feature.makestory.EglHelper
import com.example.galleryios18.feature.makestory.MediaDecoder
import com.example.galleryios18.feature.makestory.MediaEncoder
import com.example.galleryios18.feature.makestory.TextureRenderer
import com.example.galleryios18.ui.base.BaseBindingFragment
import timber.log.Timber
import java.io.File

class MakeStory2Fragment : BaseBindingFragment<FragmentMakeStory2Binding, MakeStory2ViewModel>() {
    override fun getViewModel(): Class<MakeStory2ViewModel> {
        return MakeStory2ViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_make_story_2

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        val mediaList = mutableListOf<String>()
        mainViewModel.allMediaLiveData.observe (viewLifecycleOwner){
            Timber.e("LamPro - path 1: ${mediaList.add(it[0].path)}")
            mediaList.add(it[0].path)
            mediaList.add(it[1].path)
            mediaList.add(it[2].path)
            createVideo(mediaList)

        }
        mainViewModel.getAllMedia()
    }

    private fun createVideo(mediaList: MutableList<String>) {
        val bitmaps = mutableListOf<Bitmap>()
        for (media in mediaList) {
            if (media.endsWith(".mp4")) {
                val decoder = MediaDecoder(requireContext(), media)
                bitmaps.addAll(decoder.decodeFrames())
            } else {
                val bitmap = BitmapFactory.decodeFile(media)
                bitmaps.add(bitmap)
            }
        }
        val renderer = TextureRenderer(requireContext())
        val outputBitmaps = mutableListOf<Bitmap>()
        for (i in 0 until bitmaps.size - 1) {
            val start = bitmaps[i]
            val end = bitmaps[i + 1]
            for (j in 0..30) {
                val alpha = j / 30f
                val frame = renderer.drawToBitmap(start, end, alpha, 1280, 720)
                outputBitmaps.add(frame)
            }
        }

        val outputFile = File(requireContext().getExternalFilesDir(null), "output.mp4")
        val encoder = MediaEncoder(outputFile.absolutePath, 1280, 720)
        Timber.e("LamPro - outputFile: " + outputFile)
        Timber.e("LamPro - output bitmap: " + outputBitmaps.size)
        encoder.encode(outputBitmaps)
    }

    override fun observerData() {
    }
}