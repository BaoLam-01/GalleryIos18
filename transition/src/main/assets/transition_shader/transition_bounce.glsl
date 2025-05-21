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

vec4 shadow_colour=vec4(0.0, 0.0, 0.0, 0.6);// = vec4(0.,0.,0.,.6)
float shadow_height=0.075;// = 0.075
float bounces=3.0;// = 3.0

const float PI = 3.14159265358;

vec4 transition (vec2 uv) {
    float time = progress;
    float stime = sin(time * PI / 2.);
    float phase = time * PI * bounces;
    float y = (abs(cos(phase))) * (1.0 - stime);
    float d = uv.y - y;
    return mix(
    mix(
    getToColor(uv),
    shadow_colour,
    step(d, shadow_height) * (1. - mix(
    ((d / shadow_height) * shadow_colour.a) + (1.0 - shadow_colour.a),
    1.0,
    smoothstep(0.95, 1., progress)// fade-out the shadow at the end
    ))
    ),
    getFromColor(vec2(uv.x, uv.y + (1.0 - y))),
    step(d, 0.0)
    );
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
