/**
 * Created by Matthew Stewart on 10/30/2017 10:47:01 AM
 */
package com.filter.advanced.adjust;

import android.opengl.GLES20;

import com.filter.base.GPUImageFilter;

public class GPUImageHighlightBlackPointFilter extends GPUImageFilter {
    public static final String HIGHLIGHT_BlackPoint_FRAGMENT_SHADER = "" +
            "uniform sampler2D inputImageTexture;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform lowp float shadows;\n" +
            "uniform lowp float highlights;\n" +
            "\n" +
            "const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    mediump float luminance = dot(source.rgb, luminanceWeighting);\n" +
            "\n" +
            "    //(shadows+1.0) changed to just shadows:\n" +
            "    mediump float shadow = clamp((pow(luminance, 1.0/shadows) + (-0.76)*pow(luminance, 2.0/shadows)) - luminance, 0.0, 1.0);\n" +
            "    mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);\n" +
            "    lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
            "\n" +
            "    // blend toward white if highlights is more than 1\n" +
            "    mediump float contrastedLuminance = ((luminance - 0.5) * 1.5) + 0.5;\n" +
            "    mediump float whiteInterp = contrastedLuminance*contrastedLuminance*contrastedLuminance;\n" +
            "    mediump float whiteTarget = clamp(highlights, 1.0, 2.0) - 1.0;\n" +
            "    result = mix(result, vec3(1.0), whiteInterp*whiteTarget);\n" +
            "\n" +
            "    // blend toward black if shadows is less than 1\n" +
            "    mediump float invContrastedLuminance = 1.0 - contrastedLuminance;\n" +
            "    mediump float blackInterp = invContrastedLuminance*invContrastedLuminance*invContrastedLuminance;\n" +
            "    mediump float blackTarget = 1.0 - clamp(shadows, 0.0, 1.0);\n" +
            "    result = mix(result, vec3(0.0), blackInterp*blackTarget);\n" +
            "\n" +
            "    gl_FragColor = vec4(result, source.a);\n" +
            "}";

    private int mBlackPointsLocation;
    private float mBlackPoints;
    private int mHighlightsLocation;
    private float mHighlights;

    public GPUImageHighlightBlackPointFilter() {
        this(1.0f, 1.0f);
    }

    public GPUImageHighlightBlackPointFilter(final float blackPoints, final float highlights) {
        super(NO_FILTER_VERTEX_SHADER, HIGHLIGHT_BlackPoint_FRAGMENT_SHADER);
        mHighlights = highlights;
        mBlackPoints = blackPoints;
    }

    @Override
    public void onInit() {
        super.onInit();
        mHighlightsLocation = GLES20.glGetUniformLocation(getProgram(), "highlights");
        mBlackPointsLocation = GLES20.glGetUniformLocation(getProgram(), "shadows");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setHighlights(mHighlights);
        setBlackPoint(mBlackPoints);
    }

    public void setHighlights(final float highlights) {
        mHighlights = highlights;
        setFloat(mHighlightsLocation, mHighlights);
    }

    public void setBlackPoint(final float shadows) {
        mBlackPoints = shadows;
        setFloat(mBlackPointsLocation, mBlackPoints);
    }

    public float getBlackPoints() {
        return mBlackPoints;
    }

    public float getHighlights() {
        return mHighlights;
    }
}
