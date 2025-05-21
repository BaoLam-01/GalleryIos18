precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main() {
    vec2 uv = textureCoordinate;
    if (uv.y >= 0.0 && uv.y <= 0.5) {
        uv.y = uv.y + 0.25;
        uv.x = uv.x;
    } else {
        uv.y = uv.y - 0.25;
        uv.x = uv.x;
    }
    gl_FragColor = texture2D(inputImageTexture, uv);
}