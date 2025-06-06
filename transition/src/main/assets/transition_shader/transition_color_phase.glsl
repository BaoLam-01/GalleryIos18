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

vec4 fromStep = vec4(0.0, 0.2, 0.4, 0.0);
vec4 toStep = vec4(0.6, 0.8, 1.0, 1.0);

vec4 transition (vec2 uv) {
    vec4 a = getFromColor(uv);
    vec4 b = getToColor(uv);
    return mix(a, b, smoothstep(fromStep, toStep, vec4(progress)));
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
