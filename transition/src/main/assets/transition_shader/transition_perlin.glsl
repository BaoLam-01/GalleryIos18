precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform lowp float progress;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}

float scale=4.0;// = 4.0
float smoothness=0.01;// = 0.01

float seed=12.9898;// = 12.9898

// http://byteblacksmith.com/improvements-to-the-canonical-one-liner-glsl-rand-for-opengl-es-2-0/
float random(vec2 co)
{
    highp float a = seed;
    highp float b = 78.233;
    highp float c = 43758.5453;
    highp float dt= dot(co.xy, vec2(a, b));
    highp float sn= mod(dt, 3.14);
    return fract(sin(sn) * c);
}

// 2D Noise based on Morgan McGuire @morgan3d
// https://www.shadertoy.com/view/4dS3Wd
float noise (in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    // Four corners in 2D of a tile
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    // Smooth Interpolation

    // Cubic Hermine Curve.  Same as SmoothStep()
    vec2 u = f*f*(3.0-2.0*f);
    // u = smoothstep(0.,1.,f);

    // Mix 4 coorners porcentages
    return mix(a, b, u.x) +
    (c - a)* u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

vec4 transition (vec2 uv) {
    vec4 from = getFromColor(uv);
    vec4 to = getToColor(uv);
    float n = noise(uv * scale);

    float p = mix(-smoothness, 1.0 + smoothness, progress);
    float lower = p - smoothness;
    float higher = p + smoothness;

    float q = smoothstep(lower, higher, n);

    return mix(
    from,
    to,
    1.0 - q
    );
}

void main() {
    if(progress>0.0){
        gl_FragColor = transition(textureCoordinate);
    }else{
        gl_FragColor = getFromColor(textureCoordinate);
    }
}
