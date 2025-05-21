precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main() {
    vec2 uv = textureCoordinate;
    float start = 1.0 / 6.0;
    float ratio = 3.0 / 2.0;
    if (uv.x <= 1.0 / 3.0){
        uv.x = uv.x * 3.0 / ratio  + start;
        if (uv.y <= 1.0 / 2.0){
            uv.y = uv.y * 2.0;
        } else {
            uv.x = uv.x;
            uv.y = (uv.y-0.5) * 2.0;
        }
    } else if (uv.x > 1.0 / 3.0 && uv.x <= 2.0 / 3.0){
        uv.x = (uv.x - 1.0/3.0) * 3.0 / ratio  + start;
        if (uv.y <= 1.0 / 2.0){
            uv.y = uv.y * 2.0;
            uv.x = uv.x;
        } else {
            uv.y = (uv.y-0.5) * 2.0;
        }
    } else {
        uv.x = (uv.x - 2.0/3.0) * 3.0 / ratio  + start;
        if (uv.y <= 1.0 / 2.0){
            uv.y = uv.y * 2.0;
        } else {
            uv.x = uv.x;
            uv.y = (uv.y-0.5) * 2.0;
        }
    }
    gl_FragColor = texture2D(inputImageTexture, uv);
}
