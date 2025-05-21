precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;
uniform sampler2D inputImageTexture;
vec3 iResolution = vec3(1.0);

vec4 butterfly(vec2 uv, float time) {
    uv-= 0.5;
    uv*= 1.5;
    uv.x*=1.5;
    uv.x *= 1.0 -sin(time*4.)*.3;
    vec2 p =uv*15.0;

    float r = length(p);
    float t = atan(p.y, p.x);

    float b =
    7. - 0.5*sin( 1.*t)
    + 2.5*sin( 3.*t)
    + 2.0*sin( 5.*t)
    - 1.7*sin( 7.*t)
    + 3.0*cos( 2.*t)
    - 2.0*cos( 4.*t)
    - 0.4*cos(16.*t) - r;

    vec2 g = normalize(p);
    vec2 c = g*smoothstep(-2.0, 0.0, b);
    c*=smoothstep(4.0,-3.0, b);
    return vec4(c, 0.0, b);
}


void main() {
    vec2 uv = textureCoordinate.xy / iResolution.xy;
    vec4 b = butterfly(1.0 - uv, progress * 5.0);
    gl_FragColor = texture2D(inputImageTexture,uv)*(1.0-b*.4)+smoothstep(-1.,0.0, b.a)*0.2;
}
