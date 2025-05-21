precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform lowp float progress;
vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return vec4(0.0,0.0,0.0,1.0);
}

// author: gre
// license: MIT

vec4 transition (vec2 uv) {
    return mix(
    getToColor(uv),
    getFromColor(uv),
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
