precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;

float random(in vec3 scale, in float seed) {
    return fract(sin(dot(vec3(0.78,1.45,6.98) + seed, scale)) * 43758.5453 + seed);
}

void main() {
    vec2 uv = textureCoordinate;

    float time = progress + 10.0*random(vec3(4.56, 2.34, 6.78), progress);
    time = progress;

    //    float freq = 1.0*sin(0.5*progress) + 2.0;
    //    vec2 warp = 0.5000*sin( uv.xy*1.0*freq + vec2(0.0,1.0) + 1.0*progress ) +
    //    0.2500*cos( uv.yx*3.1*freq + vec2(1.0,2.0) + 1.1*progress) +
    //    0.1250*sin( uv.xy*5.2*freq + vec2(5.0,3.0) + 1.2*progress ) +
    //    0.1625*cos( uv.yx*7.3*freq + vec2(3.0,4.0) + 0.9*progress );

    float freq = 3.0*sin(0.5*progress);
    vec2 warp = 0.5000*cos( uv.xy*1.0*freq + vec2(0.0,1.0) + progress ) +
    0.2500*cos( uv.yx*2.3*freq + vec2(1.0,2.0) + progress) +
    0.1250*cos( uv.xy*4.1*freq + vec2(5.0,3.0) + progress ) +
    0.0625*cos( uv.yx*7.9*freq + vec2(3.0,4.0) + progress );

    vec2 st = (uv - 0.5)*1.0 + 0.5;
    st += warp*0.7;
    st += 1.0;
    st *= 0.5;
    st = fract(st);
    st = abs((st-0.5))*2.0 ;

    gl_FragColor = texture2D(inputImageTexture, st);
}

