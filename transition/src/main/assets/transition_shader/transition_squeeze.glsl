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

float colorSeparation = 0.04;

vec4 transition (vec2 uv) {
    float y = 0.5 + (uv.y-0.5) / (1.0-progress);
    if (y < 0.0 || y > 1.0) {
        return getToColor(uv);
    }
    else {
        vec2 fp = vec2(uv.x, y);
        vec2 off = progress * vec2(0.0, colorSeparation);
        vec4 c = getFromColor(fp);
        vec4 cn = getFromColor(fp - off);
        vec4 cp = getFromColor(fp + off);
        return vec4(cn.r, c.g, cp.b, c.a);
    }
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
