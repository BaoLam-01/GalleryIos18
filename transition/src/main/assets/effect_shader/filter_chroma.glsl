precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;


void main()
{
    float amount = 0.0;
    amount = (1.0 + sin(progress*6.0)) * 0.5;
    amount *= 1.0 + sin(progress*16.0) * 0.5;
    amount *= 1.0 + sin(progress*19.0) * 0.5;
    amount *= 1.0 + sin(progress*27.0) * 0.5;
    amount = pow(amount, 3.0);
    amount *= 0.05;
    vec3 col;
    col.r = texture2D( inputImageTexture, vec2(textureCoordinate.x+amount,textureCoordinate.y) ).r;
    col.g = texture2D( inputImageTexture, textureCoordinate ).g;
    col.b = texture2D( inputImageTexture, vec2(textureCoordinate.x-amount,textureCoordinate.y) ).b;
    col *= (1.0 - amount * 0.5);
    gl_FragColor = vec4(col, 1.);
}
