precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;

void main() {
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
}
