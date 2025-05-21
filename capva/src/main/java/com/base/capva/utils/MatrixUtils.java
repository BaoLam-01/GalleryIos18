package com.base.capva.utils;

import android.graphics.Matrix;

public class MatrixUtils {
    public static float[] getValuesFromMatrix(Matrix matrix) {
        float[] v = new float[9];
        matrix.getValues(v);
        return v;
    }

    public static float[] getTranslate(Matrix matrix) {
        float[] v = getValuesFromMatrix(matrix);
        float tx = v[Matrix.MTRANS_X];
        float ty = v[Matrix.MTRANS_Y];
        return new float[]{tx, ty};
    }

    public static float getScale(Matrix matrix) {
        float[] v = getValuesFromMatrix(matrix);
        float scaleX = v[Matrix.MSCALE_X];
        float sKewY = v[Matrix.MSKEW_Y];
        return (float) Math.sqrt(scaleX * scaleX + sKewY * sKewY);
    }

    public static float getRotate(Matrix matrix) {
        float[] v = getValuesFromMatrix(matrix);
        return (float) (Math.atan2(v[Matrix.MSKEW_X], v[Matrix.MSCALE_X]) * (180 / Math.PI));
    }
}
