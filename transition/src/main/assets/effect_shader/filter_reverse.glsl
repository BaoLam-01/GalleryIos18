precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;

void main()
{
    // dao nguoc x y
    vec2 pos = textureCoordinate.xy;
    pos.x= 1.0- pos.x;
    pos.y= 1.0- pos.y;
    gl_FragColor = texture2D(inputImageTexture, pos);
}

