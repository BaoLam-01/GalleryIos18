precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;

uniform lowp float progress;

vec2 direction = vec2(0.0, -1.0);

void main() {
    vec2 p = textureCoordinate + progress * sign(direction);
    vec2 f = fract(p);

    lowp vec4 textureColor = texture2D(inputImageTexture, f);
    lowp vec4 textureColor2 = texture2D(inputImageTexture2, f);

    gl_FragColor = mix(textureColor2, textureColor, step(0.0, p.y) * step(p.y, 1.0) * step(0.0, p.x) * step(p.x, 1.0));
}
