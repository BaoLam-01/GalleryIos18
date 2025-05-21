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

float a= 4.0;
float b= 1.0;
float amplitude= 120.0;
float smoothness= 0.1;

vec4 transition(vec2 uv) {
    vec2 p = uv.xy / vec2(1.0).xy;
    vec2 dir = p - vec2(.5);
    float dist = length(dir);
    float x = (a - b) * cos(progress) + b * cos(progress * ((a / b) - 1.));
    float y = (a - b) * sin(progress) - b * sin(progress * ((a / b) - 1.));
    vec2 offset = dir * vec2(sin(progress  * dist * amplitude * x), sin(progress * dist * amplitude * y)) / smoothness;
    return mix(getFromColor(p + offset), getToColor(p), smoothstep(0.2, 1.0, progress));
}
void main() {
    gl_FragColor = transition(textureCoordinate);
}
