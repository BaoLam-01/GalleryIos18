precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;


void main(){
    vec4  color = texture2D(inputImageTexture, textureCoordinate);
    float r = progress - color.r;
    float g = progress - color.g;
    float b = progress - color.b;
    gl_FragColor = vec4(r, g, b, 1.0);
}
