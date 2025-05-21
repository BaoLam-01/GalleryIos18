precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform float progress;

void main()
{
    float arg = 3.0;

    vec4 color= texture2D(inputImageTexture, textureCoordinate);

    float r = color.r;
    float g = color.g;
    float b = color.b;

    b = sqrt(b)*arg;
    if (b>1.0) b = 1.0;

    gl_FragColor = vec4(r, g, b, progress);
}

