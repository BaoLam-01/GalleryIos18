precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main()
{
    vec4 color= texture2D(inputImageTexture, textureCoordinate);
    float r = color.r;
    float g = color.g;
    float b = color.b;

    g = r * 0.3 + g * 0.59 + b * 0.11;
    g= g <= progress ? 0.0 : 1.0;

    gl_FragColor = vec4(g, g, g, 1.0);
}

