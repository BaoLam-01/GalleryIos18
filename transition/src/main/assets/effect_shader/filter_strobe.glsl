precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform lowp float progress;

void main() {
    highp vec2 uv = textureCoordinate;
    int i = int(mod(progress/0.0333,3.));
    highp float t = float(i)/40.;

    vec4 col = texture2D(inputImageTexture, uv);
    col.r += t;
    col.g += t;
    col.b += t;
    gl_FragColor = clamp(col,0.0,1.0);
}
