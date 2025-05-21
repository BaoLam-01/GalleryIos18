precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main()
{
    float centerMultiplier = 0.5;
    vec2 topTextureCoordinate =vec2(0.0,0.0);
    vec2 rightTextureCoordinate =vec2(1.0,0.0);
    vec2 leftTextureCoordinate =vec2(0.0,1.0);
    vec2 bottomTextureCoordinate =vec2(1.0,1.0);
    mediump vec3 textureColor = texture2D(inputImageTexture, textureCoordinate).rgb;
    mediump vec3 leftTextureColor = texture2D(inputImageTexture, leftTextureCoordinate).rgb;
    mediump vec3 rightTextureColor = texture2D(inputImageTexture, rightTextureCoordinate).rgb;
    mediump vec3 topTextureColor = texture2D(inputImageTexture, topTextureCoordinate).rgb;
    mediump vec3 bottomTextureColor = texture2D(inputImageTexture, bottomTextureCoordinate).rgb;

    gl_FragColor = vec4((textureColor * centerMultiplier - (leftTextureColor * progress + rightTextureColor * progress + topTextureColor * progress + bottomTextureColor * progress)), texture2D(inputImageTexture, bottomTextureCoordinate).w);
}
