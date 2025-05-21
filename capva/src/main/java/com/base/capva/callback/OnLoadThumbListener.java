package com.base.capva.callback;

import android.graphics.Bitmap;

public interface OnLoadThumbListener {
    void onSuccess(Bitmap bitmap);

    void onFailure();
}
