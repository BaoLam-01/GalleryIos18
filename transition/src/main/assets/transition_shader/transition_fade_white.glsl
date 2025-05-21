precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

float SPEED = 1.0;//0 - 1 speed
float brightness=1.0;
float contrast=1.0;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    return vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)) + vec3(brightness), textureColor.w);
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
