package com.filter.base;

public class GILookUpFilter extends GPUImageFilter{

    public static final String LOOKUP_FRAGMENT_SHADER =
            "uniform mediump sampler2D lutTexture; \n" +
                    "uniform lowp sampler2D sTexture; \n" +
                    "varying highp vec2 vTextureCoord; \n" +
                    " uniform lowp float intensity;\n" +
                    " void main()\n" +
                    " {\n" +
                    "     lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
                    "     \n" +
                    "     mediump float blueColor = textureColor.b * 63.0;\n" +
                    "     \n" +
                    "     mediump vec2 quad1;\n" +
                    "     quad1.y = floor(floor(blueColor) / 8.0);\n" +
                    "     quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
                    "     \n" +
                    "     mediump vec2 quad2;\n" +
                    "     quad2.y = floor(ceil(blueColor) / 8.0);\n" +
                    "     quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +
                    "     \n" +
                    "     highp vec2 texPos1;\n" +
                    "     texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                    "     texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                    "     \n" +
                    "     highp vec2 texPos2;\n" +
                    "     texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                    "     texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                    "     \n" +
                    "     lowp vec4 newColor1 = texture2D(lutTexture, texPos1);\n" +
                    "     lowp vec4 newColor2 = texture2D(lutTexture, texPos2);\n" +
                    "     \n" +
                    "     lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
//                    "     gl_FragColor = vec4(newColor.rgb, textureColor.w);\n" +
                    "     gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), intensity);\n" +
                    " }";


}
