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

float size = 0.04;
float zoom = 50.0;
float colorSeparation = 0.3;

vec4 transition(vec2 p) {
    float inv = 1. - progress;
    vec2 disp = size*vec2(cos(zoom*p.x), sin(zoom*p.y));
    vec4 texTo = getToColor(p + inv*disp);
    vec4 texFrom = vec4(
    getFromColor(p + progress*disp*(1.0 - colorSeparation)).r,
    getFromColor(p + progress*disp).g,
    getFromColor(p + progress*disp*(1.0 + colorSeparation)).b,
    1.0);
    return texTo*progress + texFrom*inv;
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
