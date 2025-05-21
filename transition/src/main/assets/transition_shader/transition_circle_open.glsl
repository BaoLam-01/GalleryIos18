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

float smoothness = 0.0;
bool opening = true;

const vec2 center = vec2(0.5, 0.5);
const float SQRT_2 = 1.414213562373;

vec4 transition (vec2 uv) {
    float x = opening ? progress : 1.-progress;
    float m = smoothstep(-smoothness, 0.0, SQRT_2*distance(center, uv) - x*(1.+smoothness));
    return mix(getFromColor(uv), getToColor(uv), opening ? 1.-m : m);
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
