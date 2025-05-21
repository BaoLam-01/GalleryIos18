precision mediump float;

varying highp vec2 textureCoordinate;
uniform lowp sampler2D inputImageTexture;

vec2 center=vec2(0.5, 0.5);
float scale=0.5;
uniform float progress;

void main() {
    highp vec2 textureCoordinateToUse = textureCoordinate;
    highp float dist = distance(center, textureCoordinate);
    textureCoordinateToUse -= center;
    if (dist < progress) {
        highp float percent = 1.0 - ((progress - dist) / progress) * scale;
        percent = percent * percent;
        textureCoordinateToUse = textureCoordinateToUse * percent;
    }
    textureCoordinateToUse+= center;

    gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse);
}