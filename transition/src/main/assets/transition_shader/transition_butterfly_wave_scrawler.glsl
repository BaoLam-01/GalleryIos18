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

// Author: mandubian
// License: MIT
float amplitude = 1.0;
float waves = 30.0;
float colorSeparation = 0.3;
float PI = 3.14159265358979323846264;
float compute(vec2 p, float progress, vec2 center) {
    vec2 o = p*sin(progress * amplitude)-center;
    // horizontal vector
    vec2 h = vec2(1., 0.);
    // butterfly polar function (don't ask me why this one :))
    float theta = acos(dot(o, h)) * waves;
    return (exp(cos(theta)) - 2.*cos(4.*theta) + pow(sin((2.*theta - PI) / 24.), 5.)) / 10.;
}
vec4 transition(vec2 uv) {
    vec2 p = uv.xy / vec2(1.0).xy;
    float inv = 1. - progress;
    vec2 dir = p - vec2(.5);
    float dist = length(dir);
    float disp = compute(p, progress, vec2(0.5, 0.5)) ;
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
