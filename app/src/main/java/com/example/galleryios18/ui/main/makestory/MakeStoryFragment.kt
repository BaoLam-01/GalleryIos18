package com.example.galleryios18.ui.main.makestory

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.Session
import com.example.galleryios18.R
import com.example.galleryios18.databinding.FragmentMakeStoryBinding
import com.example.galleryios18.ui.base.BaseBindingFragment
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

class MakeStoryFragment : BaseBindingFragment<FragmentMakeStoryBinding, MakeStoryViewModel>() {

    private lateinit var videoView: VideoView
    private val mediaUris = mutableListOf<Uri>()

    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            mediaUris.clear()
            mediaUris.addAll(uris)
            Toast.makeText(requireContext(), "Đã chọn ${uris.size} media", Toast.LENGTH_SHORT)
                .show()
        }
    }


    override fun getViewModel(): Class<MakeStoryViewModel> {
        return MakeStoryViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_make_story

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {

        videoView = binding.videoView

        binding.btnPickMedia.setOnClickListener {
            pickMediaLauncher.launch(arrayOf("image/*", "video/*"))
        }

        binding.btnCreateStory.setOnClickListener {
            createStoryFromMedia()
        }
    }

    override fun observerData() {
    }


    private fun createStoryFromMedia() {
        if (mediaUris.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Hãy chọn ít nhất 1 ảnh hoặc video",
                Toast.LENGTH_SHORT
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
                                tempDir,
                                if (ext in listOf(
                                        "jpg",
                                        "jpeg",
                                        "png"
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
                                    "-i", path,
                                    "-t", "3",
                                    "-vf", "scale=1080:1920",
                                    "-af", "volume=0.5",
                                    "-c:v", "libx264", "-preset", "ultrafast", "-crf", "23",
                                    "-threads", "2", "-pix_fmt", "yuv420p",
                                    "-loglevel", "error", "-y", outputFile.absolutePath
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
                    "-f", "concat", "-safe", "0", "-i", listFile.absolutePath,
                    "-i", musicPath,
                    "-filter_complex", "[0:a][1:a]amix=inputs=2:duration=shortest[aout]",
                    "-map", "0:v", "-map", "[aout]",
                    "-c:v", "libx264", "-crf", "23", "-c:a", "aac",
                    "-shortest", "-y", outputPath
                )
                val mediaList: List<String> = tempFiles.sortedBy { it.first }.map { it.second }

                val ffmpegCmd =
                    generateFFmpegConcatWithMusicCommand(
                        listFile.absolutePath,
                        musicPath,
                        outputPath
                    )


                Timber.e("LamPro | createStoryFromMedia - ffmpegCmd" + ffmpegCmd)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "🎬 Đang tạo video...", Toast.LENGTH_SHORT).show()
                }

                runFFmpegAsync(ffmpegCmd) { success ->
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "✅ Tạo video thành công!", Toast.LENGTH_SHORT)
                                .show()
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

    fun generateFFmpegConcatWithMusicCommand(
        listFilePath: String,
        musicPath: String,
        outputPath: String
    ): Array<String> {
        return arrayOf(
            "-f", "concat",
            "-safe", "0",
            "-i", listFilePath,
            "-i", musicPath,
            "-filter_complex", "[0:v][1:v]xfade=transition=fade:duration=1:offset=4[v]",
            "-map", "0:v",
            "-map", "[aout]",
            "-c:v", "libx264",
            "-crf", "23",
            "-c:a", "aac",
            "-shortest",
            "-y",
            outputPath
        )
    }

}