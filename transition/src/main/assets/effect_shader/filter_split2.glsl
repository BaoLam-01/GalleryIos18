precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

float reverse(float current){
    return 1.0 - current;
}

void main() {
    vec2 uv = textureCoordinate;
    if (uv.x >= 0.0 && uv.x <= 0.5) {
        uv.x = reverse(uv.x + 0.25);
    } else {
        uv.x = uv.x - 0.25;
    }
    gl_FragColor = texture2D(inputImageTexture, uv);
}
