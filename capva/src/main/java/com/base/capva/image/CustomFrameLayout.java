package com.base.capva.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class CustomFrameLayout extends CardView {

    private float cornerClip = 0f;
    private String clipRound = "";
    private boolean isSave;

    private Path clipPath;

    public CustomFrameLayout(@NonNull Context context, boolean isSave) {
        super(context);
        this.isSave = isSave;
        init();
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        if (isSave && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//            setLayerType(LAYER_TYPE_SOFTWARE, null);
//        }
        clipPath = new Path();

        setUseCompatPadding(false);
        setCardBackgroundColor(Color.TRANSPARENT);
        setElevation(0);
        setRadius(0);
        setCardElevation(0);
        setClipChildren(false);
        setClipToPadding(false);

//        setBackgroundResource(R.drawable.test);
//        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        if (!isSave) {
            setClipToOutline(true);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    setOutline(outline);
                }
            });
        }
    }

    public void setClip(float cornerClip, String clipRound) {
        this.cornerClip = cornerClip;
        this.clipRound = clipRound;
        invalidate();
    }

    public void setClipRound(String clipRound) {
        this.clipRound = clipRound;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        if (isSave) {
//            canvas.clipPath(PathUtils.getPathClip(clipPath, CustomFrameLayout.this, cornerClip, clipRound));
//        }
        super.dispatchDraw(canvas);
    }

    private void setOutline(Outline outline) {
        if (clipRound.equals("CIRCLE")) {
            int d = Math.min(getWidth(), getHeight());
            outline.setOval(0, 0, d, d);
        } else if (clipRound.equals("defective rectangle bot")) {
        } else if (clipRound.equals("defective rectangle top")) {
        } else if (clipRound.equals("OVAL")) {
        } else if (cornerClip != 0) {
            outline.setRoundRect(0, 0, getWidth(), getHeight(), cornerClip);
        } else {
            outline.setRect(0, 0, getWidth(), getHeight());
        }
    }
}
