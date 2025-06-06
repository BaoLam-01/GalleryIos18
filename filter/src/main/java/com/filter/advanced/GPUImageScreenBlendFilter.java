/**
 * Created by Matthew Stewart on 10/30/2017 10:46:59 AM
 */
package com.filter.advanced;

public class GPUImageScreenBlendFilter extends GPUImageTwoInputFilter {
    public static final String SCREEN_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n varying highp vec2 textureCoordinate2;" +
            "\n\n uniform sampler2D inputImageTexture;\n uniform sampler2D inputImageTexture2;\n \n void main()\n {\n   " +
            "  mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n   " +
            "  mediump vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n  " +
            "   mediump vec4 whiteColor = vec4(1.0);\n   " +
            "  gl_FragColor = whiteColor - ((whiteColor - textureColor2) * (whiteColor - textureColor));\n }";

    public GPUImageScreenBlendFilter() {
        super(SCREEN_BLEND_FRAGMENT_SHADER);
    }
}
