#extension GL_OES_standard_derivatives : enable

precision mediump float;
uniform vec2 iResolution;
uniform vec2 iMouse;
uniform float iTime;

float sdfRect(vec2 center, vec2 size, vec2 p, float r)
{
    vec2 p_rel = p - center;
    vec2 q = abs(p_rel) - size;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

vec3 getNormal(float sd, float thickness)
{
    float dx = dFdx(sd);
    float dy = dFdy(sd);
    float n_cos = max(thickness + sd, 0.0) / thickness;
    float n_sin = sqrt(1.0 - n_cos * n_cos);
    return normalize(vec3(dx * n_cos, dy * n_cos, n_sin));
}

float height(float sd, float thickness)
{
    if(sd >= 0.0) return 0.0;
    if(sd < -thickness) return thickness;
    float x = thickness + sd;
    return sqrt(thickness * thickness - x * x);
}

vec4 bg(vec2 uv)
{
    if(uv.x > 0.5)
    return vec4(0.0, 0.5, 1.0, 1.0);
    return vec4(0.9, 0.9, 0.9, 1.0);
}

void main()
{
    vec2 fragCoord = gl_FragCoord.xy;
    vec2 uv = fragCoord / iResolution;

    float thickness = 14.0;
    float index = 1.5;
    float base_height = thickness * 8.0;
    float color_mix = 0.3;
    vec4 color_base = vec4(1.0, 1.0, 1.0, 1.0);

    vec2 center = iMouse;
    if(center == vec2(0.0, 0.0))
    {
        center = iResolution * 0.5;
    }

    float sd = sdfRect(center, vec2(128.0, 0.0), fragCoord, 64.0);
    vec4 bg_col = mix(vec4(0.0), bg(uv), clamp(sd / 100.0, 0.0, 1.0) * 0.1 + 0.9);
    bg_col.a = smoothstep(-4., 0., sd);

    vec3 normal = getNormal(sd, thickness);
    vec3 incident = vec3(0.0, 0.0, -1.0);
    vec3 refract_vec = refract(incident, normal, 1.0/index);
    float h = height(sd, thickness);
    float refract_length = (h + base_height) / dot(vec3(0.0, 0.0, -1.0), refract_vec);
    vec2 coord1 = fragCoord + refract_vec.xy * refract_length;
    vec4 refract_color = bg(coord1 / iResolution);

    vec3 reflect_vec = reflect(incident, normal);
    float c = clamp(abs(reflect_vec.x - reflect_vec.y), 0.0, 1.0);
    vec4 reflect_color = vec4(c, c, c, 1.0);

    vec4 finalColor = mix(mix(refract_color, reflect_color, (1.0 - normal.z) * 2.0),
    color_base, color_mix);

    finalColor = clamp(finalColor, 0.0, 1.0);
    bg_col = clamp(bg_col, 0.0, 1.0);
    gl_FragColor = mix(finalColor, bg_col, bg_col.a);
}

