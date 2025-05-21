precision mediump float;

varying mediump vec2 textureCoordinate;
uniform lowp float progress;

uniform sampler2D inputImageTexture;

uniform float zoom_quickness;// = 0.8
//float nQuick = clamp(zoom_quickness,0.2,1.0);
float nQuick = clamp(0.8, 0.2, 1.0);

vec2 zoom(vec2 uv, float amount) {
    return 0.5 + ((uv - 0.5) * (1.0-amount));
}

void main() {
    gl_FragColor = texture2D(inputImageTexture, zoom(textureCoordinate, smoothstep(0.0, 0.5, progress)));
}
