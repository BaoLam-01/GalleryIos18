precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;


void main(){
    vec2 uv = textureCoordinate;
    float waveu = sin((uv.y + progress) * 20.0) * 0.015;
    gl_FragColor = texture2D(inputImageTexture, uv + vec2(waveu, 0));
}
