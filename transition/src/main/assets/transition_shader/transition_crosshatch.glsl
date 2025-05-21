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
vec2 center= vec2(0.5);
float threshold = 3.0;
float fadeEdge = 0.1;

float rand(vec2 co) {
//    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.54);
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 45.54);
}
vec4 transition(vec2 p) {
    float dist = distance(center, p) / threshold;
    float r = progress - min(rand(vec2(p.y, 0.0)), rand(vec2(0.0, p.x)));
    return mix(getFromColor(p), getToColor(p), mix(0.0, mix(step(dist, r), 1.0, smoothstep(1.0-fadeEdge, 1.0, progress)), smoothstep(0.0, fadeEdge, progress)));
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
