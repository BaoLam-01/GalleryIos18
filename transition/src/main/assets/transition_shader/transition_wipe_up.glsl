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
    vec2 p=uv.xy/vec2(1.0).xy;
    vec4 a=getFromColor(p);
    vec4 b=getToColor(p);
    return mix(a, b, step(0.0+p.y,progress));
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
