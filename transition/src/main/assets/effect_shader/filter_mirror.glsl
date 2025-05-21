precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;


void main(){
    vec2 pos = textureCoordinate;
    if (pos.x > 0.5) {
        pos.x = 1.0 - pos.x;
    }
    gl_FragColor = texture2D(inputImageTexture, pos);
}
