precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;
uniform sampler2D inputImageTexture;
vec3 iResolution = vec3(1.0);
#define FactorA vec2(100.0, 100.0)
#define FactorB vec2(1.0, 1.0)
#define FactorScale vec2(0.01, 0.01)

void main() {
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = textureCoordinate/iResolution.xy;
    vec2 uniA = FactorA;
    vec2 uniB = FactorB;
    vec2 uniScale = FactorScale;

    vec2 dt = vec2(0.0, 0.0);
    // Shake frequency
    dt.x = sin(progress / 4.0 * uniA.x + uniB.x) * uniScale.x;
    dt.y = cos(progress / 4.0 * uniA.y + uniB.y) * uniScale.y;

    gl_FragColor = texture2D(inputImageTexture, uv+dt);
}
