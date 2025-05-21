precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;


vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}
uniform float zoom_quickness;// = 0.8
//float nQuick = clamp(zoom_quickness,0.2,1.0);
float nQuick = clamp(0.8, 0.2, 1.0);

vec2 zoom(vec2 uv, float amount) {
    return 0.5 + ((uv - 0.5) * (1.0-amount));
}

vec4 transition (vec2 uv) {
    return mix(
    getFromColor(zoom(uv, smoothstep(0.0, nQuick, progress))),
    getToColor(uv),
    smoothstep(nQuick-0.2, 1.0, progress)
    );
}

void main() {
    if(progress>0.0){
        gl_FragColor = transition(textureCoordinate);
    }else{
        gl_FragColor = getFromColor(textureCoordinate);
    }
}
