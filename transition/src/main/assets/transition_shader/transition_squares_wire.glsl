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

// Author: gre
// License: MIT

ivec2 squares= ivec2(10,10);
vec2 direction = vec2(1.0, -0.5);
float smoothness = 1.6;

const vec2 center = vec2(0.5, 0.5);
vec4 transition (vec2 p) {
    vec2 v = normalize(direction);
    v /= abs(v.x)+abs(v.y);
    float d = v.x * center.x + v.y * center.y;
    float offset = smoothness;
    float pr = smoothstep(-offset, 0.0, v.x * p.x + v.y * p.y - (d-0.5+progress*(1.+offset)));
    vec2 squarep = fract(p*vec2(squares));
    vec2 squaremin = vec2(pr/2.0);
    vec2 squaremax = vec2(1.0 - pr/2.0);
    float a = (1.0 - step(progress, 0.0)) * step(squaremin.x, squarep.x) * step(squaremin.y, squarep.y) * step(squarep.x, squaremax.x) * step(squarep.y, squaremax.y);
    return mix(getFromColor(p), getToColor(p), a);
}



void main() {
    gl_FragColor = transition(textureCoordinate);
}
