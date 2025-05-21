precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform lowp float progress;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}

float power=5.0; // = 5.0

vec4 transition(vec2 p) {
    vec4 fTex = getFromColor(p);
    vec4 tTex = getToColor(p);
    float m = step(distance(fTex, tTex), progress);
    return mix(
    mix(fTex, tTex, m),
    tTex,
    pow(progress, power)
    );
}
void main() {
    gl_FragColor = transition(textureCoordinate);
}
