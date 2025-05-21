precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main() {
    vec2 uv = textureCoordinate;
    float distance = 1.0 / 3.0;
    if (uv.x >= 0.0 && uv.x <= distance) {
        uv.x = uv.x + distance;
    } else if (uv.x >= 2.0 * distance) {
        uv.x = uv.x - distance;
    }
    gl_FragColor = texture2D(inputImageTexture, uv);
}
