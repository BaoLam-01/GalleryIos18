package com.base.capva.image.image_animate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.base.capva.image.CapVaImageView;

public abstract class BaseImageAnimate {

    protected CapVaImageView capVaImageView;

    protected float progress = 1;
    private Path clipPath;

    protected int timeDelay;
    public ValueAnimator valueAnimator;
    private boolean isRunning;

    protected PointF centerPoint;
    protected Matrix defaultMatrix = new Matrix();
    protected Matrix matrix = new Matrix();

    protected float transparentDefault;

    protected boolean isStopMedia;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progress = 1;
            capVaImageView.stopMedia();
            isRunning = false;
        }
    };
    private CapVaImageView.OnCapVaImageListener onCapVaImageListener;

    public void init(CapVaImageView capVaImageView, CapVaImageView.OnCapVaImageListener onCapVaImageListener) {
        this.onCapVaImageListener = onCapVaImageListener;
        this.capVaImageView = capVaImageView;
        clipPath = new Path();
    }

    public void prepare() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(valueAnimator -> {
                progress = (float) valueAnimator.getAnimatedValue();
                animate();
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isStopMedia) {
                        progress = 1;
                        capVaImageView.stopMedia();
                        isRunning = false;
                    }
                    if (onCapVaImageListener != null) {
                        onCapVaImageListener.onElementStopAnimation();
                    }
                }
            });
            valueAnimator.setInterpolator(null);
        }
        transparentDefault = capVaImageView.getTransparent();
    }

    public void startAnimation() {
        progress = 0;
        animate();

        if (valueAnimator != null) {
            valueAnimator.setStartDelay(timeDelay);
            valueAnimator.setDuration(getDuration());
            valueAnimator.start();
        }
        int durationVideo = capVaImageView.getTimeEnd() - capVaImageView.getTimeStart();
        if (durationVideo > getDuration()) {
            isStopMedia = false;
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, durationVideo);
        } else {
            isStopMedia = true;
        }
        isRunning = true;
    }

    public void stopAnimation() {
        if (valueAnimator != null) {
            valueAnimator.end();
        }
        progress = 1;
        handler.removeCallbacks(runnable);
        isRunning = false;
    }

    public void setFrameInTime(int time) {
        int timeInAnimation = time - timeDelay;
        if (timeInAnimation >= 0 && getDuration() != 0 && timeInAnimation <= getDuration()) {
            progress = timeInAnimation / (float) getDuration();
            progress = Math.min(progress, 1);
        } else if (timeInAnimation > getDuration()) {
            progress = 1;
        } else {
            progress = 0;
        }

        animate();
    }

    public void onDraw(Canvas canvas) {
//        float[] point = capVaImageView.getMappedBoundPoints(0);
//        clipPath.reset();
//        clipPath.moveTo(point[0], point[1]);
//        clipPath.lineTo(point[2], point[3]);
//        clipPath.lineTo(point[4], point[5]);
//        clipPath.lineTo(point[6], point[7]);
//        clipPath.close();

//        canvas.clipPath(clipPath);
        draw(canvas);
    }

    public void updateDefaultMatrix() {
        defaultMatrix.set(capVaImageView.getMatrix());
        centerPoint = capVaImageView.getMappedCenterPoint();
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    public void setTransparentDefault(float transparentDefault) {
        this.transparentDefault = transparentDefault;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getTotalDuration() {
        return timeDelay + getDuration();
    }

    public int getTimeDelay() {
        return timeDelay;
    }

    protected abstract void animate();

    protected abstract void draw(Canvas canvas);

    public abstract int getDuration();

    public abstract BaseImageAnimate duplicate();

    public abstract String getName();
}
