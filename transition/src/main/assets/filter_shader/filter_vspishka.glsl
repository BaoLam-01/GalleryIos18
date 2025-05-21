precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;
vec3 iResolution = vec3(1.0);

void mainImage(out vec4 O, vec2 U) {
    O =  ( texture2D(inputImageTexture, U/iResolution.xy) - .5 )
    *  cos( 6.3*clamp(0., 0.5, clamp(0., 0.25, fract(progress*1.5))) + vec4(0,23,21,0) ) + .5;
}

void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}