@file:Suppress("UNUSED_EXPRESSION")

package com.example.galleryios18.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.example.galleryios18.R

fun EditText.changeBackGroundIsHasFocus() {
    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            setBackgroundResource(R.drawable.bg_ripple_f3f8ff_stroke_1573fe)
        } else {
            if (!text.isNullOrEmpty()) setBackgroundResource(R.drawable.bg_ripple_f3f8ff_stroke_d9d9d9)
            else setBackgroundResource(R.drawable.bg_ripple_ffffff_stroke_d9d9d9)
        }
    }
}

fun EditText.setOnlyOneCharacter() {
    var isUpdating = false
    addTextChangedListener(object : TextWatcher {
        var posCutText = 0
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            posCutText = start
        }

        override fun afterTextChanged(s: Editable?) {
            if (isUpdating) return
            s?.let {
                if (it.length > 1) {
                    isUpdating = true
                    val char = it[posCutText].toString()
                    it.replace(0, it.length, char)
                    isUpdating = false
                }
            }
        }
    })
}

fun EditText.nextFocusEditText(editTextSetFocus: EditText? = null) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (text.isNotEmpty()) {
                editTextSetFocus?.requestFocus()
            }
        }

    })
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            editTextSetFocus?.requestFocus()
            true
        } else {
            false
        }
    }
}

fun EditText.prevFocusEditText(editTextSetFocus: EditText? = null) {
    setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
            editTextSetFocus?.requestFocus()
            setBackgroundResource(R.drawable.bg_ripple_ffffff_stroke_d9d9d9)
            true
        }
        false
    }
}
