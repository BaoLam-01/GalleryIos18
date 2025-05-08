package com.example.galleryios18.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseDialog extends Dialog {
    protected long lastClickTime = 0;
    private Toast toast;

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable,
                         @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected boolean checkDoubleClick() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastClickTime) < 1000) {
            return false;
        }
        lastClickTime = currentTime;
        return true;
    }

    public void showToast(String message) {
        if (toast == null)
            toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        try {
            toast.cancel();
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
