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

    #define PI 3.14159265359

vec2 center = vec2(0.5, 0.5);
float rotations = 1.0;
float scale = 5.0;
vec4 backColor = vec4(0.15, 0.15, 0.15, 1.0);

vec4 transition (vec2 uv) {

    vec2 difference = uv - center;
    vec2 dir = normalize(difference);
    float dist = length(difference);

    float angle = 2.0 * PI * rotations * progress;

    float c = cos(angle);
    float s = sin(angle);

    float currentScale = mix(scale, 1.0, 2.0 * abs(progress - 0.5));

    vec2 rotatedDir = vec2(dir.x  * c - dir.y * s, dir.x * s + dir.y * c);
    vec2 rotatedUv = center + rotatedDir * dist / currentScale;

    if (rotatedUv.x < 0.0 || rotatedUv.x > 1.0 ||
    rotatedUv.y < 0.0 || rotatedUv.y > 1.0)
    return backColor;

    return mix(getFromColor(rotatedUv), getToColor(rotatedUv), progress);
}



void main() {
    if(progress>0.0){
        gl_FragColor = transition(textureCoordinate);
    }else{
        gl_FragColor = getFromColor(textureCoordinate);
    }
}
