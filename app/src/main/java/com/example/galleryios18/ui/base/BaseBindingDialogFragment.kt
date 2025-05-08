package com.example.galleryios18.ui.base

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.galleryios18.R
import com.example.galleryios18.ui.main.MainViewModel


abstract class BaseBindingDialogFragment<B : ViewDataBinding> :
    BaseDialogFragment() {
    lateinit var binding: B
    abstract val layoutId: Int
    lateinit var mainViewModel: MainViewModel
    private var loaded = false
    private var lastClickTime: Long = 0
    private var toast: Toast? = null

    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!loaded)
            binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFullScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        if (!needToKeepView()) {
            onCreatedView(view, savedInstanceState)
        } else {
            if (!loaded) {
                onCreatedView(view, savedInstanceState)
                loaded = true
            }
        }
    }

    open fun needToKeepView(): Boolean {
        return false
    }

    open fun checkClick(timeCheck: Int = 600): Boolean {
        if (SystemClock.elapsedRealtime() - lastClickTime < timeCheck) {
            return false
        }
        lastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    open fun updateWhenBecomePro(isPro: Boolean) {}

    protected open fun nextAfterReward() {
    }

    open fun showToast(message: String) {
        try {
            toast?.cancel()
            toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            toast!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
