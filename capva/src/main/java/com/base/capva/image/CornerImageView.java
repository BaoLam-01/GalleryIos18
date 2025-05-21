package com.base.capva.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CornerImageView extends AppCompatImageView {

    private boolean isBackground, isSave, isVideo;
    private Path clipPath;

    private float cornerClip = 0f;
    private String clipRound = "";
    private Bitmap bitmap;
    private Matrix matrix;
    private Paint paint;
    private float scale = 1f;
    private float transX = 0f, transY = 0f;

    public CornerImageView(Context context) {
        super(context);
        init();
    }

    public CornerImageView(Context context, boolean isBackground, boolean isSave) {
        super(context);
        this.isBackground = isBackground;
        this.isSave = isSave;
        init();
    }

    public CornerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        if (!isBackground && isSave && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
//            setLayerType(LAYER_TYPE_SOFTWARE, null);
//        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clipPath = new Path();
        matrix = new Matrix();
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public void setClip(float cornerClip, String clipRound) {
        this.cornerClip = cornerClip;
        this.clipRound = clipRound;
        if (needUpdatedBitmap()) {
            super.setImageMatrix(new Matrix());
            super.setImageBitmap(updateBitmap());
        } else {
            super.setImageMatrix(matrix);
            super.setImageBitmap(bitmap);
        }
        invalidate();
    }

    private boolean needUpdatedBitmap() {
        return cornerClip != 0 || !clipRound.equals("");
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        this.bitmap = bm;
        if (isSave && isVideo) {
//            scaleBitmap();
            setBitmapWithMatrix();
        } else {
            super.setImageBitmap(updateBitmap());
        }

//        if (needUpdatedBitmap()) {
//        super.setImageBitmap(updateBitmap());
//        } else {
//            super.setImageBitmap(bm);
//        }
    }

//    private void scaleBitmap() {
//        Matrix m = new Matrix();
//        m.set(matrix);
//        m.postScale(scale, scale, getWidth() / 2f, getHeight() / 2f);
//        super.setImageBitmap(getBitmapWithMatrix(m));
//    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        this.matrix.set(matrix);
//        if (needUpdatedBitmap()) {
        super.setImageBitmap(updateBitmap());
//        } else {
//            super.setImageMatrix(matrix);
//        }
    }

    private Bitmap updateBitmap() {
        if (bitmap == null) {
            return null;
        }
        return getBitmapWithMatrix(matrix);
    }

    public void setPhotoZoom(float scale) {
        this.scale = scale;

        if (!isVideo) {
//            scaleBitmap();
            setBitmapWithMatrix();
        }
    }

    public void setPhotoFlow(float scale, float transY) {
        this.scale = scale;
        this.transX = 0;
        this.transY = transY;

        if (!isVideo) {
            setBitmapWithMatrix();
        }
    }

    private void setBitmapWithMatrix() {
        if (getWidth() > 0 && getHeight() > 0) {
            Matrix m = new Matrix();
            m.set(matrix);
            m.postScale(scale, scale, getWidth() / 2f, getHeight() / 2f);
            m.postTranslate(transX, transY);
            super.setImageBitmap(getBitmapWithMatrix(m));
        }
    }

    private Bitmap getBitmapWithMatrix(Matrix matrix) {
        int w = 50;
        int h = 50;
        if (getWidth() > 0 && getHeight() > 0) {
            w = getWidth();
            h = getHeight();
        }
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.clipPath(PathUtils.getPathClip(clipPath, CornerImageView.this, cornerClip, clipRound));
        canvas.drawBitmap(bitmap, matrix, paint);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if (isSave) {
//            canvas.clipPath(PathUtils.getPathClip(clipPath, CornerImageView.this, cornerClip, clipRound));
//        }
        super.onDraw(canvas);
    }
}
