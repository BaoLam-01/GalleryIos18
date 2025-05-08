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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.ui.dialog.RateDialog
import com.example.galleryios18.ui.main.MainViewModel
import com.example.galleryios18.utils.PermissionUtils


abstract class BaseBindingFragment<B : ViewDataBinding, T : BaseViewModel> :
    BaseFragment() {
    private var toast: Toast? = null
    private var lastClickTime: Long = 0
    lateinit var binding: B
    lateinit var viewModel: T
    lateinit var mainViewModel: MainViewModel
    private var loaded = false
    protected var isPro = true
    private var mRateDialog: RateDialog? = null
    protected abstract fun getViewModel(): Class<T>
    abstract val layoutId: Int

    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    protected abstract fun observerData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (!loaded)
            binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (needInsetTop()) {
            var insectTop = 0
            if (requireActivity() is BaseActivity) {
                insectTop = (requireActivity() as BaseActivity).statusBarHeight
            }
            binding.root.setPadding(
                binding.root.paddingLeft, insectTop, binding.root.paddingRight,
                binding.root.paddingBottom
            )
        }
        viewModel = ViewModelProvider(this)[getViewModel()]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val isProChanged = isPro
        if (!needToKeepView()) {
            onCreatedView(view, savedInstanceState)
        } else {
            if (!loaded) {
                onCreatedView(view, savedInstanceState)
                loaded = true
            } else if (isProChanged) {
                updateWhenBecomePro(isPro)
            }
        }
        observerData()
    }

    open fun needInsetTop(): Boolean {
        return false
    }

    open fun needToKeepView(): Boolean {
        return false
    }

    fun popBackStack() {
        findNavController().popBackStack()
    }

    fun popBackStack(id: Int, isInclusive: Boolean) {
        findNavController().popBackStack(id, isInclusive)
    }

    fun navigateScreen(bundle: Bundle?, id: Int, idFragmentExitCurrentScreen: Int? = null) {
        val currentDestination = findNavController().currentDestination?.id
        if (currentDestination == id) {
            return
        }

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
        idFragmentExitCurrentScreen?.let {
            navBuilder.setPopUpTo(it, true)
        }
        findNavController().navigate(id, bundle, navBuilder.build())
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

    open fun showToastSuccessApply() {
        val permissionOverlay = PermissionUtils.checkHasPermissionOverlay(requireContext())
        val backgroundPermission = PermissionUtils.checkBackgroundPermission(requireContext())
        val isSuccess = !(!permissionOverlay || !backgroundPermission)
        if (isSuccess) {
            showToast(getString(R.string.set_successfully))
        }
    }

    open fun checkClick(timeCheck: Int = 600): Boolean {
        if (SystemClock.elapsedRealtime() - lastClickTime < timeCheck) {
            return false
        }
        lastClickTime = SystemClock.elapsedRealtime()
        return true
    }


    override fun onStart() {
        super.onStart()
        lastClickTime = 0
    }

    open fun updateWhenBecomePro(isPro: Boolean) {}
    open fun finishRate() {}


    protected open fun showRateApp() {
        if (!mPrefHelper.getBoolean(Constant.KEY_IS_RATE, false) && isAdded) {
            if (mRateDialog == null) {
                mRateDialog = RateDialog(
                    requireActivity()
                )
                mRateDialog!!.setListenerRate(rateListener)
            }
            mRateDialog!!.show()
        }
    }

    fun showProDialog() {
    }

    private var rateListener: RateDialog.ListenerRate = object : RateDialog.ListenerRate {
        override fun rateFiveStar() {
            mPrefHelper.storeBoolean(Constant.KEY_IS_RATE, true)
            finishRate()
        }

        override fun close() {
        }
    }

    protected open fun dismissRate() {
        if (mRateDialog != null && mRateDialog!!.isShowing) {
            mRateDialog!!.dismiss()
        }
    }

}