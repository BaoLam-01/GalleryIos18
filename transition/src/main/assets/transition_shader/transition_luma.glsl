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

vec4 transition(vec2 uv) {
    return mix(
    getToColor(uv),
    getFromColor(uv),
    step(progress, texture2D(inputImageTexture2, uv).r)
    );
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
