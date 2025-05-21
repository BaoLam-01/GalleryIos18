precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform lowp float progress;
float ratio = 1.0;

vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}

//uniform vec4 bgcolor = vec4(0.0, 0.0, 0.0, 1.0) ; // = vec4(0.0, 0.0, 0.0, 1.0)


vec4 transition(vec2 p) {
    vec2 ratio2 = vec2(1.0, 1.0 / ratio);
    float s = pow(2.0 * abs(progress - 0.5), 3.0);

    float dist = length((vec2(p) - 0.5) * ratio2);
    return mix(
    progress < 0.5 ? getFromColor(p) : getToColor(p), // branching is ok here as we statically depend on progress uniform (branching won't change over pixels)
    vec4(0.0, 0.0, 0.0, 1.0),
    step(s, dist)
    );
}

void main() {
    gl_FragColor = transition(textureCoordinate);
}
