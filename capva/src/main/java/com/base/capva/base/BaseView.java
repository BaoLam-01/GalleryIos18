package com.base.capva.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.base.capva.utils.MatrixUtils;

public abstract class BaseView extends FrameLayout {
    public static final int MIN_WIDTH_HEIGHT = 50;

    private Matrix matrix = new Matrix();
    public static int paddingLeftRight = 20;
    private boolean disableEdit;
    private boolean disableEditSize;
    protected boolean isSave;
    protected boolean isBackground;

    public BaseView(Context context, boolean isSave) {
        super(context);
        this.isSave = isSave;
        init();
    }

    public BaseView(Context context, boolean isSave, boolean isBackground) {
        super(context);
        this.isSave = isSave;
        this.isBackground = isBackground;
        init();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setClipToPadding(false);
        setClipChildren(false);
    }

    public abstract void prepare();

    public abstract void startAnimation();

    public abstract void stopAnimation();

    public abstract void setFrameInTime(int time);

    public abstract void setTimeDelay(int timeDelay);

    public abstract float getTransparent();

    public abstract void setTransparent(float transparent);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.concat(matrix);
        super.dispatchDraw(canvas);
    }

    public boolean isBackground() {
        return isBackground;
    }

    public boolean isDisableEditSize() {
        return disableEditSize;
    }

    public void setDisableEditSize(boolean disableEditSize) {
        this.disableEditSize = disableEditSize;
    }

    public void setDisableEdit(boolean disableEdit) {
        this.disableEdit = disableEdit;
    }

    public boolean isDisableEdit() {
        return disableEdit;
    }

    public void nudgeUp() {
        matrix.postTranslate(0, -1);
        invalidate();
    }

    public void nudgeDown() {
        matrix.postTranslate(0, 1);
        invalidate();
    }

    public void nudgeLeft() {
        matrix.postTranslate(-1, 0);
        invalidate();
    }

    public void nudgeRight() {
        matrix.postTranslate(1, 0);
        invalidate();
    }

    public float[] getMappedBoundPoints(int padding) {
        float[] dst = new float[8];
        matrix.mapPoints(dst, getBoundPoints(padding));
        return dst;
    }

    public RectF getMappedBound() {
        RectF dst = new RectF();
        matrix.mapRect(dst, getBound());
        return dst;
    }

    private RectF getBound() {
        return new RectF(0, 0, getWidth(), getHeight());
    }

    protected float[] getBoundPoints(int padding) {
        return new float[]{
                -padding, -padding,
                getWidth() + padding, -padding,
                getWidth() + padding, getHeight() + padding,
                -padding, getHeight() + padding
        };
    }

    private PointF getCenterPoint() {
        return new PointF(getWidth() / 2f, getHeight() / 2f);
    }

    public PointF getMappedCenterPoint() {
        PointF pointF = getCenterPoint();
        float[] dst = getMappedPoints(new float[]{
                pointF.x,
                pointF.y
        });
        return new PointF(dst[0], dst[1]);
    }

    public float[] getMappedPoints(float[] src) {
        float[] dst = new float[src.length];
        matrix.mapPoints(dst, src);
        return dst;
    }

    public void postScale(float scale) {
        matrix.postScale(scale, scale);
    }

    public void postScale(float scale, float x, float y) {
        matrix.postScale(scale, scale, x, y);
    }


    public void postTranslate(float x, float y) {
        matrix.postTranslate(x, y);
    }

    public void postRotate(float degrees) {
        PointF center = getMappedCenterPoint();
        matrix.postRotate(degrees, center.x, center.y);
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public boolean touchInView(float dx, float dy, int padding) {
        float[] mappedBoundPoints = getMappedBoundPoints(padding);

        float[][] polygon = {
                {mappedBoundPoints[0], mappedBoundPoints[1]},
                {mappedBoundPoints[2], mappedBoundPoints[3]},
                {mappedBoundPoints[4], mappedBoundPoints[5]},
                {mappedBoundPoints[6], mappedBoundPoints[7]}
        };

        float[] point = {dx, dy};

        boolean odd = false;
        for (int i = 0, j = polygon.length - 1; i < polygon.length; i++) {
            if (((polygon[i][1] > point[1]) != (polygon[j][1] > point[1]))
                    && (point[0] < (polygon[j][0] - polygon[i][0]) * (point[1] - polygon[i][1]) / (polygon[j][1] - polygon[i][1]) + polygon[i][0])) {
                odd = !odd;
            }
            j = i;
        }
        return odd;
    }

//    private float[] getValuesFromMatrix() {
//        float[] v = new float[9];
//        matrix.getValues(v);
//        return v;
//    }

    public float[] getTranslate() {
//        float[] v = getValuesFromMatrix();
//        float tx = v[Matrix.MTRANS_X];
//        float ty = v[Matrix.MTRANS_Y];
//        return new float[]{tx, ty};
        return MatrixUtils.getTranslate(matrix);
    }

    public float getScale() {
//        float[] v = getValuesFromMatrix();
//        float scaleX = v[Matrix.MSCALE_X];
//        float sKewY = v[Matrix.MSKEW_Y];
//        return (float) Math.sqrt(scaleX * scaleX + sKewY * sKewY);
        return MatrixUtils.getScale(matrix);
    }

    public float getRotate() {
//        float[] v = getValuesFromMatrix();
//        return (float) (Math.atan2(v[Matrix.MSKEW_X], v[Matrix.MSCALE_X]) * (180 / Math.PI));
        return MatrixUtils.getRotate(matrix);
    }
}
