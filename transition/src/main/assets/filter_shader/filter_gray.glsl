precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main()
{
//    mediump float d = .0075;
    mediump float d = progress/100.0;

    gl_FragColor = vec4( texture2D(inputImageTexture,textureCoordinate-d).x,
    texture2D(inputImageTexture,textureCoordinate  ).x,
    texture2D(inputImageTexture,textureCoordinate+d).x,
    1);
}
