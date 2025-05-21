precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main() {
    vec2 uv = textureCoordinate;
    float distance = 1.0 / 3.0;
    if (uv.y >= 0.0 && uv.y <= distance) {
        uv.y = uv.y + distance;
    } else if (uv.y >= 2.0 * distance) {
        uv.y = uv.y - distance;
    } else {
        uv.y = uv.y;
        uv.x = uv.x;
    }
    gl_FragColor = texture2D(inputImageTexture, uv);
}
