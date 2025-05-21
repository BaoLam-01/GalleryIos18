precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
vec4 repeat(){
    vec2 repeatCount = vec2(3.0);
    float percentWidth,percentHeight, resultX ,resultY;
    int  indexX,indexY ;
    percentWidth = 1.0/repeatCount.x;
    indexX = int(textureCoordinate.x / percentWidth);
    resultX = (textureCoordinate.x - float(indexX) * percentWidth)/percentWidth;
    percentHeight = 1.0/repeatCount.y;
    indexY = int(textureCoordinate.y / percentHeight);
    resultY = (textureCoordinate.y - float(indexY) * percentHeight)/percentHeight;
    return texture2D(inputImageTexture, vec2(resultX, resultY));
}
void main()
{     gl_FragColor =repeat();
}