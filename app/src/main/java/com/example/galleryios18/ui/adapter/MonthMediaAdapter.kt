package com.example.galleryios18.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryios18.data.models.ItemForMonth
import com.example.galleryios18.data.models.ItemThumbInMonth
import com.example.galleryios18.databinding.ItemMonthMediaBinding
import com.example.galleryios18.ui.custom.SpaceItemDecorator
import com.example.galleryios18.ui.custom.SpanSize
import com.example.galleryios18.ui.custom.SpannedGridLayoutManager
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max


class MonthMediaAdapter : RecyclerView.Adapter<MonthMediaAdapter.MonthMediaViewHolder>() {
    private lateinit var thumbInMonthAdapter: ThumbInMonthAdapter
    private val mDiffCallback = object : DiffUtil.ItemCallback<ItemForMonth>() {
        override fun areItemsTheSame(
            oldItem: ItemForMonth, newItem: ItemForMonth
        ): Boolean {
            return oldItem.year == newItem.year
        }

        override fun areContentsTheSame(
            oldItem: ItemForMonth, newItem: ItemForMonth
        ): Boolean {
            return oldItem.year == newItem.year
        }

    }
    private val mDiffer: AsyncListDiffer<ItemForMonth> = AsyncListDiffer(this, mDiffCallback)

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listMedia: List<ItemForMonth>) {
        this.mDiffer.submitList(listMedia)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MonthMediaViewHolder {
        val binding: ItemMonthMediaBinding =
            ItemMonthMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonthMediaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MonthMediaViewHolder, position: Int
    ) {
        holder.bindData(mDiffer.currentList[position], position)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    inner class MonthMediaViewHolder(private val binding: ItemMonthMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
        fun bindData(itemForMonth: ItemForMonth, position: Int) {
            Timber.e("LamPro | bindData - bind month media")
            val textYear = formatter.format(itemForMonth.year)
            binding.tvYear.text = textYear

            thumbInMonthAdapter = ThumbInMonthAdapter()
            thumbInMonthAdapter.setData(mDiffer.currentList[position].listItemThumbInMonth)

            binding.rvMedia.post {
                initSizeRecyclerView(
                    binding.rvMedia,
                    binding.rvMedia.width,
                    mDiffer.currentList[position].listItemThumbInMonth
                )
            }

            val spannedGridLayoutManager =
                SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 6)
            binding.rvMedia.setLayoutManager(spannedGridLayoutManager)

            binding.rvMedia.setAdapter(thumbInMonthAdapter)
            spannedGridLayoutManager.spanSizeLookup =
                object : SpannedGridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): SpanSize {
                        val itemThumbInMonth = itemForMonth.listItemThumbInMonth[position]
                        return SpanSize(itemThumbInMonth.ratioWidth, itemThumbInMonth.ratioHeight)
                    }

                }
            binding.rvMedia.addItemDecoration(SpaceItemDecorator(10, 10, 10, 10))
        }
    }

    fun initSizeRecyclerView(
        recyclerView: RecyclerView,
        width: Int,
        list: List<ItemThumbInMonth>,
    ) {
        var widthRcl = width

        //        heightRcl = widthRcl / 16 * ratioH + padding;
        var totalHeight = 0
        val spanCount = 6 // Số lượng cột của GridLayoutManager
        val totalWidth = widthRcl // Tổng chiều rộng của RecyclerView
        val columnWidth = totalWidth / spanCount // Chiều rộng của mỗi cột

        var spanRemaining = spanCount // Số lượng cột còn lại trong hàng hiện tại
        var maxHeightInRow = 0 // Chiều cao lớn nhất trong hàng hiện tại

        for (i in list.indices) {

            // Lấy chiều cao item thứ i theo tỷ lệ (giả sử chiều cao của item phụ thuộc vào chiều rộng của nó)

            val itemHeight =
                (columnWidth * list[i].ratioHeight) // Tính chiều cao item theo tỷ lệ width:height

            // Cập nhật chiều cao lớn nhất trong hàng
            maxHeightInRow = max(maxHeightInRow.toDouble(), itemHeight.toDouble()).toInt()

            // Trừ đi số cột item này chiếm
            spanRemaining -= list[i].ratioWidth

            // Khi spanRemaining <= 0, nghĩa là hàng đã đầy và chúng ta cần chuyển sang hàng mới
            if (spanRemaining <= 0) {
                // Cộng chiều cao của hàng hiện tại vào tổng chiều cao
                totalHeight += maxHeightInRow
                // Reset các thông số cho hàng mới
                spanRemaining = spanCount // Reset lại số lượng cột còn lại cho hàng mới
                maxHeightInRow = 0 // Reset chiều cao lớn nhất của hàng mới
            }
        }

//        // Nếu còn các item trong hàng mà chưa cộng chiều cao
//        if (spanRemaining < spanCount) {
//            totalHeight += maxHeightInRow
//        }

        Timber.e("LamPro | initSizeRecyclerView - total height: $totalHeight| width: $width")

        val layoutParams: LayoutParams = recyclerView.layoutParams as LayoutParams
        layoutParams.height = totalHeight

        recyclerView.setLayoutParams(layoutParams)
    }

}