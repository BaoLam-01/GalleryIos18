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

//uniform float amplitude; // = 30
//uniform float speed; // = 30

vec4 transition(vec2 p) {
    vec2 dir = p - vec2(0.5);
    float dist = length(dir);

    if (dist > progress) {
        return mix(getFromColor(p), getToColor(p), progress);
    } else {
        //        vec2 offset = dir * sin(dist * amplitude - progress * speed);
        vec2 offset = dir * sin(dist * 30.0 - progress * 30.0);
        return mix(getFromColor(p + offset), getToColor(p), progress);
    }
}

void main() {
    if(progress>0.0){
        gl_FragColor = transition(textureCoordinate);
    }else{
        gl_FragColor = getFromColor(textureCoordinate);
    }
}
