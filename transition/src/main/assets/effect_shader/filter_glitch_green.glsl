precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;
vec2 inputSize = vec2(1.0);
float rand(highp vec2 co) {
    return fract(sin(mod(dot(co.xy ,vec2(12.9898,78.233)),3.14))*4.54);
//    return fract(sin(mod(dot(co.xy ,vec2(12.9898,78.233)),3.14))*43758.54);
}

void main() {
    highp vec2 uv = textureCoordinate;

    float lum = cos(uv.y * inputSize.y);
    lum*=lum;
    lum/=3.;
    lum+=0.6+rand(uv*progress)/6.;

    float col = dot(texture2D(inputImageTexture,uv).rgb,vec3(0.65,0.3,0.1)*lum);

    gl_FragColor = vec4(0,col,0,1.)*smoothstep(0.9,0.,distance(uv,vec2(0.5)));
}
