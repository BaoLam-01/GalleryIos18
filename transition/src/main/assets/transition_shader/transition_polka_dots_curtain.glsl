precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;


vec4 getFromColor(vec2 uv){
    return texture2D(inputImageTexture, uv);
}

vec4 getToColor(vec2 uv){
    return texture2D(inputImageTexture2, uv);
}

vec2 zoom(vec2 uv, float amount) {
    return 0.5 + ((uv - 0.5) * (1.0-amount));
}

const float SQRT_2 = 1.414213562373;
//uniform float dots;// = 20.0;
//uniform vec2 center;// = vec2(0, 0);

vec4 transition(vec2 uv) {
    bool nextImage = distance(fract(uv * 20.0), vec2(0.5, 0.5)) < ( progress / distance(uv, vec2(0, 0)));
    return nextImage ? getToColor(uv) : getFromColor(uv);
}


void main() {
    gl_FragColor = transition(textureCoordinate);
}
