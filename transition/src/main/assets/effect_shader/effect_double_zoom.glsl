precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;
uniform sampler2D inputImageTexture;
vec3 iResolution = vec3(1.0);

const float PI = 3.1415926;

void main() {
    float circleTime = 0.7;
    float maxHeight = 0.5;
    float param = mod(progress, circleTime);
    // scale ratio
    float scale = 0.4 + maxHeight*abs(sin(param*(PI/circleTime)));

    vec2 uv = (textureCoordinate)/iResolution.xy - 0.5;
    vec2 finalUv = uv;
    finalUv = 0.5 + uv * scale;

    vec3 color = texture2D(inputImageTexture,finalUv).rgb;

    gl_FragColor = vec4(color, 1.0);
}
