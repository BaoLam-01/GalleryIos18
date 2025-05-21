precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main(){
    float duration = 0.8;
    float progressR = mod(progress - 1.0, duration) / duration; // 0~1
    float progressG = mod(progress, duration) / duration; // 0~1
    float progressB = mod(progress + 1.0, duration) / duration; // 0~1

    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    gl_FragColor.r = gl_FragColor.r * progressR;
    gl_FragColor.g = gl_FragColor.g * progressG;
    gl_FragColor.b = gl_FragColor.b * progressB;
}
