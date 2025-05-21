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

vec4 blend(vec4 a, vec4 b) {
    return a * b;
}

vec4 transition (vec2 uv) {

    vec4 blended = blend(getFromColor(uv), getToColor(uv));

    if (progress < 0.5)
    return mix(getFromColor(uv), blended, 2.0 * progress);
    else
    return mix(blended, getToColor(uv), 2.0 * progress - 1.0);
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
