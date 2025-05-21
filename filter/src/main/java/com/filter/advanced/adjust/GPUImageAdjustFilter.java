package com.filter.advanced.adjust;

import android.opengl.GLES20;

import com.filter.base.GPUImageFilter;

public class GPUImageAdjustFilter extends GPUImageFilter {

    /**
     * exposure:    from -10.0 to 10.0, default 0.0
     * brilliance:  from 0.0 to 3.0,    default 1.0
     * brightness:  from -1.0 to 1.0,   default 0.0
     * contrast:    from 0.0 to 4.0,    default 1.0
     */

    public static final String ADJUST_FRAGMENT_SHADER = "\n" +
            "#ifdef GL_ES\n" +
            "#ifdef GL_FRAGMENT_PRECISION_HIGH\n" +
            "precision highp float;\n" +
            "#else\n" +
            "#endif\n" +
            "#endif\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform lowp float exposure;\n" +
            "uniform lowp float brilliance;\n" +
            "uniform lowp float brightness;\n" +
            "uniform lowp float contrast;\n" +
            "\n" +
            "vec4 setExposure(vec4 textureColor, float exposure){\n" +
            "\treturn vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);\n" +
            "}\n" +
            "\n" +
            "vec4 setBrilliance(vec4 textureColor, float brilliance){\n" +
            "\treturn vec4(pow(textureColor.rgb, vec3(brilliance)), textureColor.w);\n" +
            "}\n" +
            "\n" +
            "vec4 setBrightness(vec4 textureColor, float brightness){\n" +
            "\treturn vec4(textureColor.rgb + brightness, textureColor.w);\n" +
            "}\n" +
            "\n" +
            "vec4 setContrast(vec4 textureColor, float contrast){\n" +
            "\treturn vec4(((textureColor.rgb - 0.5) * contrast + 0.5), textureColor.w);\n" +
            "}\n" +
            "\n" +
            "void main(){\n" +
            "\tlowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "\t\n" +
            "\ttextureColor = setExposure(textureColor, exposure);\n" +
            "\n" +
            "\ttextureColor = setBrilliance(textureColor, brilliance)\n" +
            "\n" +
            "\ttextureColor = setBrightness(textureColor, brightness);\n" +
            "\t\n" +
            "\ttextureColor = setContrast(textureColor, contrast);\n" +
            "\n" +
            "\tgl_FragColor = textureColor;\n" +
            "}";

    private int brillianceLocation;
    private float brilliance;

    private int exposureLocation;
    private float exposure;

    private int brightnessLocation;
    private float brightness;

    private int contrastLocation;
    private float contrast;

    public GPUImageAdjustFilter() {
        this(0f, 1f, 0f, 1f);
    }

    public GPUImageAdjustFilter(float exposure, float brilliance, float brightness, float contrast) {
        super(NO_FILTER_VERTEX_SHADER, ADJUST_FRAGMENT_SHADER);
        this.exposure = exposure;
        this.brilliance = brilliance;
        this.brightness = brightness;
        this.contrast = contrast;
    }

    @Override
    public void onInit() {
        super.onInit();
        brightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
        contrastLocation = GLES20.glGetUniformLocation(getProgram(), "contrast");
        exposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
        brillianceLocation = GLES20.glGetUniformLocation(getProgram(), "brilliance");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setBrightness(brightness);
        setContrast(contrast);
        setExposure(exposure);
        setBrilliance(brilliance);
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
        setFloat(brightnessLocation, brightness);
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
        setFloat(contrastLocation, contrast);
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
        setFloat(exposureLocation, exposure);
    }

    public void setBrilliance(float brilliance) {
        this.brilliance = brilliance;
        setFloat(brillianceLocation, brilliance);
    }
}
