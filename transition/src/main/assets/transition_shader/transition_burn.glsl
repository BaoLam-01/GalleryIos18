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

vec3 color  = vec3(0.9, 0.4, 0.2);
vec4 transition (vec2 uv) {
    return mix(
    getFromColor(uv) + vec4(progress*color, 1.0),
    getToColor(uv) + vec4((1.0-progress)*color, 1.0),
    progress
    );
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
