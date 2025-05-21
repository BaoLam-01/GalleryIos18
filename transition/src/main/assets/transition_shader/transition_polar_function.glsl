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

#define PI 3.14159265359

int segments = 5;

vec4 transition (vec2 uv) {

    float angle = atan(uv.y - 0.5, uv.x - 0.5) - 1.5 * PI;
    float normalized = (angle + 1.5 * PI) * (2.0 * PI);

    float radius = (cos(float(segments) * angle) + 4.0) / 4.0;
    float difference = length(uv - vec2(0.5, 0.5));

    if (difference > radius * progress)
    return getFromColor(uv);
    else
    return getToColor(uv);
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
