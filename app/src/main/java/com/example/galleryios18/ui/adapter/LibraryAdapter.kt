package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.R
import com.example.galleryios18.common.models.Image
import com.example.galleryios18.databinding.ItemMediaPickerBinding
import com.example.galleryios18.utils.GlideUtils

class LibraryAdapter(
    val listener: OnClickPickMediaListener,
    val context: Context,
    private val screenWidth: Int
) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {
    private var toast: Toast? = null

    private val images: MutableList<Image> = arrayListOf()
    private var pos = -1

    @SuppressLint("NotifyDataSetChanged")
    fun setData(images: List<Image>) {
        this.images.clear()
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    fun setCurrentChoose(src: String) {
        val index = images.indexOfFirst { it.path == src }
        if (index != -1) {
            pos = index
            notifyItemChanged(pos)
        } else {
            if (pos != -1) {
                val position = pos
                pos = -1
                notifyItemChanged(position)
            }
        }
    }

    fun showToast(context: Context, mess: String) {
        try {
            toast?.cancel()
            toast = Toast.makeText(
                context,
                mess,
                Toast.LENGTH_SHORT
            )
            toast?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewHolder(itemView: ItemMediaPickerBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {
            val image = images[position].path
            GlideUtils.loadImage(binding.ivThumb, image, screenWidth)
            binding.ivThumb.setOnClickListener {
                if (pos != position) {
                    if (pos != -1) {
                        notifyItemChanged(pos)
                    }
                    pos = position
                    if (GlideUtils.errorPath.contains(image)) {
                        showToast(
                            context,
                            context.getString(R.string.this_file_is_corrupted_please_choose_another_file)
                        )
                        listener.onErrClickImage()
                    } else {
                        listener.onClickImage(image)
                    }
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMediaPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (pos == position) holder.binding.ivBg.visibility = View.VISIBLE
        else holder.binding.ivBg.visibility = View.GONE
        holder.bindData(position)
    }


    interface OnClickPickMediaListener {
        fun onClickImage(string: String)
        fun onErrClickImage()
    }
}