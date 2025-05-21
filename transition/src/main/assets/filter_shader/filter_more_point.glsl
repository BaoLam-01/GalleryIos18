precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main()
{
    float rate= 1000.0/1369.0;
    float cellX= 1.0;
    float cellY=1.0;
    float rowCount=100.0;
    float space =4.0;

    vec2 sizeFmt=vec2(rowCount, rowCount/rate);
    vec2 sizeMsk=vec2(cellX, cellY/rate);
    vec2 posFmt = vec2(textureCoordinate.x*sizeFmt.x, textureCoordinate.y*sizeFmt.y);
    vec2 posMsk = vec2(floor(posFmt.x/sizeMsk.x)*sizeMsk.x, floor(posFmt.y/sizeMsk.y)*sizeMsk.y)+ progress*sizeMsk;

    float del = length(posMsk - posFmt);
    vec2 UVMosaic = vec2(posMsk.x/sizeFmt.x, posMsk.y/sizeFmt.y);

    vec4 result;
    if (del< cellX/space)
    result=vec4(0.2,0.2,0.2,0.1);
    else
    result = texture2D(inputImageTexture, textureCoordinate);
    gl_FragColor = vec4(result.g,result.g,result.g,1.0);
}

