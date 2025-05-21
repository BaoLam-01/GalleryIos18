precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;

float u_rotateAngle=0.5;//通过旋转角度控制形变的程度
uniform lowp float progress;


vec2 rotate(float radius, float angle, vec2 texSize, vec2 texCoord)
{
    vec2 newTexCoord = texCoord;
    vec2 center = vec2(texSize.x / 2.0, texSize.y / 2.0);
    vec2 tc = texCoord * texSize;
    tc -= center;
    float dist = length(tc);
    if (dist < radius) {
        float percent = (radius - dist) / radius;
        float theta = percent * percent * angle * 8.0;
        float s = sin(theta);
        float c = cos(theta);
        tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
        tc += center;

        newTexCoord = tc / texSize;
    }
    return newTexCoord;
}
void main()
{
    vec2 texCoord = textureCoordinate;
    vec2 u_texSize = vec2(progress+0.5);//图像分辨率

    if(u_rotateAngle > 0.0)
    {
        texCoord = rotate(0.2, u_rotateAngle, u_texSize, textureCoordinate);
    }
    gl_FragColor = texture2D(inputImageTexture, texCoord);
    if (gl_FragColor.a < 0.6) discard;
}

