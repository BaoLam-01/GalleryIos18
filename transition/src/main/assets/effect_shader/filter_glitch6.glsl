precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

vec3 iResolution = vec3(1.0);


#define RGBSHIFT
#define OLDSCREENLINES
#define NUMBER_LINES 269.

vec4 rgbShift(in vec2 p, in vec4 shift) {
    shift *= 1.02*shift.w - 1.0;
    vec2 rs = vec2(shift.x, -shift.y);
    vec2 gs = vec2(shift.y, -shift.z);
    vec2 bs = vec2(shift.z, -shift.x);

    //scanline kinda
    #if 1
    //vec2 rip = vec2(pow(sin(progress),5.)*sin(p.y*100.+progress*10.),0);
    float slsp=1.3;
    vec2 rip = vec2(
    clamp(sin(slsp*progress), 0., 1.)*
    clamp(sin(slsp*progress*3.), 0., 1.)*
    clamp(sin(slsp*progress*17.), 0., 1.)*
    clamp(sin(slsp*progress*13.), 0., 1.)
    *sin(p.y*600.*sin(progress)+progress*10.), 0);
    p+=rip;
    #endif

    //    float r = texture(inputImageTexture, p+rs/2., 0.0).x;
    //    float g = texture(inputImageTexture, p+gs/2., 0.0).y;
    //    float b = texture(inputImageTexture, p+bs/2., 0.0).z;
    float r = texture2D(inputImageTexture, p+rs/2.).x;
    float g = texture2D(inputImageTexture, p+gs/2.).y;
    float b = texture2D(inputImageTexture, p+bs/2.).z;


    return vec4(r, g, b, 1.0)/3.;
}

float rand(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.98, 78.23))) * 43758.54);
}

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    float numberLines = 269.;
    vec2 uv = fragCoord.xy / iResolution.xy;
    vec3 oldScreenLines = vec3(sin(uv.y*NUMBER_LINES+sin(progress)*20.));

    #ifdef RGBSHIFT
    vec3 col = mix(rgbShift(uv, vec4(0.015, 0.0, 0.015, 0.0)).xyz, oldScreenLines, 0.01);
    #else
    vec3 col = texture(inputImageTexture, uv).xyz;
    #endif

    //edge detection glitch
    #if 1
    vec2 ofs = vec2(0.01, 0);
    float edsp = 1.;
    float fac = clamp(sin(edsp*progress*5.-0.6), 0., 1.)*
    clamp(sin(edsp*progress*7.-0.3), 0., 1.);
    col *= 1.-fac;////
    col += 0.01*
    fac*(texture2D(inputImageTexture, uv+ofs.xy).xyz - texture2D(inputImageTexture, uv-ofs.xy).xyz + texture2D(inputImageTexture, uv+ofs.yx).xyz - texture2D(inputImageTexture, uv-ofs.yx).xyz)/length(ofs);
    #endif

    float vign = pow(1.35-length(uv-vec2(0.5)), 3.);
    col -= .028*rand(uv.xy * progress);
    fragColor = vec4(col*2.5, 1.)*vign;
}

void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}