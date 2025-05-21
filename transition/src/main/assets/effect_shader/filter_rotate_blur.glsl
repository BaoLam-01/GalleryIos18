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


const float PI = 3.14159265;

const float uD = 80.0;
const float uR = 1.0;

vec4 transition(vec2 uv) {
    return mix(
    getFromColor(uv),
    getToColor(uv),
    progress
    );
}


void main() {
    float rate= 2264.0 / 1080.0;
    ivec2 ires = ivec2(128, 128);
    float res = float(ires.s);
    //周期
    float duration = 1.0;
    vec2 st = textureCoordinate;
    float radius = res * uR;
    //进度
    if(progress>0.0){
        float progressed = mod(progress, duration) / duration;// 0~1
        vec2 xy = res * st;

        vec2 dxy = xy - vec2(res/2., res/2.);
        float r = length(dxy);

        //(1.0 - r/Radius);
        float beta = atan(dxy.y, dxy.x) + radians(uD) * 2.0 * (-(r/radius)*(r/radius) + 1.0);

        vec2 xy1 = xy;
        if (r<=radius)
        {
            xy1 = res/2. + r*vec2(cos(beta), sin(beta))*progressed;

        }

        st = xy1/res;

        vec3 irgb = texture2D(inputImageTexture, st).rgb;

        gl_FragColor = vec4(irgb, 1.0);
    }else{
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    }
}
