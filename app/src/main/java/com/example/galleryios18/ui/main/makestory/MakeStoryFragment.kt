package com.example.galleryios18.ui.main.makestory

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.arthenica.ffmpegkit.FFmpegKit
import com.base.capva.utils.ImageAnimate
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Media
import com.example.galleryios18.data.models.template_item.Item
import com.example.galleryios18.data.models.template_item.Template
import com.example.galleryios18.data.models.template_item.TemplateItem
import com.example.galleryios18.databinding.FragmentMakeStoryBinding
import com.example.galleryios18.feature.TemplateView
import com.example.galleryios18.interfaces.OnSaveVideoListener
import com.example.galleryios18.ui.adapter.TemplateViewPager
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.utils.CreateVideoManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MakeStoryFragment : BaseBindingFragment<FragmentMakeStoryBinding, MakeStoryViewModel>(),
    OnSaveVideoListener {

    private lateinit var videoView: VideoView
    private val mediaUris = mutableListOf<Uri>()
    private lateinit var pathAudio: String
    private var templateViewPager: TemplateViewPager? = null
    private var createVideoManager: CreateVideoManager? = null
    var listItemJson: List<String> = emptyList()
    private var listTemplateView = ArrayList<TemplateView>()


    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
    }

    fun getListMediaJson(context: Context, medias: List<Media>): List<String> {
        val listMedia = mutableListOf<String>()

        for (media in medias) {
            val item = Item()
            item.width = media.width.toFloat()
            item.height = media.height.toFloat()
            item.isVideo = !media.isImage
            item.src = media.path
            item.videoTimeStart = 0
            item.videoTimeEnd = media.duration.toInt()
            item.animate = ImageAnimate.PHOTO_ZOOM.toString()
            item.folderFrame = ""

            val itemString = Gson().toJson(item)
            listMedia.add(itemString)
        }
        return listMedia
    }


    override fun getViewModel(): Class<MakeStoryViewModel> {
        return MakeStoryViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_make_story

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setupLayout()
//        videoView = binding.videoView

        binding.btnCreateStory.setOnClickListener {
            saveVideo(pathAudio)
//            createStoryFromMedia()
        }

        mainViewModel.allMediaLiveData.observe(viewLifecycleOwner) {
            Timber.e("LamPro | onCreatedView - all media obsver : ${it.size}")
            if (it.isNotEmpty()) {

                val listMedia = mutableListOf<Media>()
                listMedia.add(it[0])
                listMedia.add(it[1])
                listMedia.add(it[2])
                Timber.e("LamPro | onCreatedView - list media: " + listMedia.size)
                listItemJson = getListMediaJson(requireContext(), listMedia)
                mainViewModel.listItemJsonLiveData.value = listItemJson
                initViewPager()
            }
        }
        mainViewModel.getAllMedia()
    }

    @SuppressLint("WrongConstant")
    private fun initViewPager() {
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen._16sdp) // ví dụ: 16dp
        val marginTransformer = MarginPageTransformer(pageMarginPx)
        binding.vpTemplate.setPageTransformer(marginTransformer)

        templateViewPager = TemplateViewPager(childFragmentManager, lifecycle, "")
        binding.container.post {
            binding.vpTemplate.requestLayout()
            templateViewPager!!.initList(listItemJson)
            Log.d("chungvv", "initViewPager: ${listItemJson.size}")
            Timber.e("LamPro | initViewPager - set list ")
            binding.vpTemplate.post {
                if (isAdded) {
                    binding.vpTemplate.adapter = templateViewPager
                }
            }
        }

        binding.vpTemplate.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }


    private fun setupLayout() {

        binding.container.post {
            val params = binding.cardView.layoutParams
            params.width = binding.container.width
            params.height = binding.container.height
            binding.cardView.requestLayout()
            val paramsView = binding.view.layoutParams

            paramsView.width = binding.container.width
            paramsView.height = binding.container.height

            binding.view.requestLayout()
            //todo: scale
            scaleCardView()
        }
    }

    private fun scaleCardView() {
        binding.view.post {
            val scale = binding.view.width.toFloat() / binding.cardView.width.toFloat()
            binding.cardView.scaleX = scale
            binding.cardView.scaleY = scale
//            binding.cardView.visibility = View.VISIBLE
            binding.cardView.postDelayed({
                if (isAdded) {
                    initCreateVideoManager()
                }
            }, 300)
        }
    }


    private fun initCreateVideoManager() {
        if (createVideoManager == null) {
//            binding.cardView.visibility = View.VISIBLE
            Timber.e("LamPro | initCreateVideoManager - width: ${binding.cardView.width}")
            Timber.e("LamPro | initCreateVideoManager - height: ${binding.cardView.height}")
            binding.cardView.post {
                createVideoManager = CreateVideoManager(
                    requireActivity(),
                    binding.cardView,
                    listTemplateView,
                    1080,
                    1920,
                    10000,
                    this
                )
                pathAudio = copyAssetToExternal(requireContext(), "bg_music.mp3", "bg_music.mp3")
                createVideoManager?.setupAudioPreview(
                    pathAudio, 0, 10000
                )
            }
        }
    }

    private fun setupTemplate() {
        for (i in 0 until listItemJson.size) {
            val templateView = TemplateView(context)
            templateView.setOnInitTemplateListener {
            }
            templateView.setOnPreloadFrameListener {
            }
            binding.cardView.addView(
                templateView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
            listTemplateView.add(templateView)

            templateView.post {
                val items = Gson().fromJson(listItemJson[i], Item::class.java)
                templateView.setupTemplate(items, true)
                if (i == 0) {
                    templateView.visibility = View.VISIBLE
                } else {
                    templateView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun saveVideo(pathAudio: String) {
        setupTemplate()
        createVideoManager?.let {
            it.setupAudio(
                pathAudio,
                0,
                10000,
                10000
            )
            it.setupSave(
                10000,
                listTemplateView
            )
            it.startEncoding()
        }
    }

    override fun observerData() {
    }


    private fun createStoryFromMedia() {
        if (mediaUris.isEmpty()) {
            Toast.makeText(
                requireContext(), "Hãy chọn ít nhất 1 ảnh hoặc video", Toast.LENGTH_SHORT
            ).show()
            return
        }

        val context = requireContext()
        val tempDir = context.cacheDir
        val tempFiles = mutableListOf<Pair<Int, String>>()

        lifecycleScope.launch {
            withContext(Dispatchers.Default) {

                val jobs = mediaUris.mapIndexed { index, uri ->
                    async {
                        val path = getPathFromUri(uri)
                        if (path != null) {
                            val ext = path.substringAfterLast(".").lowercase()
                            val outputFile = File(
                                tempDir, if (ext in listOf(
                                        "jpg", "jpeg", "png"
                                    )
                                ) "img_$index.mp4" else "vid_$index.mp4"
                            )

                            val cmd = when (ext) {
                                "jpg", "jpeg", "png" -> arrayOf(
                                    "-loop",
                                    "1",
                                    "-t",
                                    "3",
                                    "-i",
                                    path,
                                    "-f",
                                    "lavfi",
                                    "-t",
                                    "3",
                                    "-i",
                                    "anullsrc=channel_layout=stereo:sample_rate=44100",
                                    "-vf",
                                    "scale=1080:1920",
                                    "-c:v",
                                    "libx264",
                                    "-preset",
                                    "ultrafast",
                                    "-crf",
                                    "23",
                                    "-c:a",
                                    "aac",
                                    "-shortest",
                                    "-pix_fmt",
                                    "yuv420p",
                                    "-threads",
                                    "2",
                                    "-loglevel",
                                    "error",
                                    "-y",
                                    outputFile.absolutePath
                                )

                                "mp4", "mov", "mkv" -> arrayOf(
                                    "-i",
                                    path,
                                    "-t",
                                    "3",
                                    "-vf",
                                    "scale=1080:1920",
                                    "-af",
                                    "volume=0.5",
                                    "-c:v",
                                    "libx264",
                                    "-preset",
                                    "ultrafast",
                                    "-crf",
                                    "23",
                                    "-threads",
                                    "2",
                                    "-pix_fmt",
                                    "yuv420p",
                                    "-loglevel",
                                    "error",
                                    "-y",
                                    outputFile.absolutePath
                                )

                                else -> null
                            }

                            if (cmd != null && runFFmpegCommand(cmd)) {
                                synchronized(tempFiles) {
                                    tempFiles.add(index to outputFile.absolutePath)
                                }
                            }
                        }
                    }
                }
                jobs.awaitAll()

                if (tempFiles.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Không xử lý được media", Toast.LENGTH_SHORT).show()
                    }
                    return@withContext
                }

                // Tạo file list concat
                val listFile = File(tempDir, "list.txt")
                val listText =
                    tempFiles.sortedBy { it.first }.joinToString("\n") { "file '${it.second}'" }
                listFile.writeText(listText)

                val outputPath =
                    "${context.getExternalFilesDir(null)?.absolutePath}/story_output.mp4"
                val musicPath = copyAssetToExternal(context, "bg_music.mp3", "bg_music.mp3")

                val cmdConcat = arrayOf(
                    "-f",
                    "concat",
                    "-safe",
                    "0",
                    "-i",
                    listFile.absolutePath,
                    "-i",
                    musicPath,
                    "-filter_complex",
                    "[0:a][1:a]amix=inputs=2:duration=shortest[aout]",
                    "-map",
                    "0:v",
                    "-map",
                    "[aout]",
                    "-c:v",
                    "libx264",
                    "-crf",
                    "23",
                    "-c:a",
                    "aac",
                    "-shortest",
                    "-y",
                    outputPath
                )
                val mediaList: List<String> = tempFiles.sortedBy { it.first }.map { it.second }

                val ffmpegCmd = generateXfadeWithMusicCommand(
                    mediaList, musicPath, outputPath
                )


                Timber.e("LamPro | createStoryFromMedia - ffmpegCmd" + ffmpegCmd.joinToString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "🎬 Đang tạo video...", Toast.LENGTH_SHORT).show()
                }

                runFFmpegAsync(ffmpegCmd) { success ->
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "✅ Tạo video thành công!", Toast.LENGTH_SHORT)
                                .show()
                            Timber.e("LamPro | createStoryFromMedia - output path: $outputPath")
                            videoView.setOnErrorListener { mp, what, extra ->
                                Toast.makeText(context, "Không phát được video", Toast.LENGTH_SHORT)
                                    .show()
                                true
                            }
                            videoView.setVideoURI(Uri.parse(outputPath))
                            videoView.start()
                        } else {
                            Toast.makeText(context, "❌ Lỗi khi tạo video", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun runFFmpegAsync(cmd: Array<String>, onDone: suspend (Boolean) -> Unit) {
        FFmpegKit.executeAsync(cmd.joinToString(" ")) { session ->
            val success = session.returnCode.isValueSuccess
            GlobalScope.launch(Dispatchers.Main) {
                onDone(success)
            }
        }
    }

    private suspend fun runFFmpegCommand(cmd: Array<String>): Boolean =
        suspendCancellableCoroutine { cont ->
            FFmpegKit.executeAsync(cmd.joinToString(" ")) { session ->
                val returnCode = session.returnCode
                cont.resume(returnCode.isValueSuccess) {

                }
            }
        }

    private fun getPathFromUri(uri: Uri): String? {
        return try {
            val extension = getExtensionFromUri(uri)
            val file = File(requireContext().cacheDir, UUID.randomUUID().toString() + extension)

            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getExtensionFromUri(uri: Uri): String {
        val type = requireContext().contentResolver.getType(uri) ?: return ""
        return when {
            type.contains("jpeg") -> ".jpg"
            type.contains("png") -> ".png"
            type.contains("mp4") -> ".mp4"
            else -> ""
        }
    }

    fun copyAssetToExternal(context: Context, assetName: String, outputFileName: String): String {
        val outFile = File(context.getExternalFilesDir(null), outputFileName)
        if (!outFile.exists()) {
            context.assets.open(assetName).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        return outFile.absolutePath
    }

    fun generateXfadeWithMusicCommand(
        videoPaths: List<String>,
        musicPath: String,
        outputPath: String,
        transitionDuration: Int = 1,
        eachVideoDuration: Int = 5,
    ): Array<String> {
        val inputs = mutableListOf<String>()
        videoPaths.forEach { inputs += listOf("-i", it) }
        inputs += listOf("-i", musicPath)

        val filterParts = mutableListOf<String>()
        val videoLabels = videoPaths.indices.map { "v$it" }
        val trimmedLabels = videoPaths.indices.map { "t$it" }

        // Tách các video ra, cắt độ dài mỗi clip (nếu cần), scale về cùng kích thước
        videoPaths.indices.forEach { i ->
            filterParts += "[$i:v]trim=duration=${eachVideoDuration},setpts=PTS-STARTPTS,scale=1280:720[${videoLabels[i]}]"
        }

        // Áp dụng xfade lần lượt
        var count = 0
        var xfadeOffset = eachVideoDuration
        var prevLabel = videoLabels[0]
        for (i in 1 until videoPaths.size) {
            val nextLabel = videoLabels[i]
            val outLabel = "x$count"
            filterParts += "[$prevLabel][$nextLabel]xfade=transition=fade:duration=$transitionDuration:offset=$xfadeOffset[$outLabel]"
            prevLabel = outLabel
            xfadeOffset += eachVideoDuration - transitionDuration
            count++
        }

        // Thêm nhạc
        filterParts += "[${videoPaths.size}:a]anull[aout]"

        val filterComplex = filterParts.joinToString(";")

        val command = mutableListOf<String>()
        command.addAll(inputs)
        command += listOf(
            "-filter_complex",
            filterComplex,
            "-map",
            "[x${count - 1}]",
            "-map",
            "[aout]",
            "-c:v",
            "libx264",
            "-crf",
            "23",
            "-preset",
            "medium",
            "-c:a",
            "aac",
            "-shortest",
            "-y",
            outputPath
        )

        return command.toTypedArray()
    }

    override fun onSaveSuccess(path: ArrayList<String>) {
        Timber.e("LamPro | onSaveSuccess - on save success")
    }

    override fun onSaveFailure(messError: String) {
        Timber.e("LamPro | onSaveFailure - on save failure")
    }

    override fun onStartPreview() {
        Timber.e("LamPro | onStartPreview - on start preview")
    }

    override fun onStopPreview(end: Boolean) {
        Timber.e("LamPro | onStopPreview - on stop preview")
    }

    override fun onProgress(process: Int) {
        Timber.e("LamPro | onProgress - on progress")
    }

    override fun onProcessPreview(process: Int) {
        Timber.e("LamPro | onProcessPreview - on process preview")
    }

}