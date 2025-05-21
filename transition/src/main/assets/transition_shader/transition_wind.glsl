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

float size = 0.2;

float rand (vec2 co) {
    //    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 45.54);
}

vec4 transition (vec2 uv) {
    float r = rand(vec2(0, uv.y));
    float m = smoothstep(0.0, -size, uv.x*(1.0-size) + size*r - (progress * (1.0 + size)));
    return mix(
    getFromColor(uv),
    getToColor(uv),
    m
    );
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
