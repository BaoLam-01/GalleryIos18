precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

void main() {
    vec4 color = texture2D(inputImageTexture, textureCoordinate);
    float r = color.r;
    float g = color.g;
    float b = color.b;

    r = 0.393* r + 0.769 * g + 0.189* b;
    g = 0.349 * r + 0.686 * g + 0.168 * b;
    b = 0.272 * r + 0.534 * g + 0.131 * b;
    gl_FragColor = vec4(r, g, b, progress);
}
