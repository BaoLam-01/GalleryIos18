precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;
uniform sampler2D inputImageTexture;
vec3 iResolution = vec3(1.0);

vec2 crt_coords(vec2 uv, float bend)
{
    uv -= 0.5;
    uv *= 2.;
    uv.x *= 1. + pow(abs(uv.y)/bend, 2.);
    uv.y *= 1. + pow(abs(uv.x)/bend, 2.);

    uv /= 2.5;
    return uv + .5;
}

// add border
float vignette(vec2 uv, float size, float smoothness, float edgeRounding)
{
    uv -= .5;
    uv *= size;
    float amount = sqrt(pow(abs(uv.x), edgeRounding) + pow(abs(uv.y), edgeRounding));
    amount = 1. - amount;
    return smoothstep(0., smoothness, amount);
}

float scanline(vec2 uv, float lines, float speed)
{
    return sin(uv.y * lines + progress * speed);
}

float random(vec2 uv)
{
    return fract(sin(dot(uv, vec2(15.5151, 42.2561))) * 12341.14122 * sin(progress * 0.03));
}

float noise(vec2 uv)
{
    vec2 i = floor(uv);
    vec2 f = fract(uv);

    float a = random(i);
    float b = random(i + vec2(1., 0.));
    float c = random(i + vec2(0., 1.));
    float d = random(i + vec2(1.));

    vec2 u = smoothstep(0., 1., f);

    return mix(a, b, u.x) + (c - a) * u.y * (1. - u.x) + (d - b) * u.x * u.y;
}

void main() {
    vec2 uv = textureCoordinate/iResolution.xy;

    vec2 crt_uv = crt_coords(uv, 4.);

//    float s1 = scanline(uv, 200., -10.);
    float s2 = scanline(uv, 20., -3.);

    vec4 col;
    //    col.r = texture2D(inputImageTexture, crt_uv + vec2(0.0, 0.0)).r;
    //    col.g = texture2D(inputImageTexture, crt_uv).r;
    //    col.b = texture2D(inputImageTexture, crt_uv + vec2(0.0, 0.0)).b;
    //    col.a = texture2D(inputImageTexture, crt_uv).a;
    col = texture2D(inputImageTexture, crt_uv);

    col = mix(col, vec4(s2), 0.05);
    gl_FragColor = mix(col, vec4(noise(uv * 75.)), 0.05) * vignette(uv, 1.9, .6, 8.);
}
