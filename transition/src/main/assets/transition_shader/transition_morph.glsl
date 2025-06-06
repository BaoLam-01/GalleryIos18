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
float strength=0.1;// = 0.1

vec4 transition(vec2 p) {
    vec4 ca = getFromColor(p);
    vec4 cb = getToColor(p);

    vec2 oa = (((ca.rg+ca.b)*0.5)*2.0-1.0);
    vec2 ob = (((cb.rg+cb.b)*0.5)*2.0-1.0);
    vec2 oc = mix(oa, ob, 0.5)*strength;

    float w0 = progress;
    float w1 = 1.0-w0;
    return mix(getFromColor(p+oc*w0), getToColor(p-oc*w1), progress);
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
