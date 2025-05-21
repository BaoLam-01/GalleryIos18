precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;

uniform lowp float progress;

void main() {
    //    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    //    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate);
    //
    //    lowp float p = 0.0;
    //    if (progress <= 0.5){
    //        p = 0.0;
    //    } else {
    //        p = 1.0;
    //    }
    //    gl_FragColor =  mix(textureColor, textureColor2, p);

    if (progress <= 0.5){
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    } else {
        gl_FragColor = texture2D(inputImageTexture2, textureCoordinate);
    }
}
