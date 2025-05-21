package com.base.capva.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

import com.filter.advanced.adjust.GPUImageBrightnessFilter;
import com.filter.advanced.adjust.GPUImageContrastFilter;
import com.filter.advanced.adjust.GPUImageExposureFilter;
import com.filter.advanced.adjust.GPUImageGammaFilter;
import com.filter.advanced.adjust.GPUImageHighlightBlackPointFilter;
import com.filter.advanced.adjust.GPUImageHighlightShadowFilter;
import com.filter.advanced.adjust.GPUImageSaturationFilter;
import com.filter.advanced.adjust.GPUImageSharpenFilter;
import com.filter.advanced.adjust.GPUImageVibranceFilter;
import com.filter.advanced.adjust.GPUImageWhiteBalanceFilter;
import com.filter.base.GPUImageFilter;

import java.util.List;

public class MethodUtils {

    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    public static float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    public static void setBitmap(Bitmap bitmap, ImageView imageView, Bitmap defaultBitmap) {
        if (bitmap != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, defaultBitmap.getWidth(), defaultBitmap.getHeight(), false);

            Bitmap rs = Bitmap.createBitmap(defaultBitmap.getWidth(), defaultBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(rs);
            canvas.drawBitmap(bitmap1, 0, 0, paint);


            canvas.drawBitmap(defaultBitmap, 0, 0, paint);

            imageView.setColorFilter(null);
            imageView.setImageBitmap(rs);

        }
    }

    public static float dpToPx(float dp) {
        return (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getPositionFilter(AdjustEnum adjustEnum, List<GPUImageFilter> filters) {
        switch (adjustEnum) {
            case BRIGHTNESS:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageBrightnessFilter) {
                        return i;
                    }
                }
                break;
            case CONTRAST:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageContrastFilter) {
                        return i;
                    }
                }
                break;
            case EXPOSURE:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageExposureFilter) {
                        return i;
                    }
                }
                break;
            case BRILLIANCE:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageGammaFilter) {
                        return i;
                    }
                }
                break;
            case HIGHLIGHT:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageHighlightShadowFilter) {
                        return i;
                    }
                }
                break;
            case SHADOW:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageHighlightShadowFilter) {
                        return i;
                    }
                }
                break;
            case SATURATION:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageSaturationFilter) {
                        return i;
                    }
                }
                break;
            case SHARPNESS:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageSharpenFilter) {
                        return i;
                    }
                }
                break;
            case TINT:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageWhiteBalanceFilter) {
                        return i;
                    }
                }
                break;
            case BLACK_POINT:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageHighlightBlackPointFilter) {
                        return i;
                    }
                }
                break;
            case VIBRANCE:
                for (int i = 0; i < filters.size(); i++) {
                    if (filters.get(i) instanceof GPUImageVibranceFilter) {
                        return i;
                    }
                }
                break;
        }
        return -1;
    }

    public static float getMax(float[] p) {
        float max = p[0];

        for (float v : p) {
            if (v > max) {
                max = v;
            }
        }

        return max;
    }

    public static float getMin(float[] p) {
        float max = p[0];

        for (float v : p) {
            if (v < max) {
                max = v;
            }
        }

        return max;
    }

    public static String convertColorToString(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }
}
