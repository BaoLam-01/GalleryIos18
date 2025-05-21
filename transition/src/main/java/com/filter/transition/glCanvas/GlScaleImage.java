package com.filter.transition.glCanvas;

import android.content.Context;
import android.graphics.Canvas;

import com.filter.transition.glCanvas.base.BaseGlCanvas;

public class GlScaleImage extends BaseGlCanvas {
    public GlScaleImage(Context context) {
        super(context);
    }

    @Override
    protected void drawCanvas(Canvas canvas) {
        canvas.scale(progress, progress, mOutputWidth / 2f, mOutputHeight / 2f);
        canvas.drawBitmap(bm, 0, 0, null);
    }
}
