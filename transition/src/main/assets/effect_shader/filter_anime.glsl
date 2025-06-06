precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;

vec3 iResolution = vec3(1.0);


const vec3 RADIAL_COLOR = vec3(1., 1., 1.);
#define ANIMATE 10.0
#define INV_ANIMATE_FREQ 0.01
#define RADIUS 1.3
#define FREQ 10.0
#define LENGTH 2.0
#define SOFTNESS 0.1
#define WEIRDNESS 0.1

#define ASPECT_AWARE 1.

#define lofi(x, d) (floor((x)/(d))*(d))

float hash(vec2 v) {
    return fract(sin(dot(v, vec2(89.44, 19.36))) * 22189.22);
}

float iHash(vec2 v, vec2 r) {
    vec4 h = vec4(
    hash(vec2(floor(v * r + vec2(0.0, 0.0)) / r)),
    hash(vec2(floor(v * r + vec2(0.0, 1.0)) / r)),
    hash(vec2(floor(v * r + vec2(1.0, 0.0)) / r)),
    hash(vec2(floor(v * r + vec2(1.0, 1.0)) / r))
    );
    vec2 ip = vec2(smoothstep(
    vec2(0.0),
    vec2(1.0),
    mod(v * r, 1.0))
    );
    return mix(
    mix(h.x, h.y, ip.y),
    mix(h.z, h.w, ip.y),
    ip.x
    );
}

float noise(vec2 v) {
    float sum = 0.0;
    for (int i = 1; i < 7; i ++) {
        sum += iHash(
        v + vec2(i),
        vec2(2.0 * pow(2.0, float(i)))) / pow(2.0, float(i)
        );
    }
    return sum;
}

// Memix boilerplate getuv function
vec2 getuv(in vec2 p) { return vec2(0.5+(p.x-0.5)*1.0, p.y); }

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    #ifdef ASPECT_AWARE
    vec2 uv = (fragCoord.xy * 2.0 - iResolution.xy) / iResolution.xy;
    #else
    vec2 uv = (fragCoord.xy * 2.0 - iResolution.xy) / iResolution.y;
    #endif
    vec2 puv = vec2(
    WEIRDNESS * length(uv) + ANIMATE * lofi(progress, INV_ANIMATE_FREQ),
    FREQ * atan(uv.y, uv.x)
);
float value = noise(puv);
float ratius = 0.3;
value = length(uv) - RADIUS - LENGTH * (value - ratius);
value = smoothstep(-SOFTNESS, SOFTNESS, value);

vec4 tex = texture2D(inputImageTexture, getuv(fragCoord.xy / iResolution.xy));
vec3 color = mix(tex.xyz, RADIAL_COLOR, value);
fragColor = vec4(color, 1.0);
}


void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}

