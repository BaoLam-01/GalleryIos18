precision mediump float;

varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform float progress;
vec2 playerResolution = vec2(1.0);

vec3 dipColor = vec3(1.0);

void main(void) {
    vec4 p = texture2D(inputImageTexture, textureCoordinate);
    vec4 n = texture2D(inputImageTexture2, textureCoordinate);
    vec4 c = vec4(dipColor, 1.0);

    vec4 result;
    if (progress < 0.5) {
        result = mix(p, c, (progress * 2.0));
    } else {
        result = mix(c, n, (progress * 2.0 - 1.0));
    }

    gl_FragColor = result;
}