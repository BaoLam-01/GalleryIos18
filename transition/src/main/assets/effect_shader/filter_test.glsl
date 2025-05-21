precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;
uniform sampler2D inputImageTexture;
vec3 iResolution = vec3(1.0);

float mask(vec2 p, float r, float e, float s)
{
    float fac = max(0.0, r - length(p));
    return mix(0.0, 1.0, clamp(pow(fac, e) * s, 0.0, 1.0));
}

float noise(vec2 uv)
{
    return texture2D(inputImageTexture, uv).x;
}

vec3 fade(vec3 color, float fac)
{
    float l = length(color) + 0.001;
    vec3 c = color / l;
    return mix(c, vec3(1.0), fac) * l;
}

void main()
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = textureCoordinate/iResolution.xy;

    vec2 p = uv * 2.0 - 1.0;
    p *= vec2(iResolution.x / iResolution.y, 1.0);

    float a = atan(p.y, p.x) / atan(0.0, -1.0) / 2.0;
    float v = length(p);

    float d0 = noise(vec2(a * 1.0, v * 0.2));
    float d1 = noise(vec2(a * 6.5, v * 0.1));

    float proc = mod(iTime, 8.0);

    float m = mask(p / pow(proc, 0.2), 0.3 + d0 * 0.3 + d1 * 0.06, 0.5, 5.0);

    vec3 col = fade(texture2D(inputImageTexture, uv).xyz, 0.7) * vec3(0.7, 0.66, 0.59);

    // Output to screen
    glFragColor =  vec4(col,1.0) * m;
}
