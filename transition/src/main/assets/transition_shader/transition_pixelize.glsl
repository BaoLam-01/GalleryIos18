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

ivec2 squaresMin= ivec2(20); // minimum number of squares (when the effect is at its higher level)
float steps  = 50.0; // zero disable the stepping

vec4 transition(vec2 uv) {
    float d = min(progress, 1.0 - progress);
    float dist = steps>0.0 ? ceil(d *steps) / steps : d;
    vec2 squareSize = 2.0 * dist / vec2(squaresMin);
    vec2 p = dist>0.0 ? (floor(uv / squareSize) + 0.5) * squareSize : uv;
    return mix(getFromColor(p), getToColor(p), progress);
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
