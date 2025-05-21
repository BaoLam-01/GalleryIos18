precision mediump float;

varying mediump vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float progress;
vec3 iResolution = vec3(1.0);


void mainImage( out vec4 O, vec2 U )
{
    vec2 R = iResolution.xy; U = (U+U-R)/R.y;
    U = vec2(atan(U.y,U.x)*3./3.1416,log(length(U))); // conformal polar
    // multiply U for smaller tiles
    U.y += U.x/6.; // comment for concentric circles instead of spiral
    O = texture2D(inputImageTexture, fract(-U+progress));
}



void main() {
    mainImage(gl_FragColor, textureCoordinate*iResolution.xy);
}

