precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTextureOverlay;

void main()
{
    lowp vec4 c2 = texture2D(inputImageTexture, textureCoordinate);
    lowp vec4 c1 = texture2D(inputImageTextureOverlay, textureCoordinate);
    lowp vec4 outputColor;
    outputColor.r = c1.r + c2.r * c2.a * (1.0 - c1.a);

    outputColor.g = c1.g + c2.g * c2.a * (1.0 - c1.a);
    outputColor.b = c1.b + c2.b * c2.a * (1.0 - c1.a);
    outputColor.a = c1.a + c2.a * (1.0 - c1.a);
    gl_FragColor = outputColor;
}
