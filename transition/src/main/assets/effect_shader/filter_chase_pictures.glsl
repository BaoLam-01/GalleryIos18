precision mediump float;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
uniform lowp float progress;
void main() {
    highp vec2 uv = textureCoordinate;
    float t = progress/0.3;
    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
    float alpha = textureColor.w;
    highp vec3 txt = 1.0 - textureColor.rgb;
    if (uv.x<.33) {
        t = mod(4.*t,2.);        // left: for reference, slow time-alterning
        if (t > 1.){
            gl_FragColor = textureColor;
        } else {
            gl_FragColor = vec4(txt, alpha);
        }
    } else {

        if (uv.x<.66) {
            t =  mod(5.*t,3.);
            if (t > 2.){
                gl_FragColor = vec4(txt, alpha);
            } else{
                gl_FragColor = textureColor;
            }
        } else {                // right: time-dithering without gamma
            t =  mod(6.*t,2.);
            if (t > 1.){
                gl_FragColor = textureColor;
            } else {
                gl_FragColor = vec4(txt, alpha);
            }

        }
    }
}
