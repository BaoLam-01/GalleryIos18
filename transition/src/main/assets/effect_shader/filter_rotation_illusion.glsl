precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main() {
    vec2 uv = textureCoordinate;
    //float d = (1.0 - abs(mod(progress,2.0) - 1.0)) * 0.1;
    float dx = cos(progress/0.16) * 0.1;
    float dy = sin(progress/0.16) * 0.1;
    vec4 c0 = texture2D(inputImageTexture,uv);
    vec4 c1 = texture2D(inputImageTexture,uv + vec2(dx,dy));
    vec4 c2 = texture2D(inputImageTexture,uv - vec2(dx,dy));
    gl_FragColor = mix(mix(c0,c1,0.5),c2,1.0/3.0);
}
