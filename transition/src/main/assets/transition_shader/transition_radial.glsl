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

float smoothness=1.0; // = 1.0

const float PI = 3.141592653589;

vec4 transition(vec2 p) {
    vec2 rp = p*2.-1.;
    return mix(
    getToColor(p),
    getFromColor(p),
    smoothstep(0., smoothness, atan(rp.y,rp.x) - (progress-.5) * PI * 2.5)
    );
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
