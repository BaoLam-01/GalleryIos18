precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

vec3 iResolution = vec3(1.0);

vec3 rainbow(float h) {
    h = mod(mod(h, 1.0) + 1.0, 1.0);
    float h6 = h * 6.0;
    float r = clamp(h6 - 4.0, 0.0, 1.0) +
    clamp(2.0 - h6, 0.0, 1.0);
    float g = h6 < 2.0
    ? clamp(h6, 0.0, 1.0)
    : clamp(4.0 - h6, 0.0, 1.0);
    float b = h6 < 4.0
    ? clamp(h6 - 2.0, 0.0, 1.0)
    : clamp(6.0 - h6, 0.0, 1.0);
    return vec3(r, g, b);
}

vec3 plasma(vec2 fragCoord)
{
    const float speed = 8.0;

    const float scale = 1.5;

    const float startA = 163.0 / 512.0;
    const float startB = 233.0 / 512.0;
    const float startC = 4325.0 / 512.0;
    const float startD = 312556.0 / 512.0;

    const float advanceA = 2.34 / 512.0 * 18.2 * speed;
    const float advanceB = 2.98 / 512.0 * 18.2 * speed;
    const float advanceC = 5.46 / 512.0 * 18.2 * speed;
    const float advanceD = 3.72 / 512.0 * 18.2 * speed;

    vec2 uv = fragCoord * scale / iResolution.xy;

    float a = startA + progress * advanceA;
    float b = startB + progress * advanceB;
    float c = startC + progress * advanceC;
    float d = startD + progress * advanceD;

    float n = sin(a + 3.0 * uv.x) +
    sin(b - 4.0 * uv.x) +
    sin(c + 2.0 * uv.y) +
    sin(d + 5.0 * uv.y);

    n = mod(((4.0 + n) / 4.0), 1.0);

    vec2 tuv = fragCoord.xy / iResolution.xy;
    n += texture2D(inputImageTexture, tuv).r;

    return rainbow(n);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec3 green = vec3(0.12, 0.2, 0.306);
    vec2 uv = fragCoord.xy / iResolution.xy;
    vec3 cai = texture2D(inputImageTexture, uv).rgb;
    float greenness = 1.0 - (length(cai - green) / length(vec3(1, 1, 1)));
    float caiAlpha = clamp((greenness - 0.7) / 0.2, 0.0, 1.0);
    fragColor = vec4(cai * (1.0 - caiAlpha), 1.0) + vec4(plasma(fragCoord) * caiAlpha, 1.0);
}

void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}

