package com.example.galleryios18.ui.main.editimage

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.galleryios18.App
import com.example.galleryios18.R
import com.example.galleryios18.data.models.TypeEdit
import com.example.galleryios18.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ThreadLocalRandom

class EditImageViewModel : BaseViewModel() {
    val listItemAdjust: MutableLiveData<List<TypeEdit>> = MutableLiveData()

    fun getAllItemAdjust() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e("LamPro | getAllListItemAdjust: ${throwable.message}")
            }
        }) {
            listItemAdjust.postValue(getListItemAdjust())
        }
    }

    private fun getListItemAdjust(): List<TypeEdit> {
        val listTypeEdits = mutableListOf<TypeEdit>()
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.auto),
                0.0,
                R.drawable.ic_adjust_auto,
                false,
                ThreadLocalRandom.current().nextDouble((-25).toDouble(), 26.0)
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.brilliance),
                0.0,
                R.drawable.ic_adjust_brilliance,
                numberRandomAuto = ThreadLocalRandom.current().nextDouble((-25).toDouble(), 26.0)
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.highlights),
                0.0,
                R.drawable.ic_adjust_highlights,
                numberRandomAuto = ThreadLocalRandom.current().nextDouble((-25).toDouble(), 26.0)
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.shadows),
                0.0,
                R.drawable.ic_adjust_shadows,
                numberRandomAuto = ThreadLocalRandom.current().nextDouble((-25).toDouble(), 26.0)
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.contrast),
                0.0,
                R.drawable.ic_adjust_contrast,
                numberRandomAuto = ThreadLocalRandom.current().nextDouble((-25).toDouble(), 26.0)
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.brightness),
                0.0,
                R.drawable.ic_adjust_brightness,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.blackpoint),
                0.0,
                R.drawable.ic_adjust_blackpoint,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.saturation),
                0.0,
                R.drawable.ic_adjust_saturation,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.vibrance),
                0.0,
                R.drawable.ic_adjust_vibrance,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.warmth),
                0.0,
                R.drawable.ic_adjust_warmth,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.tint),
                0.0,
                R.drawable.ic_adjust_tint,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.sharpness),
                0.0,
                R.drawable.ic_adjust_sharpness,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.definition),
                0.0,
                R.drawable.ic_adjust_definition,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.noise_reduction),
                0.0,
                R.drawable.ic_adjust_noise_reduction,
            )
        )
        listTypeEdits.add(
            TypeEdit(
                ContextCompat.getString(App.instance, R.string.vignette),
                0.0,
                R.drawable.ic_adjust_vignette,
            )
        )

        return listTypeEdits
    }
}