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

float speed = 2.0;

vec4 transition(vec2 uv) {

    vec2 p = uv.xy / vec2(1.0).xy;

    float circPos = atan(p.y - 0.5, p.x - 0.5) + progress * speed;
    float modPos = mod(circPos, 3.1415 / 4.);
    float signed = sign(progress - modPos);

    return mix(getToColor(p), getFromColor(p), step(signed, 0.5));

}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
