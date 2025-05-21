precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;

uniform lowp float progress;

void main() {

    float count = 10.0;// = 10.0
    float smoothness = 0.5;// = 0.5


    float pr = smoothstep(-smoothness, 0.0, textureCoordinate.x - progress * (1.0 + smoothness));
    float s = step(pr, fract(count * textureCoordinate.x));

    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate);

    gl_FragColor = mix(textureColor, textureColor2, s);
    //    gl_FragColor = vec4((textureColor.rgb + vec3(0.2)), textureColor.w);
}
