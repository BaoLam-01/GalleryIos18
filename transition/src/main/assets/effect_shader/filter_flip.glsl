precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;

uniform sampler2D inputImageTexture;
vec4 getFromColor(vec2 uv){
    vec2 pos = textureCoordinate;
    pos.x = 1.0 - pos.x;
    return texture2D(inputImageTexture, pos);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

// author: gre
// license: MIT

vec4 transition (vec2 uv) {
    return mix(
    getFromColor(uv),
    getToColor(uv),
    progress
    );
}

void main() {
    if(progress>0.0){
        gl_FragColor = transition(textureCoordinate);
    }else{
        gl_FragColor = getFromColor(textureCoordinate);
    }
}