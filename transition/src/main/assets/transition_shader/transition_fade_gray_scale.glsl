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

// if 0.0, the image directly turn grayscale, if 0.9, the grayscale transition phase is very important
float intensity = 0.3;

vec3 grayscale (vec3 color) {
    return vec3(0.2126*color.r + 0.7152*color.g + 0.0722*color.b);
}

vec4 transition (vec2 uv) {
    vec4 fc = getFromColor(uv);
    vec4 tc = getToColor(uv);
    return mix(
    mix(vec4(grayscale(fc.rgb), 1.0), fc, smoothstep(1.0-intensity, 0.0, progress)),
    mix(vec4(grayscale(tc.rgb), 1.0), tc, smoothstep(    intensity, 1.0, progress)),
    progress);
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
