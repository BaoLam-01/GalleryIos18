precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

const float noiseLevel = 0.3;

const float distortion1 = 1.76;

const float distortion2 = 3.61;

const float speed = 0.42;

//const float scroll = 0.0;

const float scanLineThickness = 18.6;

const float scanLineIntensity = 0.4;

const float scanLineOffset = 0.43;

// Start Ashima 2D Simplex Noise

const vec4 C = vec4(0.211324865405187,0.366025403784439,-0.577350269189626,0.024390243902439);

vec3 mod289(vec3 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
    return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)    {
    vec2 i  = floor(v + dot(v, C.yy) );
    vec2 x0 = v -   i + dot(i, C.xx);

    vec2 i1;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;

    i = mod289(i); // Avoid truncation effects in permutation
    vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))+ i.x + vec3(0.0, i1.x, 1.0 ));

    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
    m = m*m ;
    m = m*m ;

    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;

    m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

// End Ashima 2D Simplex Noise

const float tau = 6.28318530718;

//  use this pattern for scan lines

vec2 pattern(vec2 pt) {
    float s = 0.0;
    float c = 1.0;
    vec2 tex = pt * vec2(0.0, 0.0);
    vec2 point = vec2( c * tex.x - s * tex.y, s * tex.x + c * tex.y ) * (1.0/scanLineThickness);
    float d = point.y;

    return vec2(sin(d + scanLineOffset * tau + cos(pt.x * tau)), cos(d + scanLineOffset * tau + sin(pt.y * tau)));
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    vec2 uv = textureCoordinate;
    float time = (progress + 5.0)*100.0/105.0;
    float ty = time*speed;
    float yt = uv.y - ty;
    float scroll = progress * 0.0085;

    //smooth distortion
    float offset = snoise(vec2(yt*3.0,0.0))*0.2;
    // boost distortion
    offset = pow( offset*distortion1,3.0)/max(distortion1,0.001);
    //add fine grain distortion
    offset += snoise(vec2(yt*50.0,0.0))*distortion2*0.001;
    //combine distortion on X with roll on Y
    vec2 adjusted = vec2(fract(uv.x + offset),fract(uv.y-scroll) );
    vec4 result = texture2D(inputImageTexture, adjusted);
    vec2 pat = pattern(adjusted);
    vec3 shift = scanLineIntensity * vec3(0.3 * pat.x, 0.59 * pat.y, 0.11) / 2.0;
    result.rgb = (1.0 + scanLineIntensity / 2.0) * result.rgb + shift + (rand(adjusted * time) - 0.5) * noiseLevel;
    gl_FragColor = result;

}
