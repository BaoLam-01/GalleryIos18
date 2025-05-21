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

vec4 transition (vec2 uv) {
    if(progress == 0.0){
        return getFromColor(uv);
    }
    vec2 leaf_uv = (uv - vec2(0.5))/10./pow(progress,3.5);
    leaf_uv.y += 0.35;
    float r = 0.18;
    float o = atan(leaf_uv.y, leaf_uv.x);
    return mix(getFromColor(uv), getToColor(uv), 1.-step(1. - length(leaf_uv)+r*(1.+sin(o))*(1.+0.9 * cos(8.*o))*(1.+0.1*cos(24.*o))*(0.9+0.05*cos(200.*o)), 1.));
}
void main() {
    gl_FragColor = transition(textureCoordinate);
}
