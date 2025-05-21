precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform lowp float progress;

//uniform float strength; // = 0.5

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}

vec4 transition (vec2 uv) {
    float displacement = texture2D(inputImageTexture2, uv).r * 0.5;

    vec2 uvFrom = vec2(uv.x + progress * displacement, uv.y);
    vec2 uvTo = vec2(uv.x - (1.0 - progress) * displacement, uv.y);

    return mix(
    getFromColor(uvFrom),
    getToColor(uvTo),
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
