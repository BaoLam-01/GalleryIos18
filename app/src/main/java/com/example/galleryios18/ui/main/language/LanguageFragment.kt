package com.example.galleryios18.ui.main.language

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.galleryios18.R
import com.example.galleryios18.common.Constant
import com.example.galleryios18.common.models.Language
import com.example.galleryios18.common.models.ListLanguage
import com.example.galleryios18.databinding.FragmentLanguageBinding
import com.example.galleryios18.service.AppService
import com.example.galleryios18.ui.adapter.LanguageAdapter
import com.example.galleryios18.ui.base.BaseBindingFragment
import com.example.galleryios18.utils.LocaleUtils
import com.example.galleryios18.utils.ServiceUtils

class LanguageFragment : BaseBindingFragment<FragmentLanguageBinding, LanguageViewModel>() {

    private var languageAdapter: LanguageAdapter? = null
    private var language: Language? = null
    private var currentPos = 0

    override fun getViewModel(): Class<LanguageViewModel> {
        return LanguageViewModel::class.java
    }

    override fun needInsetTop(): Boolean {
        return true
    }

    override val layoutId: Int
        get() = R.layout.fragment_language

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        binding.layoutHeader.tvTitle.text = getString(R.string.languages)
        binding.layoutHeader.tvTitle.visibility = View.VISIBLE
        getData()
        onClick()
    }

    override fun observerData() {
        viewModel.listLanguage.observe(viewLifecycleOwner) { list: ListLanguage? ->
            if (languageAdapter == null && list != null) {
                currentPos = list.posLanguageSelected
                languageAdapter = LanguageAdapter(
                    list,
                    object : LanguageAdapter.LanguageListener {
                        override fun onClick(position: Int, language: Language) {
                            this@LanguageFragment.language =
                                if (position != currentPos) {
                                    language
                                } else {
                                    null
                                }
                            languageAdapter!!.setPosSelect(position)
                        }
                    })
                binding.rvData.adapter = languageAdapter
                binding.rvData.post {
                    if (isAdded) {
                        binding.rvData.scrollToPosition(currentPos)
                    }
                }
            }
        }
    }

    private fun onClick() {
        binding.layoutHeader.tvApply.visibility = View.GONE
        binding.ivChoose.setOnClickListener {
            languageAdapter?.apply {
                if (language == null) {
                    language = getSelectedLanguage()
                }
                val context = requireActivity()
                LocaleUtils.applyLocaleAndRestart(
                    context,
                    language?.codeLocale
                )
                val intent = Intent(context, AppService::class.java)
                intent.action = Constant.ACTION_CHANGE_LANGUAGE
                ServiceUtils.startService(context, intent)
            }
        }

        binding.layoutHeader.tvBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getData() {
        viewModel.getListLanguage(requireContext())
    }
}