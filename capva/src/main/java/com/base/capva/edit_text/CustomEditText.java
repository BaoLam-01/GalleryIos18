package com.base.capva.edit_text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.AppCompatEditText;

import com.base.capva.R;
import com.base.capva.text.CapVaTextView;

public class CustomEditText extends AppCompatEditText {

    private Matrix matrix;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setPadding(0, 0, 0, 0);
        setBackgroundColor(Color.TRANSPARENT);
        matrix = new Matrix();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setCustomInsertionActionModeCallback(callback);
        }
        setCustomSelectionActionModeCallback(callback);
        setLongClickable(false);
        setTextIsSelectable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTextSelectHandleLeft(R.drawable.ic_empty);
            setTextSelectHandleRight(R.drawable.ic_empty);
            setTextSelectHandle(R.drawable.ic_empty);
        }
    }

    private final ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (menu != null) {
                menu.clear();
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    };

    public void setBaseView(CapVaTextView baseView) {
        post(new Runnable() {
            @Override
            public void run() {
                if (!isAttachedToWindow()) {
                    return;
                }
                if (baseView != null) {
                    float scale = baseView.getScale();
                    float x = (int) ((baseView.getWidth() - getWidth()) / 2f) * scale;
                    float y = (int) ((baseView.getHeight() - getHeight()) / 2f) * scale;

                    matrix.reset();
                    matrix.set(baseView.getMatrix());
                    matrix.postTranslate(x, y);
                    invalidate();
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.concat(matrix);
        super.onDraw(canvas);
    }
}
