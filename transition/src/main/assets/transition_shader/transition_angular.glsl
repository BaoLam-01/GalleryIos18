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

    #define PI 3.141592653589

float startingAngle = 90.0;

vec4 transition (vec2 uv) {

    if (progress>0.0){
        float offset = startingAngle * PI / 180.0;
        float angle = atan(uv.y - 0.5, uv.x - 0.5) + offset;
        float normalizedAngle = (angle + PI) / (2.0 * PI);

        normalizedAngle = normalizedAngle - floor(normalizedAngle);

        return mix(
        getFromColor(uv),
        getToColor(uv),
        step(normalizedAngle, progress)
        );
    } else {
        return getFromColor(uv);
    }
}



void main() {
    gl_FragColor = transition(textureCoordinate);
}
