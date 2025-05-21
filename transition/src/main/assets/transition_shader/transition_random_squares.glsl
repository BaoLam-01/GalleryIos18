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

ivec2 size = ivec2(10, 10);
float smoothness = 0.5;

float rand (vec2 co) {
//    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 45.54);
}

vec4 transition(vec2 p) {
    float r = rand(floor(vec2(size) * p));
    float m = smoothstep(0.0, -smoothness, r - (progress * (1.0 + smoothness)));
    return mix(getFromColor(p), getToColor(p), m);
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
