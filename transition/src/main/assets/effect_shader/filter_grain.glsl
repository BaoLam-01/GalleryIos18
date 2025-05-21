precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform lowp float progress;


void main()
{
    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    float x = (textureCoordinate.x + 4.0) * (textureCoordinate.y + 4.0)  * 10.0;
    vec4 grainFactor = vec4(mod((mod(x, 13.0) + 1.0) * (mod(x, 123.0) + 1.0), 0.01)-0.005) * progress * 200.0;
    gl_FragColor = textureColor + grainFactor * 0.18;

}