/**
 * Created by Matthew Stewart on 10/30/2017 10:47:00 AM
 */


package com.filter.transition.video;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import androidx.annotation.NonNull;

import com.filter.base.OpenGlUtils;
import com.filter.transition.base.BaseGLFilterVideo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class JSToneCurvedVideo extends BaseGLFilterVideo {
    public static final String TONE_CURVE_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            " varying mediump vec2 textureCoordinate;\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D toneCurveTexture;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     lowp float redCurveValue = texture2D(toneCurveTexture, vec2(textureColor.r, 0.0)).r;\n" +
            "     lowp float greenCurveValue = texture2D(toneCurveTexture, vec2(textureColor.g, 0.0)).g;\n" +
            "     lowp float blueCurveValue = texture2D(toneCurveTexture, vec2(textureColor.b, 0.0)).b;\n" +
            "\n" +
            "     gl_FragColor = vec4(redCurveValue, greenCurveValue, blueCurveValue, textureColor.a);\n" +
            " }";

    private int[] mToneCurveTexture = new int[]{OpenGlUtils.NO_TEXTURE};
    private int mToneCurveTextureUniformLocation;
    private Context context;
    private PointF[] mRgbCompositeControlPoints;
    private PointF[] mRedControlPoints;
    private PointF[] mGreenControlPoints;
    private PointF[] mBlueControlPoints;
    private JSPointF[] mRgbCompositeControlPointsDefault;
    private JSPointF[] mRedControlPointsDefault;
    private JSPointF[] mGreenControlPointsDefault;
    private JSPointF[] mBlueControlPointsDefault;
    private ArrayList<Float> mRgbCompositeCurve;
    private ArrayList<Float> mRedCurve;
    private ArrayList<Float> mGreenCurve;
    private ArrayList<Float> mBlueCurve;
    private int mTextureHandlerVideo = -1;
    private int mTextureId = -1;

    @Override
    public void createGLPrgMore() {
        mToneCurveTextureUniformLocation = GLES20.glGetUniformLocation(getMProgram(), "toneCurveTexture");
        mTextureHandlerVideo = GLES20.glGetUniformLocation(getMProgram(), "inputImageTexture");
    }

    @Override
    public void drawMore() {
        if (mToneCurveTexture[0] == -1){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTexture[0]);
            GLES20.glUniform1i(mToneCurveTextureUniformLocation, 3);
        }
    }

    @Override
    public void getSurfaceTexture(@NonNull Function1<? super SurfaceTexture, Unit> cb) {
    }

    @Override
    public int loadShader(int type, @NonNull String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static class JSPointF {
        public float x;
        public float y;
        public float average;


        public JSPointF(PointF pointF) {
            this.x = pointF.x;
            this.y = pointF.y;
            this.average = (pointF.x + pointF.y) / 2f;
        }

        public PointF getPointF() {
            return new PointF(x, y);
        }

        @Override
        public String toString() {
            return "JSPointF{" +
                    "x=" + x +
                    ", y=" + y +
                    ", average=" + average +
                    '}';
        }
    }

    public JSToneCurvedVideo(Context context) {
        super(TONE_CURVE_FRAGMENT_SHADER, context);
        setTransition(false);
        this.context = context;
        setLoadByAsset(false);
        PointF[] defaultCurvePoints = new PointF[]{new PointF(0.0f, 0.0f), new PointF(0.5f, 0.5f), new PointF(1.0f, 1.0f)};
        mRgbCompositeControlPoints = defaultCurvePoints;
        mRedControlPoints = defaultCurvePoints;
        mGreenControlPoints = defaultCurvePoints;
        mBlueControlPoints = defaultCurvePoints;
    }

    public JSPointF[] getmRgbCompositeControlPoints() {
        return mRgbCompositeControlPointsDefault;
    }

    public JSPointF[] getmRedControlPoints() {
        return mRedControlPointsDefault;
    }

    public JSPointF[] getmGreenControlPoints() {
        return mGreenControlPointsDefault;
    }

    public JSPointF[] getmBlueControlPoints() {
        return mBlueControlPointsDefault;
    }

    public void draw(FloatBuffer mVertexBuffer,FloatBuffer mTextureBuffer, int currentId) {
        mTextureId = currentId;
        if (mTextureId != -1) {
            initDefMatrix();

            createGLPrg();
            // 【激活灵魂纹理单元】
            activateTextureVideo();

            doDraw(mVertexBuffer, mTextureBuffer);
        }
    }


    // 50-100

    public void setProgress(float progress) {
        getDefaultPointF();
        changeProgressFilter((int) (progress * 100), mRgbCompositeControlPointsDefault);

        changeProgressFilter((int) (progress * 100), mBlueControlPointsDefault);

        changeProgressFilter((int) (progress * 100), mRedControlPointsDefault);

        changeProgressFilter((int) (progress * 100), mGreenControlPointsDefault);

        PointF[] fsRgb = new PointF[mRgbCompositeControlPointsDefault.length];
        for (int j = 0; j < fsRgb.length; j++) {
            fsRgb[j] = mRgbCompositeControlPointsDefault[j].getPointF();
        }
        PointF[] fsBlue = new PointF[mBlueControlPointsDefault.length];
        for (int j = 0; j < fsBlue.length; j++) {
            fsBlue[j] = mBlueControlPointsDefault[j].getPointF();
        }
        PointF[] fsRed = new PointF[mRedControlPointsDefault.length];
        for (int j = 0; j < fsRed.length; j++) {
            fsRed[j] = mRedControlPointsDefault[j].getPointF();
        }
        PointF[] fsGreen = new PointF[mGreenControlPointsDefault.length];
        for (int j = 0; j < fsGreen.length; j++) {
            fsGreen[j] = mGreenControlPointsDefault[j].getPointF();
        }
        setRgbCompositeControlPoints(fsRgb);
        setBlueControlPoints(fsBlue);
        setRedControlPoints(fsRed);
        setGreenControlPoints(fsGreen);
    }

    private void changeProgressFilter(int i, JSPointF[] ps) {
        for (int j = 0; j < ps.length; j++) {

            PointF f = new PointF();
            JSPointF p = ps[j];
            if (j == 0) {
                f.x = p.x - ((100 - i) * p.x) / 100;
                f.y = p.y - ((100 - i) * p.y) / 100;
            } else if (j == ps.length - 1) {
                f.x = p.x + ((1f - p.x) * (100 - i) / 100);
                f.y = p.y + ((1f - p.y) * (100 - i) / 100);
            } else {
                f.x = Math.abs(p.x - ((p.x - p.average) * (100 - i) / 100));
                f.y = Math.abs(p.y - ((p.y - p.average) * (100 - i) / 100));
            }

            ps[j] = new JSPointF(f);
        }
    }

    ArrayList<PointF[]> curves;
    private int resource;

    public void setCurveFile(int resource) {
        this.resource = resource;
        InputStream input = context.getResources().openRawResource(resource);
        try {
            int version = readShort(input);
            int totalCurves = readShort(input);

            /*ArrayList<PointF[]>*/
            curves = new ArrayList<PointF[]>(totalCurves);
            float pointRate = 1.0f / 255;

            for (int i = 0; i < totalCurves; i++) {
                // 2 bytes, Count of points in the curve (short integer from 2...19)
                short pointCount = readShort(input);

                PointF[] points = new PointF[pointCount];

                // point count * 4
                // Curve points. Each curve point is a pair of short integers where
                // the first number is the output value (vertical coordinate on the
                // Curves dialog graph) and the second is the input value. All coordinates have range 0 to 255.
                for (int j = 0; j < pointCount; j++) {
                    short y = readShort(input);
                    short x = readShort(input);

                    points[j] = new PointF(x * pointRate, y * pointRate);
                }

                curves.add(points);
            }
            input.close();

            mRgbCompositeControlPoints = curves.get(0);
            mRedControlPoints = curves.get(1);
            mGreenControlPoints = curves.get(2);
            mBlueControlPoints = curves.get(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDefaultPointF() {
        InputStream input = context.getResources().openRawResource(resource);

        try {
            int version = readShort(input);
            int totalCurves = readShort(input);

            ArrayList<PointF[]> curves = new ArrayList<PointF[]>(totalCurves);
            float pointRate = 1.0f / 255;

            for (int i = 0; i < totalCurves; i++) {
                // 2 bytes, Count of points in the curve (short integer from 2...19)
                short pointCount = readShort(input);

                PointF[] points = new PointF[pointCount];

                // point count * 4
                // Curve points. Each curve point is a pair of short integers where
                // the first number is the output value (vertical coordinate on the
                // Curves dialog graph) and the second is the input value. All coordinates have range 0 to 255.
                for (int j = 0; j < pointCount; j++) {
                    short y = readShort(input);
                    short x = readShort(input);

                    points[j] = new PointF(x * pointRate, y * pointRate);
                }

                curves.add(points);
            }
            input.close();

            mRgbCompositeControlPointsDefault = new JSPointF[curves.get(0).length];
            for (int i = 0; i < curves.get(0).length; i++) {
                mRgbCompositeControlPointsDefault[i] = new JSPointF(curves.get(0)[i]);
            }

            mRedControlPointsDefault = new JSPointF[curves.get(1).length];
            for (int i = 0; i < curves.get(1).length; i++) {
                mRedControlPointsDefault[i] = new JSPointF(curves.get(1)[i]);
            }

            mGreenControlPointsDefault = new JSPointF[curves.get(2).length];
            for (int i = 0; i < curves.get(2).length; i++) {
                mGreenControlPointsDefault[i] = new JSPointF(curves.get(2)[i]);
            }

            mBlueControlPointsDefault = new JSPointF[curves.get(3).length];
            for (int i = 0; i < curves.get(3).length; i++) {
                mBlueControlPointsDefault[i] = new JSPointF(curves.get(3)[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void activateTextureVideo() {
        if (mTextureHandlerVideo != -1) {
            activateTexture(GLES20.GL_TEXTURE_2D,
                    mTextureId,
                    0,
                    mTextureHandlerVideo);
        }
        if (mToneCurveTexture[0] != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTexture[0]);
            GLES20.glUniform1i(mToneCurveTextureUniformLocation, 3);
        }
    }

    private short readShort(InputStream input) throws IOException {
        return (short) (input.read() << 8 | input.read());
    }

    public void setRgbCompositeControlPoints(PointF[] points) {
        mRgbCompositeControlPoints = points;
        mRgbCompositeCurve = createSplineCurve(mRgbCompositeControlPoints);
        updateToneCurveTexture();
    }

    public void setRedControlPoints(PointF[] points) {
        mRedControlPoints = points;
        mRedCurve = createSplineCurve(mRedControlPoints);
        updateToneCurveTexture();
    }

    public void setGreenControlPoints(PointF[] points) {
        mGreenControlPoints = points;
        mGreenCurve = createSplineCurve(mGreenControlPoints);
        updateToneCurveTexture();
    }

    public void setBlueControlPoints(PointF[] points) {
        mBlueControlPoints = points;
        mBlueCurve = createSplineCurve(mBlueControlPoints);
        updateToneCurveTexture();
    }

    private void updateToneCurveTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTexture[0]);
        if(mRedCurve!=null && mGreenCurve!=null && mBlueCurve!=null && mRgbCompositeCurve!=null)
        if ((mRedCurve.size() >= 256) && (mGreenCurve.size() >= 256) && (mBlueCurve.size() >= 256) && (mRgbCompositeCurve.size() >= 256)) {
            byte[] toneCurveByteArray = new byte[256 * 4];
            for (int currentCurveIndex = 0; currentCurveIndex < 256; currentCurveIndex++) {
                // BGRA for upload to texture
                toneCurveByteArray[currentCurveIndex * 4 + 2] = (byte) ((int) Math.min(Math.max(currentCurveIndex + mBlueCurve.get(currentCurveIndex) + mRgbCompositeCurve.get(currentCurveIndex), 0), 255) & 0xff);
                toneCurveByteArray[currentCurveIndex * 4 + 1] = (byte) ((int) Math.min(Math.max(currentCurveIndex + mGreenCurve.get(currentCurveIndex) + mRgbCompositeCurve.get(currentCurveIndex), 0), 255) & 0xff);
                toneCurveByteArray[currentCurveIndex * 4] = (byte) ((int) Math.min(Math.max(currentCurveIndex + mRedCurve.get(currentCurveIndex) + mRgbCompositeCurve.get(currentCurveIndex), 0), 255) & 0xff);
                toneCurveByteArray[currentCurveIndex * 4 + 3] = (byte) (255 & 0xff);
            }

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 256, 1, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(toneCurveByteArray));
        }
    }

    private ArrayList<Float> createSplineCurve(PointF[] points) {
        if (points == null || points.length <= 0) {
            return null;
        }

        // Sort the array
        PointF[] pointsSorted = points.clone();
        Arrays.sort(pointsSorted, new Comparator<PointF>() {
            @Override
            public int compare(PointF point1, PointF point2) {
                if (point1.x < point2.x) {
                    return -1;
                } else if (point1.x > point2.x) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        // Convert from (0, 1) to (0, 255).
        Point[] convertedPoints = new Point[pointsSorted.length];
        for (int i = 0; i < points.length; i++) {
            PointF point = pointsSorted[i];
            convertedPoints[i] = new Point((int) (point.x * 255), (int) (point.y * 255));
        }

        ArrayList<Point> splinePoints = createSplineCurve2(convertedPoints);

        // If we have a first point like (0.3, 0) we'll be missing some points at the beginning
        // that should be 0.
        Point firstSplinePoint = splinePoints.get(0);
        if (firstSplinePoint.x > 0) {
            for (int i = firstSplinePoint.x; i >= 0; i--) {
                splinePoints.add(0, new Point(i, 0));
            }
        }

        // Insert points similarly at the end, if necessary.
        Point lastSplinePoint = splinePoints.get(splinePoints.size() - 1);
        if (lastSplinePoint.x < 255) {
            for (int i = lastSplinePoint.x + 1; i <= 255; i++) {
                splinePoints.add(new Point(i, 255));
            }
        }

        // Prepare the spline points.
        ArrayList<Float> preparedSplinePoints = new ArrayList<Float>(splinePoints.size());
        for (Point newPoint : splinePoints) {
            Point origPoint = new Point(newPoint.x, newPoint.x);

            float distance = (float) Math.sqrt(Math.pow((origPoint.x - newPoint.x), 2.0) + Math.pow((origPoint.y - newPoint.y), 2.0));

            if (origPoint.y > newPoint.y) {
                distance = -distance;
            }

            preparedSplinePoints.add(distance);
        }

        return preparedSplinePoints;
    }

    private ArrayList<Point> createSplineCurve2(Point[] points) {
        ArrayList<Double> sdA = createSecondDerivative(points);

        // Is [points count] equal to [sdA count]?
//    int n = [points count];
        int n = sdA.size();
        if (n < 1) {
            return null;
        }
        double sd[] = new double[n];

        // From NSMutableArray to sd[n];
        for (int i = 0; i < n; i++) {
            sd[i] = sdA.get(i);
        }

        ArrayList<Point> output = new ArrayList<Point>(n + 1);

        for (int i = 0; i < n - 1; i++) {
            Point cur = points[i];
            Point next = points[i + 1];

            for (int x = cur.x; x < next.x; x++) {
                double t = (double) (x - cur.x) / (next.x - cur.x);

                double a = 1 - t;
                double b = t;
                double h = next.x - cur.x;

                double y = a * cur.y + b * next.y + (h * h / 6) * ((a * a * a - a) * sd[i] + (b * b * b - b) * sd[i + 1]);

                if (y > 255.0) {
                    y = 255.0;
                } else if (y < 0.0) {
                    y = 0.0;
                }

                output.add(new Point(x, (int) Math.round(y)));
            }
        }

        // If the last point is (255, 255) it doesn't get added.
        if (output.size() == 255) {
            output.add(points[points.length - 1]);
        }
        return output;
    }

    private ArrayList<Double> createSecondDerivative(Point[] points) {
        int n = points.length;
        if (n <= 1) {
            return null;
        }

        double matrix[][] = new double[n][3];
        double result[] = new double[n];
        matrix[0][1] = 1;
        // What about matrix[0][1] and matrix[0][0]? Assuming 0 for now (Brad L.)
        matrix[0][0] = 0;
        matrix[0][2] = 0;

        for (int i = 1; i < n - 1; i++) {
            Point P1 = points[i - 1];
            Point P2 = points[i];
            Point P3 = points[i + 1];

            matrix[i][0] = (double) (P2.x - P1.x) / 6;
            matrix[i][1] = (double) (P3.x - P1.x) / 3;
            matrix[i][2] = (double) (P3.x - P2.x) / 6;
            result[i] = (double) (P3.y - P2.y) / (P3.x - P2.x) - (double) (P2.y - P1.y) / (P2.x - P1.x);
        }

        // What about result[0] and result[n-1]? Assuming 0 for now (Brad L.)
        result[0] = 0;
        result[n - 1] = 0;

        matrix[n - 1][1] = 1;
        // What about matrix[n-1][0] and matrix[n-1][2]? For now, assuming they are 0 (Brad L.)
        matrix[n - 1][0] = 0;
        matrix[n - 1][2] = 0;

        // solving pass1 (up->down)
        for (int i = 1; i < n; i++) {
            double k = matrix[i][0] / matrix[i - 1][1];
            matrix[i][1] -= k * matrix[i - 1][2];
            matrix[i][0] = 0;
            result[i] -= k * result[i - 1];
        }
        // solving pass2 (down->up)
        for (int i = n - 2; i >= 0; i--) {
            double k = matrix[i][2] / matrix[i + 1][1];
            matrix[i][1] -= k * matrix[i + 1][0];
            matrix[i][2] = 0;
            result[i] -= k * result[i + 1];
        }

        ArrayList<Double> output = new ArrayList<Double>(n);
        for (int i = 0; i < n; i++) output.add(result[i] / matrix[i][1]);

        return output;
    }
}
