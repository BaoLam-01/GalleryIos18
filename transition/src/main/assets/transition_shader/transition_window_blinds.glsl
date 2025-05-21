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
    float t = progress;

    if (mod(floor(uv.y*100.*progress),2.)==0.)
    t*=2.-.5;

    return mix(
    getFromColor(uv),
    getToColor(uv),
    mix(t, progress, smoothstep(0.8, 1.0, progress))
    );
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
