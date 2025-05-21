package com.base.capva.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;

import com.base.capva.R;
import com.base.capva.image.CapVaImageView;
import com.base.capva.utils.ActionTouch;
import com.base.capva.utils.MethodUtils;

public class CropView extends View {

    private CapVaImageView baseView;
    private Bitmap bitmap;
    private Paint paint, paintDrawPath;
    private Path path, clipPath;
    private PointF centerPoint;
    private Bitmap bmOriginal, bmKnob;

    private float rotateBaseView;
    private float left, top;
    private float minScale, scaleBitmap, maxScale = 3f;
    private float downX, downY, tempX, tempY, oldDistance;
    private float[] translate;
    private PointF zoom1, zoom2, zoom3, zoom4;

    private int color;

    private boolean isShowHandle = true;

    private int baseViewWidth, baseViewHeight;

    private ActionTouch actionTouch = ActionTouch.NONE;
    private Matrix matrix;

    private int tempWidth, tempHeight, tempWidth1, tempHeight1;
    private float tempTop;
    private boolean useTwoFinger;

    public CropView(Context context) {
        super(context);
        init();
    }

    public CropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        zoom1 = new PointF();
        zoom4 = new PointF();
        zoom3 = new PointF();
        zoom2 = new PointF();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrawPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrawPath.setColor(getResources().getColor(R.color.colorLineSelected));
        paintDrawPath.setStyle(Paint.Style.STROKE);
        paintDrawPath.setStrokeWidth(getResources().getDimension(R.dimen.stroke_selected));
        color = getResources().getColor(R.color.colorBackgroundCrop);

        path = new Path();
        clipPath = new Path();
        bmKnob = BitmapFactory.decodeResource(getResources(), R.drawable.ic_knob);
    }

    public void setBaseView(CapVaImageView baseView, boolean useTwoFinger) {
        this.useTwoFinger = useTwoFinger;
        this.baseView = baseView;
        rotateBaseView = baseView.getRotate();
        float[] point = baseView.getMappedBoundPoints(0);
        translate = baseView.getTranslate();
        centerPoint = baseView.getMappedCenterPoint();

        bmOriginal = baseView.getBitmap();

        RectF imageRect = baseView.getImageRect();

        baseViewWidth = (int) (baseView.getWidth() * baseView.getScale());
        baseViewHeight = (int) (baseView.getHeight() * baseView.getScale());

//        minScale = scaleBitmap = baseViewWidth / imageRect.width();
        scaleBitmap = baseViewWidth / imageRect.width();
        minScale = baseViewWidth / baseView.getImageRectCenterCrop().width();

        bitmap = Bitmap.createScaledBitmap(bmOriginal, (int) (bmOriginal.getWidth() * scaleBitmap), (int) (bmOriginal.getHeight() * scaleBitmap), true);

        left = -imageRect.left * scaleBitmap;
        top = -imageRect.top * scaleBitmap;

        clipPath.reset();
        clipPath.moveTo(point[0], point[1]);
        clipPath.lineTo(point[2], point[3]);
        clipPath.lineTo(point[4], point[5]);
        clipPath.lineTo(point[6], point[7]);
        clipPath.close();

        matrix = new Matrix();
        matrix.postRotate(-rotateBaseView);
        setVisibility(VISIBLE);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (baseView != null) {
            canvas.save();
            canvas.translate(translate[0], translate[1]);
            canvas.rotate(-rotateBaseView);
            canvas.drawBitmap(bitmap, left, top, paint);

            path.reset();
            path.moveTo(left, top);
            path.lineTo(left + bitmap.getWidth(), top);
            path.lineTo(left + bitmap.getWidth(), bitmap.getHeight() + top);
            path.lineTo(left, bitmap.getHeight() + top);
            path.close();
            canvas.drawPath(path, paintDrawPath);

            canvas.restore();

            canvas.save();
            canvas.clipPath(clipPath, Region.Op.DIFFERENCE);
            canvas.drawColor(color);
            canvas.restore();
            canvas.drawPath(clipPath, paintDrawPath);

            canvas.save();
            canvas.translate(translate[0], translate[1]);
            canvas.rotate(-rotateBaseView);

            zoom1.x = left - bmKnob.getWidth() / 2f;
            zoom1.y = top - bmKnob.getHeight() / 2f;

            zoom2.x = left + bitmap.getWidth() - bmKnob.getWidth() / 2f;
            zoom2.y = top - bmKnob.getHeight() / 2f;

            zoom3.x = left + bitmap.getWidth() - bmKnob.getWidth() / 2f;
            zoom3.y = top + bitmap.getHeight() - bmKnob.getHeight() / 2f;

            zoom4.x = left - bmKnob.getWidth() / 2f;
            zoom4.y = top + bitmap.getHeight() - bmKnob.getHeight() / 2f;

            if (isShowHandle && !useTwoFinger) {
                canvas.drawBitmap(bmKnob, zoom1.x, zoom1.y, paintDrawPath);
                canvas.drawBitmap(bmKnob, zoom2.x, zoom2.y, paintDrawPath);
                canvas.drawBitmap(bmKnob, zoom3.x, zoom3.y, paintDrawPath);
                canvas.drawBitmap(bmKnob, zoom4.x, zoom4.y, paintDrawPath);
            }

            canvas.restore();
        }
    }

    protected float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    protected float calculateDistance(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private PointF calculateMidPoint(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return new PointF();
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    private final float pointerLimitDis = 20f;
    private PointF midPoint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                tempX = left;
                tempY = top;
                oldDistance = MethodUtils.calculateDistance(centerPoint.x, centerPoint.y, event.getX(), event.getY());

                getActionTouch();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (!useTwoFinger) {
                    break;
                }
                if (spacing(event) > pointerLimitDis) {
                    oldDistance = calculateDistance(event);
                    midPoint = calculateMidPoint(event);
                    actionTouch = ActionTouch.ZOOM_WITH_TWO_TOUCH;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                isShowHandle = false;
                if (actionTouch == ActionTouch.DRAG) {
                    //todo: drag
                    float dx = downX - event.getX();
                    float dy = downY - event.getY();
                    left = tempX - dx;
                    top = tempY - dy;

                    if (left > 0) {
                        left = 0;
                    } else if (baseViewWidth - left > bitmap.getWidth()) {
                        left = baseViewWidth - bitmap.getWidth();
                    }

                    if (top > 0) {
                        top = 0;
                    } else if (baseViewHeight - top > bitmap.getHeight()) {
                        top = baseViewHeight - bitmap.getHeight();
                    }
                } else if (actionTouch == ActionTouch.ZOOM_WITH_TWO_TOUCH) {
                    float newDistance = calculateDistance(event);
                    float scale = newDistance / oldDistance;
                    float s = 1 + ((scale - 1) / 2);
                    float temp = Math.max(scaleBitmap * s, minScale);
                    temp = Math.min(temp, maxScale);
                    int newWidth = (int) (bmOriginal.getWidth() * temp);
                    int newHeight = (int) (bmOriginal.getHeight() * temp);
                    left -= (newWidth - bitmap.getWidth()) / 2f;
                    top -= (newHeight - bitmap.getHeight()) / 2f;
                    if (top > 0) {
                        top = 0;
                    } else if (newHeight + top < baseViewHeight) {
                        top = baseViewHeight - newHeight;
                    }

                    if (left > 0) {
                        left = 0;
                    } else if (newWidth + left < baseViewWidth) {
                        left = baseViewWidth - newWidth;
                    }
                    bitmap = Bitmap.createScaledBitmap(bmOriginal, newWidth, newHeight, true);
                } else {
                    float newDistance = MethodUtils.calculateDistance(centerPoint.x, centerPoint.y, event.getX(), event.getY());
                    float scale = newDistance / oldDistance;
                    float s = 1 + ((scale - 1) / 2);
                    scaleBitmap(s);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (!useTwoFinger) {
                    break;
                }
                actionTouch = ActionTouch.NONE;
                break;

            case MotionEvent.ACTION_UP:
                actionTouch = ActionTouch.NONE;
                scaleBitmap = (float) bitmap.getWidth() / bmOriginal.getWidth();
                isShowHandle = true;
                invalidate();
                break;
        }
        return true;
    }

    private void scaleBitmap(float s) {
        float temp = Math.max(scaleBitmap * s, minScale);
        int oldWidth, oldHeight, d, d1;
        int newWidth = (int) (bmOriginal.getWidth() * temp);
        int newHeight = (int) (bmOriginal.getHeight() * temp);
        boolean check;
        switch (actionTouch) {
            case RIGHT_BOT:
                if (newHeight + top < baseViewHeight) {
                    newHeight = (int) (baseViewHeight - top);
                    newWidth = newHeight * bmOriginal.getWidth() / bmOriginal.getHeight();
                    check = !(newWidth + left < baseViewWidth);
                } else {
                    check = false;
                }

                if (!check && newWidth + left < baseViewWidth) {
                    newWidth = (int) (baseViewWidth - left);
                    newHeight = newWidth * bmOriginal.getHeight() / bmOriginal.getWidth();
                }
                bitmap = Bitmap.createScaledBitmap(bmOriginal, newWidth, newHeight, true);
                break;
            case LEFT_BOT:
                if (newHeight + top < baseViewHeight) {
                    newHeight = (int) (baseViewHeight - top);
                    newWidth = newHeight * bmOriginal.getWidth() / bmOriginal.getHeight();
                }
                oldWidth = bitmap.getWidth();
                d = newWidth - oldWidth;
                left -= d;

                if (left > 0) {
                    left = 0;
                    newWidth = tempWidth;
                    newHeight = tempHeight;
                } else {
                    tempWidth = newWidth;
                    tempHeight = newHeight;
                }

                bitmap = Bitmap.createScaledBitmap(bmOriginal, newWidth, newHeight, true);
                break;
            case RIGHT_TOP:
                if (newWidth + left < baseViewWidth) {
                    newWidth = (int) (baseViewWidth - left);
                    newHeight = newWidth * bmOriginal.getHeight() / bmOriginal.getWidth();
                }

                oldHeight = bitmap.getHeight();
                d = newHeight - oldHeight;
                top -= d;

                if (top > 0) {
                    top = 0;
                    newWidth = tempWidth;
                    newHeight = tempHeight;
                } else {
                    tempWidth = newWidth;
                    tempHeight = newHeight;
                }

                bitmap = Bitmap.createScaledBitmap(bmOriginal, newWidth, newHeight, true);
                break;
            case LEFT_TOP:
                oldHeight = bitmap.getHeight();
                oldWidth = bitmap.getWidth();
                d = newHeight - oldHeight;
                d1 = newWidth - oldWidth;

                float tempT = top - d;
                float tempL = left - d1;

                if (tempT > 0) {
                    newWidth = tempWidth;
                    newHeight = tempHeight;
                    top = 0;
                    left -= (newWidth - oldWidth);
                    check = left < 0;
                } else {
                    tempWidth = newWidth;
                    tempHeight = newHeight;
                    check = false;
                }

                if (!check) {
                    if (tempL > 0) {
                        newWidth = tempWidth1;
                        newHeight = tempHeight1;
                        left = 0;
                        top = tempTop;
                    } else {
                        tempWidth1 = newWidth;
                        tempHeight1 = newHeight;
                        left = tempL;
                        top -= (newHeight - oldHeight);
                        tempTop = top;
                    }
                }
                bitmap = Bitmap.createScaledBitmap(bmOriginal, newWidth, newHeight, true);
                break;
        }
    }

    private void getActionTouch() {
        float[] p = new float[]{
                zoom1.x, zoom1.y,
                zoom2.x, zoom2.y,
                zoom3.x, zoom3.y,
                zoom4.x, zoom4.y
        };
        float[] point = new float[p.length];
        matrix.mapPoints(point, p);
        if (touchInHandle(point[0], point[1])) {
            actionTouch = ActionTouch.LEFT_TOP;
        } else if (touchInHandle(point[2], point[3])) {
            actionTouch = ActionTouch.RIGHT_TOP;
        } else if (touchInHandle(point[4], point[5])) {
            actionTouch = ActionTouch.RIGHT_BOT;
        } else if (touchInHandle(point[6], point[7])) {
            actionTouch = ActionTouch.LEFT_BOT;
        } else {
            actionTouch = ActionTouch.DRAG;
        }
    }

    private boolean touchInHandle(float left, float top) {
        return downX >= (left - 10) + translate[0]
                && downX <= left + bmKnob.getWidth() + 10 + translate[0]
                && downY >= top - 10 + +translate[1]
                && downY <= top + bmKnob.getHeight() + 10 + +translate[1];
    }

    public RectF getRectCrop() {
        setVisibility(INVISIBLE);
        float l = Math.abs(left);
        float t = Math.abs(top);

        int left = (int) Math.max(0, l / scaleBitmap);
        int top = (int) Math.max(0, t / scaleBitmap);
        int right = (int) Math.min((l + baseViewWidth) / scaleBitmap, bmOriginal.getWidth());
        int bot = (int) Math.min((t + baseViewHeight) / scaleBitmap, bmOriginal.getHeight());

        RectF rectF = new RectF();
        rectF.left = left;
        rectF.top = top;
        rectF.right = right;
        rectF.bottom = bot;
        return rectF;
    }

    public void cancelCrop() {
        setVisibility(INVISIBLE);
    }
}
