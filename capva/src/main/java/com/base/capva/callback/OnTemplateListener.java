package com.base.capva.callback;

import com.base.capva.base.BaseView;
import com.base.capva.image.CapVaImageView;

public interface OnTemplateListener {
    void onEditText(BaseView baseView);

    void onEditTextDone();

    void onInEdit(BaseView baseView);

    void onCrop(CapVaImageView baseView);

    void onCropBackground(CapVaImageView capVaImageView);

    void onCurrentViewScale(float scale);

    void onSelectAnimate();

    void onAddElementSuccess();
}
