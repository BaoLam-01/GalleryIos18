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

float amplitude = 100.0;
float speed = 50.0;

vec4 transition (vec2 uv) {
    vec2 dir = uv - vec2(.5);
    float dist = length(dir);
    vec2 offset = dir * (sin(progress * dist * amplitude - progress * speed) + .5) / 30.;
    return mix(
    getFromColor(uv + offset),
    getToColor(uv),
    smoothstep(0.2, 1.0, progress)
    );
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
