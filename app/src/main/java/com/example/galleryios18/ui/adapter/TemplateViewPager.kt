package com.example.galleryios18.ui.adapter

import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.galleryios18.ui.main.itemtemplate.ItemTemplateFragment
import timber.log.Timber

class TemplateViewPager(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    var pathImage: String,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    fun initList(templates: List<String>) {
        list.clear()
        if (templates.isNotEmpty()) {
            Timber.e("LamPro | initList - template size > 0")
            for (i in 0 until templates.size) {
                list.add(
                    ItemTemplateFragment.newIns(
                        pathImage,
                        i
                    )
                )
            }
        } else {
            Timber.e("LamPro | initList - template size < 0")

            list.add(
                ItemTemplateFragment.newIns(
                    pathImage,
                    0
                )
            )
        }
    }

    private val list: ArrayList<ItemTemplateFragment> = ArrayList()
    override fun getItemCount(): Int {
        return list.size
    }


    fun getListItemDetailFragment(): ArrayList<ItemTemplateFragment> {
        return list
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("chungvv", "createFragment: $position")
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return list.find { it.hashCode().toLong() == itemId } != null
    }


    fun getTemplateView(position: Int): View? {
        return list[position].getTemplateView()
    }

    fun setDuration(position: Int, duration: Int) {
        list[position].setDuration(duration)
    }

    fun getDuration(position: Int): Int {
//        return list[position].getDuration()
        return list[position].getDurationTemplate()
    }

}